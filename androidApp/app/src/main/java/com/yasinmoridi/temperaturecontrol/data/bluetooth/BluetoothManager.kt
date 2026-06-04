package com.yasinmoridi.temperaturecontrol.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import com.yasinmoridi.temperaturecontrol.data.dataClass.SensorData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

/**
 * Manages Bluetooth Low Energy (BLE) scanning and TCP socket communication with the hardware node.
 * This class handles device discovery, socket connection, and data exchange (JSON format).
 */
class BluetoothManager(private val context: Context) {

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    
    // Server configuration: Use 10.0.2.2 for Android Emulator to access host machine
    private val HOST = "10.27.117.183" // Emulator IP: "10.0.2.2", Physical Phone IP example: "10.27.117.183"
    private val PORT = 5000

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // SharedFlow to emit real-time sensor data updates to subscribers
    private val _sensorData = MutableSharedFlow<Result<SensorData>>(replay = 1)
    val sensorData = _sensorData.asSharedFlow()

    private var isConnecting = false

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    
    // Internal map to track unique devices discovered during scan, avoiding duplicates
    private val _discoveredDevicesMap = MutableStateFlow<Map<String, FoundDevice>>(emptyMap())
    
    /**
     * Exposes the list of discovered BLE devices as a StateFlow.
     */
    val discoveredDevices = _discoveredDevicesMap.map { it.values.toList() }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Model representing a BLE device found during scanning.
     */
    data class FoundDevice(val name: String, val address: String, val rssi: Int)

    /**
     * Starts a 10-second BLE scan to discover nearby devices.
     * Clears the previous list before starting.
     */
    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        val bleScanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        _discoveredDevicesMap.value = emptyMap()
        
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                val name = device.name ?: "Unknown Device"
                val address = device.address
                
                val currentMap = _discoveredDevicesMap.value.toMutableMap()
                currentMap[address] = FoundDevice(name, address, result.rssi)
                _discoveredDevicesMap.value = currentMap
            }
        }

        scope.launch {
            bleScanner.startScan(scanCallback)
            delay(10000) // Scan for 10 seconds
            bleScanner.stopScan(scanCallback)
        }
    }

    /**
     * Establishes a TCP socket connection and maintains a persistent listening loop.
     * Automatically attempts to reconnect if the connection is lost.
     */
    fun startConnection() {
        if (isConnecting) return
        isConnecting = true
        scope.launch {
            while (true) {
                try {
                    socket = Socket(HOST, PORT)
                    writer = PrintWriter(OutputStreamWriter(socket?.getOutputStream()), true)
                    val reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                    
                    while (true) {
                        val line = reader.readLine() ?: break
                        val data = parseLine(line)
                        if (data != null) _sensorData.emit(Result.success(data))
                    }
                } catch (e: Exception) {
                    _sensorData.emit(Result.failure(e))
                    delay(3000) // Wait before attempting reconnection
                } finally {
                    socket?.close()
                    socket = null
                    writer = null
                }
            }
        }
    }

    /**
     * Wrapper to start connection and return the sensor data flow.
     */
    fun connectAndReceive(): Flow<Result<SensorData>> {
        startConnection()
        return sensorData
    }

    /**
     * Sends a new temperature threshold to the remote node via the active socket.
     */
    suspend fun sendThreshold(threshold: Int) {
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply { put("threshold", threshold) }
                writer?.println(json.toString())
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    /**
     * Sends a fan control command (manual override) to the remote node.
     */
    suspend fun sendFanControl(isOn: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply { put("fan_control", if (isOn) 1 else 0) }
                writer?.println(json.toString())
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    /**
     * Closes the active socket connection and stops the reconnection attempts.
     */
    fun disconnect() {
        try { socket?.close() } catch (e: Exception) { e.printStackTrace() }
        socket = null
        writer = null
        isConnecting = false
    }

    /**
     * Parses a JSON string received from the hardware node into a SensorData object.
     */
    private fun parseLine(line: String): SensorData? {
        return try {
            val json = JSONObject(line.trim())
            SensorData(
                temperature = json.getDouble("temp").toFloat(),
                fanOn = json.getInt("fan") == 1
            )
        } catch (e: Exception) { null }
    }
}

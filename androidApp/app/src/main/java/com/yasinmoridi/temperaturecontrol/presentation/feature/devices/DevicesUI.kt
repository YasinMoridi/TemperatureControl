package com.yasinmoridi.temperaturecontrol.presentation.feature.devices

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.*
import com.yasinmoridi.temperaturecontrol.presentation.core.UiStrings
import org.koin.androidx.compose.koinViewModel

/**
 * Screen for managing Bluetooth connections and scanning for new devices.
 */
@Composable
fun DevicesUI(
    vm: DevicesVM = koinViewModel()
) {
    val isBluetoothEnabled by vm.isBluetoothEnabled.collectAsState()
    val devices by vm.devices.collectAsState()
    val isScanning by vm.isScanning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp)
    ) {
        // Header Section: Displays screen title and instruction
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = UiStrings.DEVICES_TITLE,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = UiStrings.DEVICES_SUBTITLE,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Bluetooth Toggle Card: Main switch to enable/disable Bluetooth radio
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceColor)
                .border(1.dp, GlassWhite, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyanHighlight.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = UiStrings.BT_LABEL,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = UiStrings.BT_SUBTITLE,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Switch(
                checked = isBluetoothEnabled,
                onCheckedChange = { vm.toggleBluetooth(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentCyan,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Slate500.copy(alpha = 0.5f),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Device List Header: Scan button and section title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = UiStrings.MY_DEVICES_TITLE,
                color = Slate300,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            
            TextButton(
                onClick = { vm.startScan() },
                enabled = !isScanning,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = UiStrings.SCANNING_LABEL,
                            color = AccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = AccentCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = UiStrings.BTN_SCAN,
                            color = AccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // List of found and paired devices
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(devices) { device ->
                DeviceItem(device, onAction = {
                    if (device.isConnected) vm.disconnectDevice(device) else vm.connectDevice(device)
                })
            }
        }
    }
}

/**
 * Individual device card component.
 */
@Composable
fun DeviceItem(device: Device, onAction: () -> Unit) {
    val icon = when (device.type) {
        DeviceType.BLUETOOTH -> Icons.Rounded.Bluetooth
        DeviceType.SMARTPHONE -> Icons.Rounded.Smartphone
        DeviceType.WATCH -> Icons.Rounded.Watch
    }

    val isTargetDevice = device.name == UiStrings.DEVICE_NAME
    
    val itemBackground = if (device.isConnected) AccentCyan.copy(alpha = 0.05f) else SurfaceColor
    val itemBorder = if (device.isConnected) AccentCyan.copy(alpha = 0.3f) else GlassWhite

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(itemBackground)
            .border(1.dp, itemBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Device Icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (device.isConnected) AccentCyan.copy(alpha = 0.2f) else Slate800),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (device.isConnected) AccentCyan else Slate400,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        // Device Info: Name and Signal Strength
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Text(
                text = "${UiStrings.SIGNAL_PREFIX}${device.signal}",
                color = Slate500,
                fontSize = 12.sp
            )
        }
        
        // Dynamic Action Button based on connection status
        val buttonText = if (isTargetDevice) {
            if (device.isConnected) UiStrings.BTN_DISCONNECT else UiStrings.BTN_CONNECT
        } else {
            UiStrings.BTN_PAIR
        }
        
        val buttonBg = if (isTargetDevice) {
            if (device.isConnected) AccentCyan.copy(alpha = 0.2f) else Slate700
        } else {
            Slate800
        }
        
        val buttonContentColor = if (isTargetDevice) {
            if (device.isConnected) AccentCyan else Slate300
        } else {
            Slate400
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(buttonBg)
                .clickable { onAction() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buttonText,
                color = buttonContentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

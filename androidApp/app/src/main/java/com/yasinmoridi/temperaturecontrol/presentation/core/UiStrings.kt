package com.yasinmoridi.temperaturecontrol.presentation.core

object UiStrings {

    // ▪ General messages
    const val ERROR_PREFIX = "Error: "
    
    // ▪ Splash Screen
    const val SPLASH_TITLE_PART1 = "Thermo"
    const val SPLASH_TITLE_PART2 = "Core"
    const val SPLASH_INIT_TEXT = " SYSTEM INITIALIZATION "
    const val SPLASH_LOADING_TEXT = "CONNECTING TO ATMEGA16..."

    // ▪ Settings
    const val SETTINGS_TITLE = "Settings"
    const val SETTINGS_SUBTITLE = "Preferences and configurations"
    const val SETTINGS_CORE_TITLE = "Core Settings"
    const val SETTINGS_THRESHOLD_LABEL = "Critical Threshold"
    const val SETTINGS_THRESHOLD_DESC = "Fan activates automatically above this limit"
    const val SETTINGS_PUSH_TITLE = "Push Notifications"
    const val SETTINGS_PUSH_SUBTITLE = "Alerts on threshold breach"
    const val SETTINGS_DARK_MODE_TITLE = "Dark Mode"
    const val SETTINGS_DARK_MODE_SUBTITLE = "System matches dark theme"
    const val SETTINGS_POWER_SAVING_TITLE = "Power Saving"
    const val SETTINGS_POWER_SAVING_SUBTITLE = "Reduces BLE poll rate"
    const val SETTINGS_DEVICE_INFO_TITLE = "Device Info"
    const val SETTINGS_FIRMWARE_LABEL = "Firmware Version"
    const val SETTINGS_MAC_LABEL = "MAC Address"
    const val SETTINGS_CALIBRATED_LABEL = "Last Calibrated"
    const val BTN_RESET_FACTORY = "Reset Factory Settings"
    
    // Settings Mocks/Constants
    const val MOCK_FIRMWARE = "v2.1.4-beta"
    const val MOCK_MAC = "00:1B:44:11:3A:B7"
    const val MOCK_CALIBRATED = "12 Days Ago"
    const val RANGE_MIN_TEMP = "20°C"
    const val RANGE_MAX_TEMP = "45°C"

    // ▪ Dashboard
    const val DASHBOARD_TITLE = "ThermoCore"
    const val DASHBOARD_SUBTITLE = "Smart Fan Controller"
    const val STATUS_CONNECTED = "Connected"
    const val STATUS_CONNECTED_BLE = "Connected via BLE"
    const val STATUS_DISCONNECTED = "Disconnected"
    const val STATUS_CONNECTING = "Connecting..."
    const val BTN_DISCONNECT = "Disconnect"
    const val BTN_CONNECT = "Connect"
    const val CURRENT_TEMP_LABEL = "CURRENT TEMP"
    const val ALERT_HIGH_TEMP_TITLE = "High Temperature Alert"
    const val ALERT_HIGH_TEMP_DESC = "Threshold exceeded. Cooling system activated."
    const val FAN_LABEL = "Cooling Fan"
    const val STATUS_ON = "ON"
    const val STATUS_OFF = "OFF"
    const val BTN_MANUAL_ON = "Manual On"
    const val BTN_MANUAL_OFF = "Manual Off"
    const val LABEL_FAN_RUNTIME = "FAN RUNTIME"
    const val LABEL_SYSTEM = "SYSTEM"
    const val STATUS_STABLE = "Stable"
    const val STATUS_OFFLINE = "Offline"
    const val DEVICE_NAME = "ATmega16 Node"
    const val LABEL_THRESHOLD = "Threshold"
    const val LABEL_TREND = "Recent Trend"
    const val LABEL_LIVE = "LIVE"
    const val UNIT_CELSIUS = "°C"
    const val DEFAULT_RUNTIME = "0h 0m"
    const val TEMP_FORMAT = "%.1f°C"

    // ▪ History
    const val HISTORY_TITLE = "History"
    const val HISTORY_SUBTITLE = "Temperature logs and trends"
    const val HISTORY_GRAPH_TITLE = "Temperature Graph"
    const val RANGE_1H = "1H"
    const val RANGE_24H = "24H"
    const val RANGE_7D = "7D"
    const val RANGE_30D = "30D"
    const val STAT_MAX_TEMP = "Max Temp"
    const val STAT_ALERTS = "Alerts"
    const val RECENT_LOGS_TITLE = "Recent Logs"
    const val BTN_EXPORT = "Export"
    const val TIME_FORMAT_HM = "HH:mm"

    // ▪ Devices
    const val DEVICES_TITLE = "Devices"
    const val DEVICES_SUBTITLE = "Manage Bluetooth Connections"
    const val BT_LABEL = "Bluetooth"
    const val BT_SUBTITLE = "Visible to other devices"
    const val MY_DEVICES_TITLE = "My Devices"
    const val SCANNING_LABEL = "Scanning..."
    const val BTN_SCAN = "Scan"
    const val SIGNAL_PREFIX = "Signal: "
    const val BTN_PAIR = "Pair"
    const val MOCK_SIGNAL = "-45 dBm"
    const val SIGNAL_UNIT = "dBm"

}

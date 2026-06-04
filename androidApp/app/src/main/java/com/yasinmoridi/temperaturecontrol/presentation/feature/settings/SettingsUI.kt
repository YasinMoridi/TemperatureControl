package com.yasinmoridi.temperaturecontrol.presentation.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.*
import com.yasinmoridi.temperaturecontrol.presentation.core.UiStrings
import org.koin.androidx.compose.koinViewModel

/**
 * Main Settings screen where users can configure app preferences and view device information.
 */
@Composable
fun SettingsUI(
    vm: SettingsVM = koinViewModel()
) {
    // Observing state from the ViewModel
    val threshold by vm.criticalThreshold.collectAsState()
    val pushEnabled by vm.pushNotificationsEnabled.collectAsState()
    val darkMode by vm.darkModeEnabled.collectAsState()
    val powerSaving by vm.powerSavingEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Header section with title and description
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                text = UiStrings.SETTINGS_TITLE,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = TextPrimary
            )
            Text(
                text = UiStrings.SETTINGS_SUBTITLE,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Core Configuration Card: Includes temperature threshold and various toggles
        SettingsCard(title = UiStrings.SETTINGS_CORE_TITLE) {
            // Temperature threshold slider configuration
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.MonitorHeart,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                UiStrings.SETTINGS_THRESHOLD_LABEL,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            UiStrings.SETTINGS_THRESHOLD_DESC,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        "${threshold.toInt()}${UiStrings.UNIT_CELSIUS}",
                        color = AccentCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = threshold,
                    onValueChange = { vm.updateThreshold(it) },
                    valueRange = 20f..45f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = AccentCyan,
                        inactiveTrackColor = TrackInactive
                    ),
                    modifier = Modifier.height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(UiStrings.RANGE_MIN_TEMP, color = TextSecondary.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    Text(UiStrings.RANGE_MAX_TEMP, color = TextSecondary.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = GlassWhite, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            // Application preference switches
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingsToggleItem(
                    icon = Icons.Rounded.Notifications,
                    title = UiStrings.SETTINGS_PUSH_TITLE,
                    subtitle = UiStrings.SETTINGS_PUSH_SUBTITLE,
                    checked = pushEnabled,
                    onCheckedChange = { vm.togglePushNotifications(it) }
                )
                SettingsToggleItem(
                    icon = Icons.Rounded.DarkMode,
                    title = UiStrings.SETTINGS_DARK_MODE_TITLE,
                    subtitle = UiStrings.SETTINGS_DARK_MODE_SUBTITLE,
                    checked = darkMode,
                    onCheckedChange = { vm.toggleDarkMode(it) }
                )
                SettingsToggleItem(
                    icon = Icons.Rounded.BatteryChargingFull,
                    title = UiStrings.SETTINGS_POWER_SAVING_TITLE,
                    subtitle = UiStrings.SETTINGS_POWER_SAVING_SUBTITLE,
                    checked = powerSaving,
                    onCheckedChange = { vm.togglePowerSaving(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Device Information Card: Displays firmware and MAC details
        SettingsCard(title = UiStrings.SETTINGS_DEVICE_INFO_TITLE) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoRow(UiStrings.SETTINGS_FIRMWARE_LABEL, UiStrings.MOCK_FIRMWARE)
                InfoRow(UiStrings.SETTINGS_MAC_LABEL, UiStrings.MOCK_MAC)
                InfoRow(UiStrings.SETTINGS_CALIBRATED_LABEL, UiStrings.MOCK_CALIBRATED)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Critical Action: Reset to Factory Settings
        OutlinedButton(
            onClick = { /* Handle factory reset */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(RedGlassBorder)
            ),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedStrong)
        ) {
            Text(UiStrings.BTN_RESET_FACTORY, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

/**
 * A styled card container used for grouping related settings.
 */
@Composable
fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceColor)
            .border(1.dp, GlassWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = TextSecondary.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

/**
 * A reusable row for toggle-based settings with an icon and description.
 */
@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(subtitle, color = TextSecondary, fontSize = 11.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentCyan,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = SwitchTrackUnchecked,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

/**
 * Displays a single row of read-only information.
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 12.sp)
    }
}

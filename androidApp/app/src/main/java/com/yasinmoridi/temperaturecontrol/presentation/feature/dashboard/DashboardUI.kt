package com.yasinmoridi.temperaturecontrol.presentation.feature.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.*
import com.yasinmoridi.temperaturecontrol.presentation.core.UiStrings
import org.koin.androidx.compose.koinViewModel
import com.yasinmoridi.temperaturecontrol.R

/**
 * Main Dashboard screen providing real-time temperature monitoring, fan control, 
 * and system status overview.
 */
@Composable
fun DashboardUI(
    vm: DashboardVM = koinViewModel()
) {
    // Observing UI states from the ViewModel
    val temp by vm.temperature.collectAsState()
    val tempHistory by vm.tempHistory.collectAsState()
    val isFanOn by vm.isFanOn.collectAsState()
    val threshold by vm.threshold.collectAsState()
    val runtime by vm.fanRuntime.collectAsState()
    val status by vm.systemStatus.collectAsState()

    val isConnected = status == UiStrings.STATUS_CONNECTED
    val isCritical = temp >= threshold

    // Animated glow color based on connection and temperature state
    val glowColor by animateColorAsState(
        targetValue = if (!isConnected) StatusGlowDisabled
                      else if (isCritical) StatusGlowCritical
                      else StatusGlowActive,
        label = "GlowColor"
    )

    // Primary theme color used for gauge and charts
    val themeColor by animateColorAsState(
        targetValue = if (!isConnected) TextSecondary.copy(alpha = 0.4f) 
                      else if (isCritical) AccentRed else AccentCyan,
        label = "ThemeColor"
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            // Screen Header: Title and Menu button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        UiStrings.DASHBOARD_TITLE,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        UiStrings.DASHBOARD_SUBTITLE,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                IconButton(
                    onClick = { /* Open side menu */ },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Rounded.Menu, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Connection card for managing Bluetooth link
            BluetoothStatusCard(isConnected, status, vm)

            Spacer(modifier = Modifier.height(32.dp))

            // Main Visual Element: Temperature Gauge
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                TemperatureGauge(temp, isConnected, themeColor, glowColor, isCritical)
            }

            // Alert banner shown only during critical high temperature
            AnimatedVisibility(
                visible = isConnected && isCritical,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                WarningBanner()
                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interaction Grid: Fan manual override and threshold adjustment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FanControlCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    isFanOn = isFanOn,
                    isConnected = isConnected,
                    onManualOn = { vm.toggleFanManual(true) },
                    onManualOff = { vm.toggleFanManual(false) }
                )

                ThresholdControlCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    threshold = threshold,
                    onIncrement = { vm.updateThreshold(1) },
                    onDecrement = { vm.updateThreshold(-1) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Historical Data Visualization
            TrendChartCard(tempHistory, themeColor, isConnected, isCritical)

            Spacer(modifier = Modifier.height(16.dp))

            // Secondary Metrics: Fan runtime and system health
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryItem(
                    modifier = Modifier.weight(1f),
                    label = UiStrings.LABEL_FAN_RUNTIME,
                    value = runtime,
                    icon = Icons.Rounded.Schedule
                )
                SummaryItem(
                    modifier = Modifier.weight(1f),
                    label = UiStrings.LABEL_SYSTEM,
                    value = if (isConnected) UiStrings.STATUS_STABLE else UiStrings.STATUS_OFFLINE,
                    icon = Icons.Rounded.Power,
                    valueColor = if (isConnected) GreenStable else TextSecondary
                )
            }
        }
    }
}

/**
 * Card displaying connection status and providing a connect/disconnect action.
 */
@Composable
fun BluetoothStatusCard(isConnected: Boolean, status: String, vm: DashboardVM) {
    val borderColor = if (isConnected) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.05f)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isConnected) Color.White.copy(alpha = 0.03f) else Color.Transparent)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isConnected) AccentCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isConnected) Icons.Rounded.Bluetooth else Icons.Rounded.BluetoothDisabled,
                contentDescription = null,
                tint = if (isConnected) AccentCyan else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                UiStrings.DEVICE_NAME,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                if (isConnected) UiStrings.STATUS_CONNECTED_BLE else status,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Button(
            onClick = { if (isConnected) vm.disconnect() else vm.connectToSystem() },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isConnected) Color.White.copy(alpha = 0.08f) else AccentCyan.copy(alpha = 0.15f),
                contentColor = if (isConnected) Color.White else AccentCyan
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text(if (isConnected) UiStrings.BTN_DISCONNECT else UiStrings.BTN_CONNECT, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * A circular gauge that visualizes the current temperature and provides visual feedback 
 * via animations and glows.
 */
@Composable
fun TemperatureGauge(temp: Float, isConnected: Boolean, themeColor: Color, glowColor: Color, isCritical: Boolean) {
    val animatedTemp by animateFloatAsState(
        targetValue = if (isConnected) temp else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "TempAnimation"
    )
    
    Box(
        modifier = Modifier
            .size(220.dp)
            .drawBehind {
                // Background radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent),
                        radius = size.width * 0.8f
                    ),
                    radius = size.width * 0.8f
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = (size.minDimension / 2)
            
            // Background dashed outer ring
            drawCircle(
                color = themeColor.copy(alpha = 0.3f),
                radius = radius,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
                )
            )
            
            if (isConnected) {
                // Visual progress arc representing current temperature (0-50 range)
                val sweepAngle = (animatedTemp.coerceIn(0f, 50f) / 50f) * 360f
                drawArc(
                    color = themeColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        
        // Inner informative container
        Box(
            modifier = Modifier
                .size(212.dp)
                .clip(CircleShape)
                .background(
                    if (!isConnected) BgColor.copy(alpha = 0.5f)
                    else if (isCritical) GaugeBgCritical
                    else GaugeBgActive
                )
                .border(4.dp, themeColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Thermostat,
                    contentDescription = null,
                    tint = themeColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = if (isConnected) "%.1f".format(temp) else "--",
                        color = if (isConnected) Color.White else TextSecondary.copy(alpha = 0.4f),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-3).sp
                    )
                    Text(
                        text = "°C",
                        color = TextSecondary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 10.dp, start = 2.dp)
                    )
                }
                Text(
                    text = UiStrings.CURRENT_TEMP_LABEL,
                    color = TextSecondary.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

/**
 * Warning banner displayed when the temperature exceeds the safe threshold.
 */
@Composable
fun WarningBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AccentRed.copy(alpha = 0.1f))
            .border(1.dp, AccentRed.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Rounded.ErrorOutline,
            contentDescription = null,
            tint = AccentRed.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                UiStrings.ALERT_HIGH_TEMP_TITLE,
                color = AccentRed.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                UiStrings.ALERT_HIGH_TEMP_DESC,
                color = AccentRed.copy(alpha = 0.7f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

/**
 * Card for controlling the cooling fan, includes a spinning animation when active.
 */
@Composable
fun FanControlCard(
    modifier: Modifier = Modifier,
    isFanOn: Boolean,
    isConnected: Boolean,
    onManualOn: () -> Unit,
    onManualOff: () -> Unit
) {
    // Continuous rotation animation for the fan icon
    val rotationTransition = rememberInfiniteTransition(label = "Fan")
    val rotation by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFanOn && isConnected) AccentCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fan),
                    contentDescription = null,
                    tint = if (isFanOn && isConnected) AccentCyan else TextSecondary,
                    modifier = Modifier.size(20.dp).rotate(if (isFanOn && isConnected) rotation else 0f)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isFanOn && isConnected) AccentCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    if (isFanOn && isConnected) UiStrings.STATUS_ON else UiStrings.STATUS_OFF,
                    color = if (isFanOn && isConnected) AccentCyan else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(UiStrings.FAN_LABEL, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ManualActionButton(
                text = UiStrings.BTN_MANUAL_ON,
                isSelected = isFanOn && isConnected,
                onClick = onManualOn,
                modifier = Modifier.weight(1f),
                enabled = isConnected
            )
            ManualActionButton(
                text = UiStrings.BTN_MANUAL_OFF,
                isSelected = !isFanOn && isConnected,
                onClick = onManualOff,
                modifier = Modifier.weight(1f),
                enabled = isConnected,
                selectedColor = Color.Gray
            )
        }
    }
}

/**
 * Card for displaying and adjusting the critical temperature threshold.
 */
@Composable
fun ThresholdControlCard(
    modifier: Modifier = Modifier,
    threshold: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Bolt, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
            Column {
                IconButton(onClick = onIncrement, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = null, tint = TextSecondary)
                }
                IconButton(onClick = onDecrement, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(threshold.toString(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("°C", color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp, start = 2.dp), fontSize = 14.sp)
        }
        Text(UiStrings.LABEL_THRESHOLD, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Container card for the temperature trend chart.
 */
@Composable
fun TrendChartCard(history: List<Float>, color: Color, isConnected: Boolean, isCritical: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.StackedLineChart, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(UiStrings.LABEL_TREND, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentCyan.copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(UiStrings.LABEL_LIVE, color = AccentCyan, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        SimpleTrendChart(history, if (isConnected) (if (isCritical) AccentRed else AccentCyan) else TextSecondary.copy(alpha = 0.3f))
    }
}

/**
 * A custom-drawn cubic spline chart for visualizing temperature history.
 */
@Composable
fun SimpleTrendChart(history: List<Float>, color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 8.dp)
    ) {
        if (history.size < 2) return@Canvas
        
        val width = size.width
        val height = size.height
        val maxTemp = (history.maxOrNull() ?: 40f).coerceAtLeast(35f)
        val minTemp = (history.minOrNull() ?: 20f).coerceAtMost(25f)
        val range = (maxTemp - minTemp).coerceAtLeast(1f)

        // Mapping history points to screen coordinates
        val points = history.takeLast(10).mapIndexed { index, temp ->
            val x = index * (width / (minOf(history.size, 10) - 1))
            val y = height - ((temp - minTemp) / range) * height
            Offset(x, y)
        }

        // Draw smooth path using cubic curves
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlX = (p0.x + p1.x) / 2
                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

/**
 * Reusable layout for displaying small metadata items like runtime or status.
 */
@Composable
fun SummaryItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    valueColor: Color = Color.White
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Specialized button for manual control actions with selection feedback.
 */
@Composable
fun ManualActionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedColor: Color = AccentCyan
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) selectedColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.03f))
            .border(
                width = 1.dp,
                color = if (isSelected) selectedColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) selectedColor else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

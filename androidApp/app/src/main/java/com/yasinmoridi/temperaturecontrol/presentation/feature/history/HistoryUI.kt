package com.yasinmoridi.temperaturecontrol.presentation.feature.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.*
import com.yasinmoridi.temperaturecontrol.presentation.core.UiStrings
import org.koin.androidx.compose.koinViewModel

/**
 * Main UI for the Temperature History screen.
 * Displays charts, statistics, and recent logs.
 */
@Composable
fun HistoryUI(vm: HistoryVM = koinViewModel()) {
    val selectedRange by vm.selectedRange.collectAsState()
    val logs by vm.logs.collectAsState()
    val maxTemp by vm.maxTemp.collectAsState()
    val alertsCount by vm.alertsCount.collectAsState()

    // Transform logs into numerical data for the chart
    val chartData = remember(logs) {
        logs.map { it.temperature.replace(UiStrings.UNIT_CELSIUS, "").trim().toFloatOrNull() ?: 0f }.reversed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {
        // Header: Screen Title and Description
        Column {
            Text(
                UiStrings.HISTORY_TITLE,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Text(
                UiStrings.HISTORY_SUBTITLE,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Chart Card: Visualization of temperature over time
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceColor)
                .border(1.dp, GlassWhite, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.BarChart,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        UiStrings.HISTORY_GRAPH_TITLE,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                // Range Selector: Allows user to filter by 1H, 24H, 7D, 30D
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(UiStrings.RANGE_1H, UiStrings.RANGE_24H, UiStrings.RANGE_7D, UiStrings.RANGE_30D).forEach { range ->
                        val isSelected = range == selectedRange
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) CyanHighlight else Color.Transparent)
                                .clickable { vm.selectRange(range) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = range,
                                color = if (isSelected) AccentCyan else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            TemperatureChart(chartData)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Summary: Cards for Peak Temperature and Alert Counts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = UiStrings.STAT_MAX_TEMP,
                value = UiStrings.TEMP_FORMAT.format(maxTemp),
                icon = Icons.AutoMirrored.Rounded.TrendingUp,
                iconColor = Emerald400
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = UiStrings.STAT_ALERTS,
                value = alertsCount.toString(),
                icon = Icons.Default.Warning,
                iconColor = Amber400
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Logs Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                UiStrings.RECENT_LOGS_TITLE,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* Export Action */ }
            ) {
                Icon(
                    Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    UiStrings.BTN_EXPORT,
                    color = AccentCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logs List: Detailed table of recorded sensor data
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceColor)
                .border(1.dp, GlassWhite, RoundedCornerShape(16.dp))
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(logs) { index, log ->
                    LogItem(log)
                    if (index < logs.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = GlassWhite,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * A simple card displaying a statistic with an icon.
 */
@Composable
fun StatCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceColor)
            .border(1.dp, GlassWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * Custom-drawn Bezier line chart for temperature trends.
 */
@Composable
fun TemperatureChart(data: List<Float>) {
    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
        val width = size.width
        val height = size.height
        val maxVal = (data.maxOrNull() ?: 30f) + 2f
        val minVal = (data.minOrNull() ?: 20f) - 2f
        val range = (maxVal - minVal).coerceAtLeast(1f)

        // Draw horizontal grid lines
        val gridLines = 5
        for (i in 0 until gridLines) {
            val y = height - (i.toFloat() / (gridLines - 1)) * height
            drawLine(
                color = ChartGrid,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        if (data.size < 2) return@Canvas

        // Calculate coordinates for points
        val points = data.mapIndexed { i, temp ->
            Offset(i * (width / (data.size - 1)), height - ((temp - minVal) / range) * height)
        }

        // Draw the smooth curve
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]; val p1 = points[i+1]
                val controlX = (p0.x + p1.x) / 2
                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }
        }
        
        // Gradient fill under the curve
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(AccentCyan.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        // Draw the main line
        drawPath(
            path = path,
            color = AccentCyan,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

/**
 * Single row item in the logs list.
 * Highlights high temperature warnings.
 */
@Composable
fun LogItem(log: LogEntry) {
    // Determine if this log entry should trigger a visual warning
    val isAlert = (log.temperature.replace(UiStrings.UNIT_CELSIUS, "").trim().toFloatOrNull() ?: 0f) >= 30f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Rounded.InsertDriveFile,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(log.time, color = TextSecondary, fontSize = 12.sp)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isAlert) {
                // Red dot indicator for alert readings
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(RedAlertDot, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                log.temperature, 
                color = if (isAlert) RedAlertText else Color.White,
                fontWeight = FontWeight.Medium, 
                fontSize = 14.sp
            )
        }
    }
}

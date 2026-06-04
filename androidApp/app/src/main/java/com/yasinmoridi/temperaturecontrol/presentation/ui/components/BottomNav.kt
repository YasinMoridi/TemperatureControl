package com.yasinmoridi.temperaturecontrol.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yasinmoridi.temperaturecontrol.data.dataClass.BottomNavItem
import com.yasinmoridi.temperaturecontrol.presentation.core.main.MainVM
import com.yasinmoridi.temperaturecontrol.presentation.core.navigation.AppDestination
import com.yasinmoridi.temperaturecontrol.presentation.feature.dashboard.DashboardUI
import com.yasinmoridi.temperaturecontrol.presentation.feature.devices.DevicesUI
import com.yasinmoridi.temperaturecontrol.presentation.feature.history.HistoryUI
import com.yasinmoridi.temperaturecontrol.presentation.feature.settings.SettingsUI
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.AccentCyan
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.AccentRed
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.BgColor
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreenUI(
    vm: MainVM = koinViewModel()
) {
    val navController = rememberNavController()
    val isCritical by vm.isCritical.collectAsState()
    val isConnected by vm.isConnected.collectAsState()

    val showOverlay = isCritical && isConnected

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                CustomBottomBar(navController)
            },
            containerColor = BgColor
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.Dash,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<AppDestination.Dash> { DashboardUI() }
                composable<AppDestination.Devices> { DevicesUI() }
                composable<AppDestination.History> { HistoryUI() }
                composable<AppDestination.Settings> { SettingsUI() }
            }
        }

        // 1. Critical Temperature Full Overlay FX (چشمک‌زن سراسری)
        if (showOverlay) {
            val infiniteTransition = rememberInfiniteTransition(label = "CriticalOverlay")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.05f,
                targetValue = 0.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Alpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = alpha))
            )
        }

        // 2. Critical Alert Banner (بنر هشدار بالای صفحه)
        AnimatedVisibility(
            visible = showOverlay,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp, start = 16.dp, end = 16.dp)
        ) {
            CriticalAlertBanner()
        }
    }
}

@Composable
fun CriticalAlertBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xE6DC2626)) // red-600 with 90% opacity
            .border(1.dp, Color(0xFFF87171).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon with pulse effect
            val infiniteTransition = rememberInfiniteTransition(label = "IconPulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Scale"
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Whatshot,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp).graphicsLayer(scaleX = scale, scaleY = scale)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CRITICAL TEMPERATURE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Device overheating! Cooling system active.",
                    color = Color(0xFFFEE2E2),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun CustomBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        color = BgColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(85.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                BottomNavItem(AppDestination.Dash, "Dash", Icons.Rounded.GridView),
                BottomNavItem(AppDestination.Devices, "Devices", Icons.Rounded.Memory),
                BottomNavItem(AppDestination.History, "History", Icons.AutoMirrored.Rounded.TrendingUp),
                BottomNavItem(AppDestination.Settings, "Settings", Icons.Rounded.Settings)
            )

            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) AccentCyan else TextSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.label,
                        color = if (selected) AccentCyan else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Indicator Dot
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (selected) AccentCyan else Color.Transparent)
                    )
                }
            }
        }
    }
}

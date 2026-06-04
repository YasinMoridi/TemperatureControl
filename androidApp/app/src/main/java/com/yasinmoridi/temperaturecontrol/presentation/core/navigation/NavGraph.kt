package com.yasinmoridi.temperaturecontrol.presentation.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yasinmoridi.temperaturecontrol.presentation.ui.components.MainScreenUI
import com.yasinmoridi.temperaturecontrol.presentation.feature.splash.SplashUI

@Composable
fun SetUpNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<AppDestination.Splash> {
            SplashUI(navController = navController)
        }
        composable<AppDestination.DashboardRoot> {
            MainScreenUI()
        }
    }
}
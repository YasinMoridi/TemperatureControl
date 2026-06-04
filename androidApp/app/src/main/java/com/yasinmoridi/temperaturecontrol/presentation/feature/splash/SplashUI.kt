package com.yasinmoridi.temperaturecontrol.presentation.feature.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yasinmoridi.temperaturecontrol.presentation.core.navigation.AppDestination
import com.yasinmoridi.temperaturecontrol.presentation.core.UiStrings
import com.yasinmoridi.temperaturecontrol.presentation.ui.theme.*
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/**
 * Entry point of the application. Displays branding animations while the system initializes.
 */
@Composable
fun SplashUI(
    vm: SplashVM = koinViewModel(),
    navController: NavHostController,
) {
    // Navigation logic: Transition to Dashboard after a fixed delay
    LaunchedEffect(Unit) {
        delay(3500) // 3.5 seconds delay for branding visibility
        navController.navigate(AppDestination.DashboardRoot) {
            popUpTo(AppDestination.Splash) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),
        contentAlignment = Alignment.Center
    ) {
        // Background layer: Subtle tech-style grid
        GridBackground()

        // Background layer: Soft ambient glow in the center
        Box(
            modifier = Modifier
                .size(400.dp)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SplashGlow, Color.Transparent),
                        center = Offset.Unspecified,
                        radius = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Animated logo with multiple rotation layers
            AnimatedLogo()

            Spacer(modifier = Modifier.height(32.dp))

            // Branding and system status text
            TypographySection()
        }

        // Bottom progress indicator simulating system loading
        BottomLoadingSection(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

/**
 * Draws a subtle blueprint-style grid in the background.
 */
@Composable
fun GridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 30.dp.toPx()
        val strokeWidth = 1.dp.toPx()
        val gridColor = SplashGrid

        // Draw vertical grid lines
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height),
                strokeWidth = strokeWidth
            )
        }

        // Draw horizontal grid lines
        for (y in 0..size.height.toInt() step gridSize.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = strokeWidth
            )
        }
    }
}

/**
 * Complex animated logo consisting of rotating rings and a glowing thermostat icon.
 */
@Composable
fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    
    // Counter-clockwise rotation for the outer dashed ring
    val rotationOuter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "outer_rotation"
    )

    // Clockwise rotation for the inner partial ring
    val rotationInner by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "inner_rotation"
    )

    // Pulsing effect for the "Bolt" (electricity) icon
    val zapAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0.0f at 0
                1.0f at 1000
                0.0f at 2000
            },
            repeatMode = RepeatMode.Restart
        ), label = "zap_alpha"
    )

    // Initial scale-up entrance animation
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .size(144.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(CircleShape)
            .background(SplashLogoBg.copy(alpha = 0.8f))
            .border(1.dp, SplashLogoBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Outer rotating ring (dashed style)
        Canvas(modifier = Modifier.fillMaxSize().rotate(rotationOuter)) {
            drawCircle(
                color = SplashLogoOuter,
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                ),
                radius = (size.minDimension / 2) - 4.dp.toPx()
            )
        }

        // Inner rotating ring (partial arc style)
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp).rotate(rotationInner)) {
            drawCircle(
                color = SplashLogoBorder,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(200f, 300f), 0f)
                ),
                radius = (size.minDimension / 2)
            )
        }

        // Center Branding Icons
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Thermostat,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(56.dp)
            )
            
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = AccentCyan.copy(alpha = zapAlpha),
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (4).dp, y = (-4).dp)
            )
        }
    }
}

/**
 * Displays the app name and initialization status.
 */
@Composable
fun TypographySection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = buildAnnotatedString {
                append(UiStrings.SPLASH_TITLE_PART1)
                withStyle(style = SpanStyle(color = AccentCyan)) {
                    append(UiStrings.SPLASH_TITLE_PART2)
                }
            },
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Technical status indicator row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(4.dp).background(AccentCyan, CircleShape))
            Text(
                text = UiStrings.SPLASH_INIT_TEXT,
                color = SplashTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
            Box(modifier = Modifier.size(4.dp).background(AccentCyan, CircleShape))
        }
    }
}

/**
 * Visual loading bar with a sweeping gradient effect and technical text.
 */
@Composable
fun BottomLoadingSection(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    // Animates the gradient offset for a "scanning" effect
    val progressOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "progress"
    )

    Column(
        modifier = modifier.padding(bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(2.dp)
                .background(SplashLoadingTrack, RoundedCornerShape(1.dp))
                .clip(RoundedCornerShape(1.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, AccentCyan, Color.Transparent),
                            startX = size.width * progressOffset,
                            endX = size.width * (progressOffset + 1f)
                        )
                        drawRect(brush)
                    }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = UiStrings.SPLASH_LOADING_TEXT,
            color = Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp
        )
    }
}

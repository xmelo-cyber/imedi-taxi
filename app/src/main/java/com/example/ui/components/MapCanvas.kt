package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.data.LocationPoint
import com.example.data.Ride
import com.example.data.RideStatus
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TaxiYellow
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MapCanvas(
    modifier: Modifier = Modifier,
    activeRide: Ride? = null,
    pickupLocation: LocationPoint? = null,
    destinationLocation: LocationPoint? = null,
    isDarkMode: Boolean = true,
    onMapCenterReset: () -> Unit = {}
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    // Pulsing circle for user location
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Moving car animation
    val carProgress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "carProgress"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            val mapBg = if (isDarkMode) Color(0xFF14171D) else Color(0xFFECEFF1)
            val streetColor = if (isDarkMode) Color(0xFF232832) else Color(0xFFFFFFFF)
            val majorStreetColor = if (isDarkMode) Color(0xFF2F3644) else Color(0xFFE2E6EA)
            val riverColor = if (isDarkMode) Color(0xFF17283C) else Color(0xFFBCE0FD)
            val parkColor = if (isDarkMode) Color(0xFF16251C) else Color(0xFFD4EDDA)

            // 1. Draw Map Background
            drawRect(color = mapBg)

            // 2. Draw Scenic River (Mtkvari / Kura River simulation in Tbilisi)
            val riverPath = Path().apply {
                moveTo(-200f + offsetX, height * 0.1f + offsetY)
                cubicTo(
                    width * 0.4f + offsetX, height * 0.25f + offsetY,
                    width * 0.2f + offsetX, height * 0.65f + offsetY,
                    width + 200f + offsetX, height * 0.85f + offsetY
                )
            }
            drawPath(
                path = riverPath,
                color = riverColor,
                style = Stroke(width = 65f * zoomScale, cap = StrokeCap.Round)
            )

            // 3. Draw Green Parks (Vake Park, Mtatsminda)
            drawCircle(
                color = parkColor,
                radius = 160f * zoomScale,
                center = Offset(width * 0.18f + offsetX, height * 0.38f + offsetY)
            )
            drawCircle(
                color = parkColor,
                radius = 130f * zoomScale,
                center = Offset(width * 0.82f + offsetX, height * 0.22f + offsetY)
            )

            // 4. Draw City Grid Streets (Rustaveli, Melikishvili, Chavchavadze avenues)
            for (i in -4..10) {
                val yLine = (height * 0.1f * i) + offsetY
                drawLine(
                    color = streetColor,
                    start = Offset(-200f, yLine),
                    end = Offset(width + 200f, yLine),
                    strokeWidth = 14f * zoomScale
                )
            }
            for (j in -4..10) {
                val xLine = (width * 0.2f * j) + offsetX
                drawLine(
                    color = streetColor,
                    start = Offset(xLine, -200f),
                    end = Offset(xLine, height + 200f),
                    strokeWidth = 14f * zoomScale
                )
            }

            // Diagonal arterial avenues
            val majorAvenuePath = Path().apply {
                moveTo(-100f + offsetX, height * 0.7f + offsetY)
                lineTo(width * 0.5f + offsetX, height * 0.45f + offsetY)
                lineTo(width + 100f + offsetX, height * 0.15f + offsetY)
            }
            drawPath(
                path = majorAvenuePath,
                color = majorStreetColor,
                style = Stroke(width = 24f * zoomScale, cap = StrokeCap.Round)
            )

            // 5. Draw Nearby Available Free Taxi Cabs around city
            val cabOffsets = listOf(
                Offset(width * 0.35f + offsetX, height * 0.42f + offsetY),
                Offset(width * 0.68f + offsetX, height * 0.50f + offsetY),
                Offset(width * 0.42f + offsetX, height * 0.68f + offsetY),
                Offset(width * 0.75f + offsetX, height * 0.32f + offsetY)
            )
            for (cab in cabOffsets) {
                drawTaxiCarIcon(cab, TaxiYellow, 26f * zoomScale)
            }

            // 6. Draw User Location Pin with Pulse
            val userCenter = Offset(width * 0.5f + offsetX, height * 0.55f + offsetY)
            drawCircle(
                color = Color(0xFF3B82F6).copy(alpha = pulseAlpha),
                radius = pulseRadius * zoomScale,
                center = userCenter
            )
            drawCircle(
                color = Color(0xFF3B82F6),
                radius = 12f * zoomScale,
                center = userCenter
            )
            drawCircle(
                color = Color.White,
                radius = 5f * zoomScale,
                center = userCenter
            )

            // 7. Draw Active Trip Route Polyline if ride is active or destinations chosen
            val startPt = Offset(width * 0.30f + offsetX, height * 0.62f + offsetY)
            val endPt = Offset(width * 0.72f + offsetX, height * 0.35f + offsetY)
            val wayPt = Offset(width * 0.48f + offsetX, height * 0.48f + offsetY)

            val showRoute = (activeRide != null) || (destinationLocation != null)
            if (showRoute) {
                // Route Shadow / Border
                val routePath = Path().apply {
                    moveTo(startPt.x, startPt.y)
                    quadraticTo(wayPt.x, wayPt.y, endPt.x, endPt.y)
                }

                // Outer route glow
                drawPath(
                    path = routePath,
                    color = TaxiYellow.copy(alpha = 0.35f),
                    style = Stroke(width = 18f * zoomScale, cap = StrokeCap.Round)
                )

                // Main route line
                drawPath(
                    path = routePath,
                    color = TaxiYellow,
                    style = Stroke(
                        width = 8f * zoomScale,
                        cap = StrokeCap.Round,
                        pathEffect = if (activeRide?.status == RideStatus.SEARCHING) {
                            PathEffect.dashPathEffect(floatArrayOf(25f, 15f), 0f)
                        } else null
                    )
                )

                // Pickup Marker (Green)
                drawCircle(color = StatusGreen, radius = 14f * zoomScale, center = startPt)
                drawCircle(color = Color.White, radius = 6f * zoomScale, center = startPt)

                // Destination Marker (Red)
                drawCircle(color = StatusRed, radius = 14f * zoomScale, center = endPt)
                drawCircle(color = Color.White, radius = 6f * zoomScale, center = endPt)

                // Animated Traveling Taxi Car on route
                val carX = (1 - carProgress) * (1 - carProgress) * startPt.x +
                        2 * (1 - carProgress) * carProgress * wayPt.x +
                        carProgress * carProgress * endPt.x
                val carY = (1 - carProgress) * (1 - carProgress) * startPt.y +
                        2 * (1 - carProgress) * carProgress * wayPt.y +
                        carProgress * carProgress * endPt.y

                drawTaxiCarIcon(Offset(carX, carY), TaxiYellow, 34f * zoomScale)
            }
        }

        // Floating Map Controls
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            shadowElevation = 4.dp
        ) {
            IconButton(
                onClick = {
                    offsetX = 0f
                    offsetY = 0f
                    zoomScale = 1.0f
                    onMapCenterReset()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = "My Location",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun DrawScope.drawTaxiCarIcon(center: Offset, color: Color, size: Float) {
    // Car Body
    drawCircle(
        color = Color.Black.copy(alpha = 0.35f),
        radius = size * 0.75f,
        center = center.copy(y = center.y + 4f)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(center.x - size * 0.45f, center.y - size * 0.7f),
        size = androidx.compose.ui.geometry.Size(size * 0.9f, size * 1.4f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size * 0.25f, size * 0.25f)
    )
    // Windshield
    drawRoundRect(
        color = Color(0xFF1F242D),
        topLeft = Offset(center.x - size * 0.35f, center.y - size * 0.4f),
        size = androidx.compose.ui.geometry.Size(size * 0.7f, size * 0.35f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size * 0.1f, size * 0.1f)
    )
    // Taxi roof sign
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(center.x - size * 0.22f, center.y - size * 0.05f),
        size = androidx.compose.ui.geometry.Size(size * 0.44f, size * 0.2f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
    )
}

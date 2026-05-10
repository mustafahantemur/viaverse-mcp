package app.viaverse.template.ui.screen.splash

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.viaverse.template.generated.resources.Res
import app.viaverse.template.generated.resources.viaverse_text
import app.viaverse.template.generated.resources.viaverse_v_orange_green
import app.viaverse.template.ui.theme.ViaverseColors
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.hypot

private data class SnowParticleSeed(
    val angle: Float,
    val radius: Float,
    val drift: Float,
    val column: Float,
    val fallOffset: Float,
    val fallSpeed: Float,
    val sway: Float,
    val swayPhase: Float,
    val wind: Float,
    val depth: Float
)

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "splash_particles")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 9000)),
        label = "particle_phase"
    )

    var pointer by remember { mutableStateOf(Offset.Zero) }
    var startPointer by remember { mutableStateOf(Offset.Zero) }
    var windTarget by remember { mutableStateOf(Offset.Zero) }
    var isTouching by remember { mutableStateOf(false) }
    var hasPointer by remember { mutableStateOf(false) }
    val windX by animateFloatAsState(
        targetValue = windTarget.x,
        animationSpec = spring(stiffness = 18f, dampingRatio = 0.72f),
        label = "snow_wind_x"
    )
    val windY by animateFloatAsState(
        targetValue = windTarget.y,
        animationSpec = spring(stiffness = 18f, dampingRatio = 0.72f),
        label = "snow_wind_y"
    )
    val revealProgress by animateFloatAsState(
        targetValue = if (isTouching) 1f else 0f,
        animationSpec = tween(durationMillis = if (isTouching) 620 else 760),
        label = "burn_reveal_progress"
    )

    val particles = remember {
        List(280) { index ->
            SnowParticleSeed(
                angle = ((index * 47) % 360).toFloat(),
                radius = 1.1f + (index % 10) * 0.48f + if (index % 37 == 0) 2.2f else 0f,
                drift = -38f + ((index * 19) % 76),
                column = ((index * 37) % 100) / 100f,
                fallOffset = ((index * 53) % 100) / 100f,
                fallSpeed = 0.18f + (index % 11) * 0.035f,
                sway = 18f + (index % 13) * 8f,
                swayPhase = (index * 0.73f) % 6.28f,
                wind = 0.34f + (index % 9) * 0.13f,
                depth = 0.45f + (index % 7) * 0.11f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitPointerEvent().changes.firstOrNull { it.pressed } ?: return@awaitEachGesture
                    isTouching = true
                    hasPointer = true
                    pointer = down.position
                    startPointer = down.position
                    windTarget = Offset.Zero
                    down.consume()

                    while (true) {
                        val event = awaitPointerEvent()
                        val change: PointerInputChange = event.changes.firstOrNull() ?: break
                        if (!change.pressed) {
                            val dx = change.position.x - startPointer.x
                            val dy = change.position.y - startPointer.y
                            isTouching = false
                            pointer = change.position
                            windTarget = Offset(windTarget.x * 0.45f, windTarget.y * 0.45f)
                            if (hypot(dx, dy) < 18f) {
                                onComplete()
                            }
                            break
                        }
                        if (abs(change.positionChange().x) > 0f || abs(change.positionChange().y) > 0f) {
                            val delta = change.positionChange()
                            windTarget = Offset(
                                x = (delta.x * 10f).coerceIn(-360f, 360f),
                                y = (delta.y * 8f).coerceIn(-260f, 260f)
                            )
                            pointer = change.position
                            change.consume()
                        }
                    }
                }
            }
            .background(Color(0xFFFFEDD5)),
        contentAlignment = Alignment.Center
    ) {
        SplashBackground(
            pointer = pointer,
            wind = Offset(windX, windY),
            isTouching = isTouching,
            phase = phase,
            particles = particles
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.viaverse_v_orange_green),
                contentDescription = "Viaverse",
                modifier = Modifier.size(210.dp),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(Res.drawable.viaverse_text),
                contentDescription = "Viaverse text",
                modifier = Modifier.fillMaxWidth(0.76f),
                contentScale = ContentScale.Fit
            )
        }

        if (revealProgress > 0.01f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawBurnReveal(
                    center = pointer,
                    phase = phase,
                    progress = revealProgress
                )
            }
        }
    }
}

@Composable
private fun SplashBackground(
    pointer: Offset,
    wind: Offset,
    isTouching: Boolean,
    phase: Float,
    particles: List<SnowParticleSeed>
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x66FB923C), Color.Transparent),
                center = Offset(size.width * 0.34f, size.height * 0.30f),
                radius = 440f
            ),
            radius = 440f,
            center = Offset(size.width * 0.34f, size.height * 0.30f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x44F59E0B), Color.Transparent),
                center = Offset(size.width * 0.72f, size.height * 0.70f),
                radius = 520f
            ),
            radius = 520f,
            center = Offset(size.width * 0.72f, size.height * 0.70f)
        )

        particles.forEachIndexed { index, seed ->
            val progress = (phase * seed.fallSpeed + seed.fallOffset) % 1f
            val fallDistance = size.height + 360f
            val naturalSway = kotlin.math.sin(phase * 6.28f * seed.depth + seed.swayPhase) * seed.sway
            val asynchronousWind = kotlin.math.sin(phase * 9.2f + seed.angle) * wind.x * 0.09f
            val touchDistance = hypot(pointer.x - seed.column * size.width, pointer.y - progress * fallDistance)
            val touchLift = if (isTouching && touchDistance < 420f) (1f - touchDistance / 420f) else 0f
            val x = seed.column * size.width +
                naturalSway +
                wind.x * seed.wind * (0.42f + progress * 0.75f) +
                asynchronousWind +
                seed.drift * progress
            val y = progress * fallDistance - 180f +
                wind.y * seed.wind * 0.16f +
                touchLift * 28f * kotlin.math.sin(seed.angle)

            drawCircle(
                color = ViaverseColors.BrandOrange.copy(alpha = 0.26f + seed.depth * 0.18f),
                radius = seed.radius,
                center = Offset(x, y)
            )
        }
    }
}

private fun DrawScope.drawBurnReveal(
    center: Offset,
    phase: Float,
    progress: Float
) {
    val baseRadius = 82f * progress
    if (baseRadius <= 1f) return

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x10FFFFFF),
                Color(0x26F97316),
                Color(0x38301C0B),
                Color.Transparent
            ),
            center = center,
            radius = baseRadius + 62f
        ),
        radius = baseRadius + 62f,
        center = center
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                Color.White.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.52f),
                Color.Transparent
            ),
            center = center,
            radius = baseRadius + 30f
        ),
        radius = baseRadius + 30f,
        center = center,
        blendMode = BlendMode.Clear
    )

    withTransform({
        rotate(degrees = phase * 4f, pivot = center)
    }) {
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color(0x22F97316), Color(0x40301C0B), Color.Transparent),
                center = center,
                radius = baseRadius + 58f
            ),
            topLeft = center - Offset(baseRadius * 1.16f, baseRadius * 1.06f),
            size = androidx.compose.ui.geometry.Size(baseRadius * 2.32f, baseRadius * 2.12f)
        )
    }
}

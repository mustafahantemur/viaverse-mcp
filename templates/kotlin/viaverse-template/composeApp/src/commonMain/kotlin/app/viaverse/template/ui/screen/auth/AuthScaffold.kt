package app.viaverse.template.ui.screen.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.viaverse.template.generated.resources.Res
import app.viaverse.template.generated.resources.viaverse_text
import app.viaverse.template.generated.resources.viaverse_v_orange_green
import app.viaverse.template.ui.theme.Dimensions
import app.viaverse.template.ui.theme.ViaverseColors
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun AuthScaffold(
    mode: AuthScaffoldMode,
    error: String?,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(ViaverseColors.WarmBase),
        color = ViaverseColors.WarmBase
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = Dimensions.ScreenMaxWidth)
                .then(
                    if (mode == AuthScaffoldMode.Centered) {
                        Modifier
                            .fillMaxHeight()
                            .padding(bottom = 72.dp)
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = Dimensions.SpaceLg, vertical = Dimensions.SpaceXl),
            verticalArrangement = if (mode == AuthScaffoldMode.Centered) Arrangement.Center else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mode == AuthScaffoldMode.Top) {
                Spacer(Modifier.height(44.dp))
            }

            AuthBrandHeader(showTextLogo = mode != AuthScaffoldMode.Form)

            if (mode == AuthScaffoldMode.Form) {
                Spacer(Modifier.height(28.dp))
            }

            content()

            error?.let {
                Text(
                    text = it,
                    color = ViaverseColors.BrandOrange,
                    modifier = Modifier.padding(top = 14.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun AuthBrandHeader(showTextLogo: Boolean) {
    val logoTransition = rememberInfiniteTransition(label = "auth_logo_rotation")
    val logoRotation by logoTransition.animateFloat(
        initialValue = -18f,
        targetValue = 342f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7800, easing = LinearEasing)
        ),
        label = "auth_logo_rotation_y"
    )
    val density = LocalDensity.current.density

    Image(
        painter = painterResource(Res.drawable.viaverse_v_orange_green),
        contentDescription = "Viaverse",
        modifier = Modifier
            .size(if (showTextLogo) 86.dp else 76.dp)
            .graphicsLayer {
                rotationY = logoRotation
                cameraDistance = 18f * density
            },
        contentScale = ContentScale.Fit
    )

    if (showTextLogo) {
        Spacer(Modifier.height(18.dp))
        Image(
            painter = painterResource(Res.drawable.viaverse_text),
            contentDescription = "Viaverse text",
            modifier = Modifier.fillMaxWidth(0.50f),
            contentScale = ContentScale.Fit
        )
    }
}

internal enum class AuthScaffoldMode {
    Top,
    Form,
    Centered
}

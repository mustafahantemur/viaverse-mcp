package app.viaverse.template.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = ViaverseColors.BrandOrange,
    secondary = ViaverseColors.TrustGreen,
    background = ViaverseColors.WarmBase,
    surface = ViaverseColors.WarmSurface,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = ViaverseColors.Ink,
    onSurface = ViaverseColors.Ink
)

@Composable
fun ViaverseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = ViaverseTypography,
        content = content
    )
}


package app.viaverse.template.ui.screen.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.viaverse.template.ui.theme.ViaverseColors

@Composable
internal fun GoogleIcon(modifier: Modifier = Modifier) {
    val vector = remember {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF4285F4))) {
                moveTo(22.56f, 12.25f)
                curveTo(22.56f, 11.47f, 22.49f, 10.72f, 22.36f, 10f)
                horizontalLineTo(12f)
                verticalLineTo(14.26f)
                horizontalLineTo(17.92f)
                curveTo(17.66f, 15.63f, 16.88f, 16.79f, 15.71f, 17.57f)
                verticalLineTo(20.34f)
                horizontalLineTo(19.28f)
                curveTo(21.36f, 18.42f, 22.56f, 15.6f, 22.56f, 12.25f)
                close()
            }
            path(fill = SolidColor(Color(0xFF34A853))) {
                moveTo(12f, 23f)
                curveTo(14.97f, 23f, 17.46f, 22.02f, 19.28f, 20.34f)
                lineTo(15.71f, 17.57f)
                curveTo(14.73f, 18.23f, 13.48f, 18.63f, 12f, 18.63f)
                curveTo(9.14f, 18.63f, 6.71f, 16.7f, 5.84f, 14.1f)
                horizontalLineTo(2.18f)
                verticalLineTo(16.94f)
                curveTo(3.99f, 20.53f, 7.7f, 23f, 12f, 23f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFBBC05))) {
                moveTo(5.84f, 14.09f)
                curveTo(5.62f, 13.43f, 5.49f, 12.73f, 5.49f, 12f)
                reflectiveCurveTo(5.62f, 10.57f, 5.84f, 9.91f)
                verticalLineTo(7.07f)
                horizontalLineTo(2.18f)
                curveTo(1.43f, 8.55f, 1f, 10.22f, 1f, 12f)
                reflectiveCurveTo(1.43f, 15.45f, 2.18f, 16.93f)
                lineTo(5.84f, 14.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEA4335))) {
                moveTo(12f, 5.38f)
                curveTo(13.62f, 5.38f, 15.06f, 5.94f, 16.21f, 7.02f)
                lineTo(19.36f, 3.87f)
                curveTo(17.45f, 2.09f, 14.97f, 1f, 12f, 1f)
                curveTo(7.7f, 1f, 3.99f, 3.47f, 2.18f, 7.07f)
                lineTo(5.84f, 9.91f)
                curveTo(6.71f, 7.31f, 9.14f, 5.38f, 12f, 5.38f)
                close()
            }
        }.build()
    }
    Image(imageVector = vector, contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
}

@Composable
internal fun InstagramIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRoundRect(
            brush = Brush.linearGradient(
                listOf(Color(0xFFF58529), Color(0xFFDD2A7B), Color(0xFF8134AF))
            ),
            cornerRadius = CornerRadius(7f, 7f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(6.3f, 6.3f),
            size = Size(size.width - 12.6f, size.height - 12.6f),
            cornerRadius = CornerRadius(5f, 5f),
            style = Stroke(width = 2.1f)
        )
        drawCircle(
            color = Color.White,
            radius = size.minDimension * 0.17f,
            center = Offset(size.width / 2f, size.height / 2f),
            style = Stroke(width = 2.1f)
        )
        drawCircle(
            color = Color.White,
            radius = 1.8f,
            center = Offset(size.width * 0.70f, size.height * 0.30f)
        )
    }
}

@Composable
internal fun AppleIcon(modifier: Modifier = Modifier) {
    val vector = remember {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(ViaverseColors.Ink)) {
                moveTo(18.71f, 19.5f)
                curveTo(17.88f, 20.74f, 17f, 21.95f, 15.66f, 21.97f)
                curveTo(14.32f, 22f, 13.89f, 21.18f, 12.37f, 21.18f)
                curveTo(10.84f, 21.18f, 10.37f, 21.95f, 9.1f, 22f)
                curveTo(7.79f, 22.05f, 6.8f, 20.68f, 5.96f, 19.47f)
                curveTo(4.25f, 17f, 2.94f, 12.45f, 4.7f, 9.39f)
                curveTo(5.57f, 7.87f, 7.13f, 6.91f, 8.82f, 6.88f)
                curveTo(10.1f, 6.86f, 11.32f, 7.75f, 12.11f, 7.75f)
                curveTo(12.89f, 7.75f, 14.37f, 6.68f, 15.92f, 6.84f)
                curveTo(16.57f, 6.87f, 18.39f, 7.1f, 19.56f, 8.82f)
                curveTo(19.47f, 8.88f, 17.39f, 10.1f, 17.41f, 12.63f)
                curveTo(17.44f, 15.65f, 20.06f, 16.66f, 20.09f, 16.67f)
                curveTo(20.06f, 16.74f, 19.67f, 18.11f, 18.71f, 19.5f)
                close()
                moveTo(13f, 3.5f)
                curveTo(13.73f, 2.67f, 14.94f, 2.04f, 15.94f, 2f)
                curveTo(16.07f, 3.17f, 15.6f, 4.35f, 14.9f, 5.19f)
                curveTo(14.21f, 6.04f, 13.07f, 6.7f, 11.95f, 6.61f)
                curveTo(11.8f, 5.46f, 12.36f, 4.26f, 13f, 3.5f)
                close()
            }
        }.build()
    }
    Image(imageVector = vector, contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
}

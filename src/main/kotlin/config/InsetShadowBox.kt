package config

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun InsetShadowBox(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.clip(RoundedCornerShape(50))) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val shadowColor = Color.Black
        val shadowAlpha = 0.5f
        val cornerRadius = CornerRadius(if (width < height) width / 2 else height / 2)

        Canvas(modifier = Modifier.matchParentSize()) {// Draw the main rounded rectangle
            drawRoundRect(
                color = Color(0xff50665d),
                size = Size(width, height),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(shadowColor.copy(alpha = shadowAlpha), Color.Transparent),
                    startY = 0f,
                    endY = 10f
                ),
                size = Size(width, 10f),
                topLeft = Offset(0f, 0f),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, shadowColor.copy(alpha = shadowAlpha)),
                    startY = height - 10f, // Adjust for the desired spread of the shadow
                    endY = height
                ),
                size = Size(width, 10f),
                topLeft = Offset(0f, height - 10f),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(shadowColor.copy(alpha = shadowAlpha), Color.Transparent),
                    startX = 0f,
                    endX = 10f
                ),
                size = Size(10f, height),
                topLeft = Offset(0f, 0f),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, shadowColor.copy(alpha = shadowAlpha)),
                    startX = width - 10f, // Adjust for the desired spread of the shadow
                    endX = width
                ),
                size = Size(10f, height),
                topLeft = Offset(width - 10f, 0f),
                cornerRadius = cornerRadius
            )
        }
    }
}
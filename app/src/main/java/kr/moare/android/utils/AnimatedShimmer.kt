package kr.moare.android.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedShimmer(loading: Boolean = false): Brush {
    val colors = listOf(
        Color.Gray.copy(alpha = 0.6f),
        Color.Gray.copy(alpha = 0.2f),
        Color.Gray.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition()
    val animation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 9000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            )
        )
    )
    return if (loading) Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(x = animation.value, y = animation.value)
    ) else Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
}
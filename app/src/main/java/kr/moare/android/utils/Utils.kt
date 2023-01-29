package kr.moare.android.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LazyListState.LoadMore(
    buffer : Int = 0,
    onLoadMore : () -> Unit
) {
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?:
                return@derivedStateOf true

            lastVisibleItem.index >=  layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore){
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) onLoadMore() }
    }
}

object DateHelper {
    fun getDays(createdAt: String): String {
        if (createdAt.isNotEmpty()) {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val objDate = formatter.parse(createdAt.split("T").first())
            val nowDate = formatter.parse(formatter.format(Date()))

            val days = ((nowDate.time - objDate.time) / (60 * 60 * 24 * 1000)).toInt()

            if (days == 0) {
                return StringResources.today
            } else if (days < 4) {
                return "${days}일전"
            } else {
                val formatter = SimpleDateFormat("M월d일", Locale.KOREAN)
                val calendar = createdAt.split("T").first().split("-")
                val currentDate = formatter.parse(calendar[1] + "월" + calendar[2] + "일")
                return "${formatter.format(currentDate)}"
            }
        } else {
            return ""
        }
    }
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex < firstVisibleItemIndex
            } else {
                previousScrollOffset <= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex < firstVisibleItemIndex
            } else {
                previousScrollOffset <= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

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
package kr.moare.android.utils.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.moare.android.ui.theme.MoareTheme

@Composable
fun Test() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray),
        ) {
//            Canvas(
//                Modifier
//                    .fillMaxSize()
//                    .wrapContentSize(Alignment.Center)
//                    .size(200.dp)
//                    .alpha(0.99f)
//            ) {
//                val radius = size.minDimension / 4

//                drawRect(
//                    color = Color.White,
//                    size = Size(width = size.width, height = size.height / 2f),
//                    topLeft = Offset(0f, -size.height / 4f),
//                )
//
//                drawRect(
//                    color = Color.Black,
//                    alpha = 0.7f,
//                    size = size,
//                )
//                drawCircle(
//                    Color.Red,
//                    center = Offset(center.x - radius / 2, center.y),
//                    radius = radius,
//                    blendMode = BlendMode.Xor
//                )
//
//                drawCircle(
//                    Color.Blue,
//                    center = Offset(center.x + radius / 2, center.y),
//                    radius = radius,
//                    blendMode = BlendMode.Multiply
//                )
//            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PostDetailViewPreview() {
    MoareTheme {
        Test()
    }
}
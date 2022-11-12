package kr.moare.android.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun SportSelectButton(
    selected: Boolean?,
    sport: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .padding(
                start = 7.dp,
                end = 7.dp,
                bottom = 20.dp
            )
            .height(50.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selected == true) {
                Box(
                    modifier = Modifier
                        .border(
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colors.primary
                            ),
                            RoundedCornerShape(15.dp)
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sport, color = MaterialTheme.colors.primary)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sport)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(top = 10.dp)
                            .clip(RectangleShape)
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}
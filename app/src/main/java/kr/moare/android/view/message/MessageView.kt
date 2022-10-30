package kr.moare.android.view.message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.moare.android.R
import kr.moare.android.ui.theme.MoareTheme

@Composable
fun MessageView() {
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "goBackIcon"
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(CircleShape)
                            .size(30.dp)
                            .background(Color.Gray)
                        )

                        Text(text = "moare",
                            style = MaterialTheme.typography.h6,
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box() {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp)
            ) {
                Row(

                ) {
                    Box(modifier = Modifier
                        .padding(end = 4.dp)
                        .clip(CircleShape)
                        .size(30.dp)
                        .background(Color.Gray)
                    )
                    Column(

                    ) {
                        Text(text = "moare", Modifier.padding(bottom = 4.dp))
                        MessageLeftItem(text = "hi")
                        MessageLeftItem(text = "hello")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(

                    ) {
                        MessageRightItem(text = "hi")
                        MessageRightItem(text = "hello")
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "cameraIcon"
                )
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(30.dp)
                        .weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_up),
                    contentDescription = "sendMessageIcon",
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.dp, MaterialTheme.colors.primary, CircleShape)
                        .padding(4.dp),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
fun MessageLeftItem(text: String) {
    Box() {
        Text(text = text, Modifier.padding(start = 8.dp, top = 6.dp, bottom = 4.dp))
        Row() {
            Box(
                Modifier
                    .clip(RectangleShape)
                    .size(2.dp, 8.dp)
                    .background(Color.Gray)
            )
        }
        Row() {
            Box(
                Modifier
                    .clip(RectangleShape)
                    .size(8.dp, 2.dp)
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
fun MessageRightItem(text: String) {
    Box() {
        Row() {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = text, Modifier.padding(end = 8.dp, top = 6.dp, bottom = 4.dp))
        }
        Row() {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .clip(RectangleShape)
                    .size(2.dp, 8.dp)
                    .background(MaterialTheme.colors.primary)
            )
        }
        Row() {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .clip(RectangleShape)
                    .size(8.dp, 2.dp)
                    .background(MaterialTheme.colors.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageViewPreview() {
    MoareTheme {
        MessageView()
    }
}
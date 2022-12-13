package kr.moare.android.utils.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kr.moare.android.components.StartViewButton
import kr.moare.android.components.StartViewTextField
import kr.moare.android.utils.StartNavItem
import kr.moare.android.viewmodel.start.JoinViewModel

@Composable
fun PhOrEmailView(navController: NavController, joinVM: JoinViewModel) {
    var email by remember { mutableStateOf("") }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val height = this.maxHeight
        val width = this.maxWidth
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .offset(y = 35.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
            ) {
                Button(onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    ),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "전화번호", fontSize = 17.sp, color = Color.Gray)
                }

                Button(onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    ),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "이메일", fontSize = 17.sp, color = Color.Gray)
                }
            }

            Box(modifier = Modifier
                .padding(start = 10.dp)
                .padding(bottom = 10.dp)
                .clip(RectangleShape)
                .background(MaterialTheme.colors.primary)
                .width(width / 2 - 10.dp)
                .height(2.dp)
                .align(Alignment.Start),
            )

//            TextFieldView1(placeholder = "전화번호", text = email, onTextChange = { email = it })
            StartViewTextField(placeholder = "이메일", text = email, onTextChange = { email = it })
            StartViewButton(
                text = "다음",
                onClick = {
                joinVM.account.email = email
//                joinVM.getEmailCode()
                navController.navigate(StartNavItem.Auth.name)
                },
                enabled = false
            )
            Spacer(modifier = Modifier.height(height/2))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PhOrEmailViewPreview() {
//    MoareTheme {
//        PhOrEmailView(navController = rememberNavController(), JoinViewModel())
//    }
//}
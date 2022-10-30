package kr.moare.android.view.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kr.moare.android.components.CircleStartViewButton
import kr.moare.android.components.StartViewTextField
import kr.moare.android.utils.StartNavItem
import kr.moare.android.viewmodel.start.JoinViewModel

@Composable
fun AuthView(navController: NavController, joinVM: JoinViewModel) {
    var clientCode by remember { mutableStateOf("") }
    val showErrorText by joinVM.showErrorText.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val height = this.maxHeight
        val width = this.maxWidth
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .offset(y = 37.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "인증 코드 입력",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "ㅇㅇㅇ@ㅇㅇㅇ으로 전송된\n인증 코드를 입력하세요.",
                style = MaterialTheme.typography.caption,
            )

            Button(onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp
            ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(text = "인증 코드 재전송",
                color = Color.Blue,
                fontSize = 12.sp)
            }

            if (showErrorText) {
                Text(text = "인증번호가 틀립니다.",
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            StartViewTextField(placeholder = "인증 코드", text = clientCode, onTextChange = { clientCode = it })
//            StartViewButton(
//                text = "다음",
//                onClick = {
//                    joinVM.checkCode(clientCode) {
//                        if (it) {
//                            navController.navigate(StartNavItem.Pwd.name)
//                        }
//                    }
//                },
//                enabled = clientCode.isNotEmpty(),
//                width = screenWidth
//            )
            CircleStartViewButton(enabled = clientCode.isNotEmpty()) {
                joinVM.checkCode(clientCode) {
                    if (it) {
                        navController.navigate(StartNavItem.Pwd.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(height/2))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AuthViewPreview() {
//    MoareTheme {
//        AuthView(navController = rememberNavController(), JoinViewModel())
//    }
//}


















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
import kr.moare.android.components.StartViewButton
import kr.moare.android.components.StartViewTextField
import kr.moare.android.utils.StartNavItem
import kr.moare.android.viewmodel.start.JoinViewModel

@Composable
fun AuthView(navController: NavController, joinVM: JoinViewModel) {
    val showErrorText by joinVM.showErrorText.collectAsState()
    val networkError by joinVM.networkError.collectAsState()

    var clientCode by remember { mutableStateOf("") }

    val errorText1 = "인증번호가 틀립니다."
    val errorText2 = "인증번호 전송에 실패하였습니다.\n다시 시도해주세요."

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
                text = "${joinVM.email.value}으로 전송된\n인증 코드를 입력하세요.",
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

            if (showErrorText || networkError) {
                Text(text = if (showErrorText) errorText1 else errorText2,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            StartViewTextField(
                placeholder = "인증 코드",
                text = clientCode,
                textClear = { clientCode = "" },
                onTextChange = { clientCode = it }
            )

            StartViewButton(enabled = clientCode.isNotEmpty()) {
                joinVM.checkCode(clientCode) {
                    navController.navigate(StartNavItem.Pwd.name)
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


















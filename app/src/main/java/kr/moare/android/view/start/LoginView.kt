package kr.moare.android.view.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.moare.android.components.CircleStartViewButton
import kr.moare.android.components.StartViewTextField
import kr.moare.android.viewmodel.start.LoginViewModel

@Composable
fun LoginView(
    navController: NavController,
    loginVM: LoginViewModel
) {
    var email by remember { mutableStateOf("") }
    var pwd by remember { mutableStateOf("") }

    val showErrorText by loginVM.showErrorText.collectAsState()

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
            if (showErrorText) {
                Text(text = "이메일 또는 비밀번호를 확인해주세요",
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            StartViewTextField(placeholder = "이메일", text = email, onTextChange = { email = it })
            StartViewTextField(placeholder = "비밀번호", text = pwd, onTextChange = { pwd = it })
//            StartViewButton(
//                text = "로그인",
//                onClick = {
//                loginVM.login(email, pwd) },
//                enabled = email.isNotEmpty() && pwd.isNotEmpty(),
//                width = screenWidth
//            )
            CircleStartViewButton(
                enabled = email.isNotEmpty() && pwd.isNotEmpty()
            ) {
                loginVM.login(email, pwd)
            }
            Spacer(modifier = Modifier.height(height/2))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginViewPreview() {
//    MoareTheme {
//        LoginView(navController = rememberNavController())
//    }
//}
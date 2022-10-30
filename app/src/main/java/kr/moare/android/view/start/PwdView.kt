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
import kr.moare.android.utils.StartNavItem
import kr.moare.android.viewmodel.start.JoinViewModel

@Composable
fun PwdView(navController: NavController, joinVM: JoinViewModel) {
    val pwd by joinVM.pwd.collectAsState()
    val pwdBtn by joinVM.pwdBtn.collectAsState()
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
                text = "비밀번호 생성",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (showErrorText) {
                Text(text = "비밀번호가 유효하지 않습니다.",
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            StartViewTextField(
                placeholder = "비밀번호",
                text = pwd,
                onTextChange = {
                    joinVM.checkPwd(it)
                }
            )
//            StartViewButton(
//                text = "다음",
//                onClick = {
//                    joinVM.checkPwd(pwd) {
//                        navController.navigate(StartNavItem.Username.name)
//                    }
//                },
//                enabled = pwd.isNotEmpty(),
//                width = screenWidth
//            )
            CircleStartViewButton(pwdBtn) {
                joinVM.addPwd() {
                    navController.navigate(StartNavItem.Username.name)
                }
            }
            Spacer(modifier = Modifier.height(height/2))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PwdViewPreview() {
//    MoareTheme {
//        PwdView(navController = rememberNavController(), JoinViewModel())
//    }
//}
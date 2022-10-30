package kr.moare.android.view.start

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.moare.android.components.CircleStartViewButton
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.components.StartViewTextField
import kr.moare.android.utils.StartNavItem
import kr.moare.android.viewmodel.start.JoinViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@Composable
fun EmailView(
    navController: NavController,
    joinVM: JoinViewModel
) {
//    var email by remember { mutableStateOf("") }
    val email by joinVM.email.collectAsState()
    val emailBtn by joinVM.emailBtn.collectAsState()
    val showErrorText by joinVM.showErrorText.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

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
                .offset(y = 37.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "이메일 주소 입력",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "입력한 이메일 주소로 인증코드가 전송됩니다.",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (showErrorText) {
                Text(text = "이메일 형식을 확인해주세요",
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            StartViewTextField(
                placeholder = "이메일",
                text = email,
                onTextChange = {
                    joinVM.checkEmail(it)
                }
            )
//            StartViewButton(
//                text = "다음",
//                onClick = {
//                    joinVM.account.email = email
//                    joinVM.getEmailCode()
//                    navController.navigate(StartNavItem.Auth.name)
//                },
//                enabled = emailBtn,
//                width = screenWidth
//            )
            CircleStartViewButton(
                enabled = emailBtn,
                checkEmail = { joinVM.showErrorText.value = !emailBtn }
            ) {
                joinVM.account.email = email
                joinVM.getEmailCode()
                navController.navigate(StartNavItem.Auth.name)
            }
            Spacer(modifier = Modifier.height(height/2))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun PhOrEmailViewPreview() {
    val joinVM: JoinViewModel = hiltViewModel()
    MoareTheme {
        EmailView(navController = rememberAnimatedNavController(), joinVM)
    }
}
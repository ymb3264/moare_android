package kr.moare.android.view.start

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kr.moare.android.components.StartViewButton
import kr.moare.android.components.StartViewTextField
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.viewmodel.start.JoinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthView(navController: NavController, joinVM: JoinViewModel) {
    val showErrorText by joinVM.showErrorText.collectAsState()
    val networkError by joinVM.networkError.collectAsState()
    val loading by joinVM.loading.collectAsState()

    var clientCode by remember { mutableStateOf("") }

    val errorText1 = StringResources.wrongAuthCodeError
    val errorText2 = StringResources.failedToSendAuthCode

    val keyboardController = LocalSoftwareKeyboardController.current

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
                .offset(y = 37.dp)
                .noRippleClickable { keyboardController?.hide() },
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
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp))
                } else {
                    Text(text = "인증 코드 재전송",
                        color = MaterialTheme.colors.primary,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            joinVM.getEmailCode()
                        }
                    )
                }
            }

            if (showErrorText || networkError) {
                Text(text = if (showErrorText) errorText1 else errorText2,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            StartViewTextField(
                placeholder = StringResources.authCodePlaceholder,
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


















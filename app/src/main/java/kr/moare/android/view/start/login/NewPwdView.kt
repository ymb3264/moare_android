package kr.moare.android.view.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.moare.android.components.PwdTextField
import kr.moare.android.components.StartViewButton
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.viewmodel.start.JoinViewModel
import kr.moare.android.viewmodel.start.LoginViewModel
import kotlin.math.log

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewPwdView(
    navController: NavController,
    loginVM: LoginViewModel
) {
    val pwd by loginVM.pwd.collectAsState()
    val pwdBtn by loginVM.pwdBtn.collectAsState()
    val loading by loginVM.loading.collectAsState()
    val showErrorText by loginVM.showErrorText.collectAsState()
    val showErrorText2 by loginVM.showErrorText2.collectAsState()

    var pwdForCheck by remember { mutableStateOf("") }

    val errorText1 = StringResources.passwordValidationError
    val errorText2 = StringResources.wrongPasswordForCheck

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
                text = "새로운 비밀번호 생성",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (showErrorText || showErrorText2) {
                Text(text = if (showErrorText) errorText1 else errorText2,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            PwdTextField(
                placeholder = StringResources.passwordPlaceholder,
                text = pwd,
                textClear = { loginVM.pwd.value = "" },
                onTextChange = {
                    loginVM.checkPwd(it)
                }
            )
            PwdTextField(
                placeholder = StringResources.passwordForCheckPlaceholder,
                text = pwdForCheck,
                textClear = { pwdForCheck = "" },
                onTextChange = {
                    pwdForCheck = it
                    if (!showErrorText) {
                        loginVM.checkSecondPwd(pwdForCheck)
                    }
                }
            )

            StartViewButton(
                enabled = pwdBtn,
                loading = loading
            ) {
                loginVM.setNewPwd {
                    // backStack 전부 없애기
                    navController.popBackStack()
                    navController.navigate(StartNavItem.Login.name)
                }
            }
            Spacer(modifier = Modifier.height(height/2))
        }
    }
}
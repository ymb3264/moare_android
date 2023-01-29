package kr.moare.android.view.start.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.moare.android.components.StartViewButton
import kr.moare.android.components.StartViewTextField
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.viewmodel.start.JoinViewModel
import kr.moare.android.viewmodel.start.LoginViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailForNewPwdView(
    navController: NavController,
    loginVM: LoginViewModel
) {
    val email by loginVM.email.collectAsState()
    val emailBtn by loginVM.emailBtn.collectAsState()
    val loading by loginVM.loading.collectAsState()
    val showErrorText by loginVM.showErrorText.collectAsState()
    val networkError by loginVM.networkError.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val height = this.maxHeight
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
                text = StringResources.emailTitle,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = StringResources.emailForNewPwdMessage,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (showErrorText || networkError) {
                Text(text = if (showErrorText) StringResources.emailValidationError else loginVM.networkErrorText,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            StartViewTextField(
                placeholder = StringResources.emailPlaceholder,
                text = email,
                textClear = { loginVM.email.value = "" },
                onTextChange = {
                    loginVM.checkEmail(it)
                }
            )

            StartViewButton(
                enabled = emailBtn,
                loading = loading,
                checkEmail = { loginVM.showErrorText.value = !emailBtn }
            ) {
                loginVM.getEmailCode {
                    navController.navigate(StartNavItem.AuthForNewPwd.name)
                }
            }
            Spacer(modifier = Modifier.height(height/2))
        }
    }
}
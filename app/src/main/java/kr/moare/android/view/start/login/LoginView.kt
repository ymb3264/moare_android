package kr.moare.android.view.start

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kr.moare.android.components.PwdTextField
import kr.moare.android.components.StartViewButton
import kr.moare.android.components.StartViewTextField
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.LoadingNavItem
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.viewmodel.start.LoginViewModel
import kotlin.math.log

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginView(
    navController: NavController,
    loginVM: LoginViewModel,
    loadingNavController: NavController
) {
    var email by remember { mutableStateOf("") }
    var pwd by remember { mutableStateOf("") }

    val showErrorText by loginVM.showErrorText.collectAsState()
    val loading by loginVM.loginLoading.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
            if (showErrorText) {
                Text(text = StringResources.loginError,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            StartViewTextField(
                placeholder = StringResources.emailPlaceholder,
                text = email,
                textClear = { email = "" },
                onTextChange = { email = it }
            )

            PwdTextField(
                placeholder = StringResources.passwordPlaceholder,
                text = pwd,
                textClear = { pwd = "" },
                onTextChange = { pwd = it }
            )

            Row() {
                Spacer(modifier = Modifier.weight(1f))
                StartViewButton(
                    enabled = email.isNotEmpty() && pwd.isNotEmpty(),
                    loading = loading
                ) {
                    loginVM.login(email, pwd) {
                        loadingNavController.navigate(LoadingNavItem.Main.name)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.TopEnd
                ) {
                    TextButton(
                        onClick = { navController.navigate(StartNavItem.EmailForNewPwd.name) }
                    ) {
                        Text(text = StringResources.forgotPassword,
                            color = MaterialTheme.colors.primary,
                            fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(height/2))
        }
    }
}
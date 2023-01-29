package kr.moare.android.view.start

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.moare.android.components.StartViewButton
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.components.StartViewTextField
import kr.moare.android.utils.StartNavItem
import kr.moare.android.viewmodel.start.JoinViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.noRippleClickable

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailView(
    navController: NavController,
    joinVM: JoinViewModel
) {
    val email by joinVM.email.collectAsState()
    val emailBtn by joinVM.emailBtn.collectAsState()
    val loading by joinVM.loading.collectAsState()
    val showErrorText by joinVM.showErrorText.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

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
                text = StringResources.emailMessage,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (showErrorText) {
                Text(text = StringResources.emailValidationError,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            StartViewTextField(
                placeholder = StringResources.emailPlaceholder,
                text = email,
                textClear = { joinVM.email.value = "" },
                onTextChange = {
                    joinVM.checkEmail(it)
                }
            )

            StartViewButton(
                enabled = emailBtn,
                loading = loading,
                checkEmail = { joinVM.showErrorText.value = !emailBtn }
            ) {
                joinVM.getEmailCode {
                    navController.navigate(StartNavItem.Auth.name)
                }
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
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kr.moare.android.components.CircleStartViewButton
import kr.moare.android.utils.StartNavItem
import kr.moare.android.components.StartViewTextField
import kr.moare.android.viewmodel.start.JoinViewModel

@Composable
fun UsernameView(navController: NavController, joinVM: JoinViewModel) {
    val username by joinVM.username.collectAsState()
    val usernameBtn by joinVM.usernameBtn.collectAsState()
    val showErrorText by joinVM.showErrorText.collectAsState()
    val showErrorText2 by joinVM.showErrorText2.collectAsState()

    val errorText1 = "이미 사용중인 이름입니다"
    val errorText2 = "사용자 이름에는 영어 대/소문자, 숫자,\n밑줄(_) 및 마침표(.)만 사용할 수 있습니다."

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
            Text(text = "사용자이름 생성",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 10.dp))

            if (showErrorText || showErrorText2) {
                Text(text = if (showErrorText) errorText2 else errorText1,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp))
            }

            StartViewTextField(
                placeholder = "사용자이름",
                text = username,
                onTextChange = { joinVM.checkUsername(it) }
            )

            CircleStartViewButton(usernameBtn) {
                joinVM.checkUsername2(username) {
                    if (it) {
                        navController.navigate(StartNavItem.SportSelect.name)
                    }
                }
            }
            Spacer(modifier = Modifier.height(height/2))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun UsernameViewPreview() {
//    MoareTheme {
//        UsernameView(navController = rememberNavController(), JoinViewModel())
//    }
//}
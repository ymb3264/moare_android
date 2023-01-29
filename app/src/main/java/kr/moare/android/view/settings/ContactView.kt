package kr.moare.android.view.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kr.moare.android.R
import kr.moare.android.utils.StringResources

@Composable
fun ContactView(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = StringResources.questions)
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        },
    ) {
        Text(text = "문의사항은 ymb3264@naver.com으로 문의주시기 바랍니다.")
    } // scaffold
}
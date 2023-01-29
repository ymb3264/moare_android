package kr.moare.android.view.start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kr.moare.android.R
import kr.moare.android.components.StartViewButton
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.viewmodel.start.JoinViewModel

@Composable
fun LoginInfoSaveView(
    startNavController: NavController,
    loadingNavController: NavController,
    joinVM: JoinViewModel
) {
    val username by joinVM.username.collectAsState()

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
                .offset(y = 37.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "로그인 정보를 저장하시겠습니까?",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "${username}님의 로그인 정보가 저장되므로 해당 기기에서는\n로그인 정보를 입력하지 않아도 됩니다",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(bottom = 12.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .background(Color.Transparent)
                    .weight(1f))

                Box(contentAlignment = Alignment.Center) {
                    StartViewButton(enabled = true) {
                        joinVM.join(true) {
                            startNavController.navigate(StartNavItem.JOINSPLASH.name)
                        }
                    }
                    Text(text = StringResources.save, color = MaterialTheme.colors.primary)
                }

                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .background(Color.Transparent)
                    .weight(1f)
                ) {
                    TextButton(
                        onClick = {
                            joinVM.join(false) {
                                startNavController.navigate(StartNavItem.JOINSPLASH.name)
                            }
                        }
                    ) {
                        Text(text = StringResources.saveLater,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .padding(start = 20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(height/2))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TOSViewPreview() {
//    MoareTheme {
//        LoginInfoSaveView(rememberNavController(), rememberNavController())
//    }
//}
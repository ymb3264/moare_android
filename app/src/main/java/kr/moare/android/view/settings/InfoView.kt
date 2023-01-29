package kr.moare.android.view.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kr.moare.android.R
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.MyProfileNavItem
import kr.moare.android.utils.StringResources

@Composable
fun InfoView(
    myProfileNavController: NavController,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = StringResources.info)
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        },
    ) {
        Column() {
            TextButton(
                onClick = {
                    myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("title", StringResources.tos)
                    myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("url", StringResources.locationTosUrl)
                    myProfileNavController.navigate(MyProfileNavItem.INFODETAIL.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(text = StringResources.tos, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "arrow_right",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            TextButton(
                onClick = {
                    myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("title", StringResources.privacyPolicy)
                    myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("url", StringResources.tosUrl)
                    myProfileNavController.navigate(MyProfileNavItem.INFODETAIL.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(text = StringResources.privacyPolicy, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "arrow_right",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            TextButton(
                onClick = {
                    myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("title", StringResources.locationTos)
                    myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("url", StringResources.privacyPolicyUrl)
                    myProfileNavController.navigate(MyProfileNavItem.INFODETAIL.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(text = StringResources.locationTos, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "arrow_right",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    } // scaffold
}
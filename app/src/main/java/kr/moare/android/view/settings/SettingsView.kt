package kr.moare.android.view.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import kr.moare.android.utils.LoadingNavItem
import kr.moare.android.utils.MyProfileNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.viewmodel.profile.MyProfileViewModel
import kr.moare.android.viewmodel.start.LoginViewModel

@Composable
fun SettingsView(
    loadingNavController: NavController,
    myProfileNavController: NavController,
    profileVM: MyProfileViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = StringResources.settingsNavigationTitle)
                },
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        },
    ) {
        Column() {
            TextButton(
                onClick = { myProfileNavController.navigate(MyProfileNavItem.ACCOUNTINFO.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(text = StringResources.account, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "arrow_right",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            TextButton(
                onClick = { myProfileNavController.navigate(MyProfileNavItem.INFO.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(text = StringResources.info, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "arrow_right",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            TextButton(
                onClick = { myProfileNavController.navigate(MyProfileNavItem.CONTACT.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(text = StringResources.questions, fontSize = 16.sp, color = Color.Black)
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
                    profileVM.logout {
                        loadingNavController.popBackStack()
                        loadingNavController.navigate(LoadingNavItem.StartLoading.name)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Text(text = StringResources.logoutButton, color = MaterialTheme.colors.primary)
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    } // scaffold
}
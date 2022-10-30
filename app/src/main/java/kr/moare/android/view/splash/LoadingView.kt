package kr.moare.android.view.splash

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import kr.moare.android.utils.SplashNavItem
import kr.moare.android.viewmodel.start.LoginViewModel

@Composable
fun LoadingView(
    splashNavController: NavController,
    loginVM: LoginViewModel
) {
    val login by loginVM.login.collectAsState()
    val loading by loginVM.loading.collectAsState()

    if (loading) {
        CircularProgressIndicator()
    } else {
        splashNavController.popBackStack()
        if (login) {
            splashNavController.navigate(SplashNavItem.Main.name)
        } else {
            splashNavController.navigate(SplashNavItem.Start.name)
        }
    }

//    LaunchedEffect(key1 = true) {
//        delay(3000)
//        splashNavController.popBackStack()
//        if (login) {
//            splashNavController.navigate(SplashNavItem.Main.name)
//        } else {
//            splashNavController.navigate(SplashNavItem.Start.name)
//        }
//    }
}
package kr.moare.android.view.navgraph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kr.moare.android.utils.SplashNavItem
import kr.moare.android.view.splash.JoinSplashView
import kr.moare.android.viewmodel.start.JoinViewModel
import kr.moare.android.viewmodel.start.LoginViewModel

@Composable
fun SplashNavGraph(
    postNavController: NavController,
    loginVM: LoginViewModel = hiltViewModel(),
    joinVM: JoinViewModel = hiltViewModel()
) {
    val splashNavController = rememberNavController()

    val login by loginVM.login.collectAsState()
    val loading by loginVM.meLoading.collectAsState()

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = splashNavController,
            startDestination = if (login) SplashNavItem.Main.name else SplashNavItem.Start.name
        ) {
            composable(SplashNavItem.Start.name) {
                StartNavGraph(joinVM, loginVM, splashNavController)
            }
            composable(SplashNavItem.JoinSplash.name) {
                JoinSplashView(splashNavController, joinVM)
            }
            composable(SplashNavItem.Main.name) {
                MainNavGraph(postNavController)
            }
        }
    }

}
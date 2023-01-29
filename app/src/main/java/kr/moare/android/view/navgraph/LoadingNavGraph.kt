package kr.moare.android.view.navgraph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kr.moare.android.utils.LoadingNavItem
import kr.moare.android.viewmodel.start.LoginViewModel

@Composable
fun LoadingNavGraph() {
    val loadingNavController = rememberNavController()

    NavHost(
        navController = loadingNavController,
        startDestination = LoadingNavItem.StartLoading.name
    ) {
        composable(LoadingNavItem.StartLoading.name) {
            StartLoadingView(loadingNavController)
        }

        composable(LoadingNavItem.Start.name) {
            StartNavGraph(loadingNavController)
        }
        composable(LoadingNavItem.Main.name) {
            MainNavGraph(loadingNavController)
        }
    }
}

@Composable
fun StartLoadingView(
    loadingNavController: NavController
) {
    var loginVM: LoginViewModel = hiltViewModel()

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
        LaunchedEffect(null) {
            if (login) {
                loadingNavController.popBackStack()
                loadingNavController.navigate(LoadingNavItem.Main.name)
            } else {
                loadingNavController.popBackStack()
                loadingNavController.navigate(LoadingNavItem.Start.name)
            }
        }
    }

}
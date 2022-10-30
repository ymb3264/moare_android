package kr.moare.android.view.navgraph

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kr.moare.android.utils.StartNavItem
import kr.moare.android.view.start.*
import kr.moare.android.viewmodel.start.JoinViewModel
import kr.moare.android.viewmodel.start.LoginViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StartNavGraph(
    joinVM: JoinViewModel,
    loginVM: LoginViewModel,
    splashNavController: NavController
) {
    val startNavController = rememberAnimatedNavController()

    val start = StartNavItem.Start.name
    val login = StartNavItem.Login.name
    val email = StartNavItem.Email.name
    val auth = StartNavItem.Auth.name
    val pwd = StartNavItem.Pwd.name
    val username = StartNavItem.Username.name
    val sportSelect = StartNavItem.SportSelect.name

    Box() {
        AnimatedNavHost(
            navController = startNavController,
            startDestination = start
        ) {
            composable(
                start,
                enterTransition = {
                    when (initialState.destination.route) {
                        else -> null
                    }
                }
            ) {
                StartView(startNavController)
            }
            composable(
                login,
                enterTransition = {
                    when (initialState.destination.route) {
                        start -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        start -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                LoginView(startNavController, loginVM)
            }
            composable(
                email,
                enterTransition = {
                    when (initialState.destination.route) {
                        start -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        start -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                EmailView(startNavController, joinVM)
            }
            composable(
                auth,
                enterTransition = {
                    when (initialState.destination.route) {
                        email -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        email -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                AuthView(startNavController, joinVM)
            }
            composable(
                pwd,
                enterTransition = {
                    when (initialState.destination.route) {
                        auth -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        auth -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                PwdView(startNavController, joinVM)
            }
            composable(
                username,
                enterTransition = {
                    when (initialState.destination.route) {
                        pwd -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        pwd -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                UsernameView(startNavController, joinVM)
            }
            composable(
                sportSelect,
                enterTransition = {
                    when (initialState.destination.route) {
                        username -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        username -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                SportSelectView(
                    startNavController,
                    splashNavController,
                    joinVM
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun StartViewPreview() {
//    MoareTheme {
//        StartNavigation()
//    }
//}
package kr.moare.android.view.navgraph

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.moare.android.utils.StartNavItem
import kr.moare.android.view.start.*
import kr.moare.android.viewmodel.start.JoinViewModel
import kr.moare.android.viewmodel.start.LoginViewModel
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kr.moare.android.view.splash.JoinSplashView
import kr.moare.android.view.start.login.AuthForNewPwdView
import kr.moare.android.view.start.login.EmailForNewPwdView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StartNavGraph(
    loadingNavController: NavController,
    loginVM: LoginViewModel = hiltViewModel(),
    joinVM: JoinViewModel = hiltViewModel(),
) {
    val startNavController = rememberAnimatedNavController()

    val start = StartNavItem.Start.name
    val login = StartNavItem.Login.name
    val emailForNewPwd = StartNavItem.EmailForNewPwd.name
    val authForNewPwd = StartNavItem.AuthForNewPwd.name
    val newPwd = StartNavItem.NewPwd.name
    val email = StartNavItem.Email.name
    val auth = StartNavItem.Auth.name
    val pwd = StartNavItem.Pwd.name
    val username = StartNavItem.Username.name
    val joinSportAdd = StartNavItem.JoinSportAdd.name
    val tos = StartNavItem.TOS.name
    val loginInfoSave = StartNavItem.LOGININFOSAVE.name
    val joinSplash = StartNavItem.JOINSPLASH.name

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
                },
//                deepLinks =
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
                LoginView(startNavController, loginVM, loadingNavController)
            }
            composable(
                emailForNewPwd,
                enterTransition = {
                    when (initialState.destination.route) {
                        login -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        login -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                EmailForNewPwdView(startNavController, loginVM)
            }
            composable(
                authForNewPwd,
                enterTransition = {
                    when (initialState.destination.route) {
                        emailForNewPwd -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        emailForNewPwd -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                AuthForNewPwdView(startNavController, loginVM)
            }
            composable(
                newPwd,
                enterTransition = {
                    when (initialState.destination.route) {
                        authForNewPwd -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        authForNewPwd -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                NewPwdView(startNavController, loginVM)
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
                joinSportAdd,
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
                JoinSportSelectView(startNavController, joinVM)
            }
            composable(
                tos,
                enterTransition = {
                    when (initialState.destination.route) {
                        joinSportAdd -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        joinSportAdd -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                TermsAgreeView(startNavController, joinVM)
            }
            composable(
                loginInfoSave,
                enterTransition = {
                    when (initialState.destination.route) {
                        tos -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        tos -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                LoginInfoSaveView(startNavController, loadingNavController, joinVM)
            }
            composable(
                joinSplash,
                enterTransition = {
                    when (initialState.destination.route) {
                        loginInfoSave -> slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        loginInfoSave -> slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                        else -> null
                    }
                }
            ) {
                JoinSplashView(loadingNavController, joinVM)
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
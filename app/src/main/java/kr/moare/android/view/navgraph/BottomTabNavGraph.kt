package kr.moare.android.view.navgraph

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kr.moare.android.components.EmptyView
import kr.moare.android.entities.BottomSheet
import kr.moare.android.utils.BottomTabNavItem
import kr.moare.android.utils.MainCurrentBottomSheet
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.view.profile.*
import kr.moare.android.viewmodel.post.PostViewModel
import kr.moare.android.viewmodel.profile.ProfileViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun BottomTabNavGraph(
    mainNavController: NavController,
    bottomSheet: BottomSheet,
    postNavController: NavController
) {
    val postVM: PostViewModel = hiltViewModel()
    val profileVM: ProfileViewModel = hiltViewModel()

    val bottomTabNavController = rememberNavController()

    BottomSheetScaffold(
        scaffoldState = bottomSheet.mainSheetScaffoldState,
        sheetContent = {
            when (bottomSheet.mainSheet) {
                MainCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet,
                    null,
                    null
                )
                MainCurrentBottomSheet.CreateTeamProfile -> {
                    Log.d("dsss", "ssdsf")
                    TeamProfileCreateView(
                        bottomSheet,
                        profileVM
                    )
                }
                MainCurrentBottomSheet.UpdateProfile -> UpdateProfileView(
                    bottomSheet,
                    profileVM
                )
                MainCurrentBottomSheet.UpdateTeamProfile -> UpdateTeamProfileView(
                    bottomSheet,
                    profileVM
                )
                MainCurrentBottomSheet.MyAccounts -> MyAccountsView(
                    bottomSheet,
                    profileVM
                )
                MainCurrentBottomSheet.Empty -> EmptyView()
            }
        },
        sheetPeekHeight = 0.dp
    ) {
        Scaffold(
            bottomBar = { BottomTab(navController = bottomTabNavController) }
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                NavHost(
                    navController = bottomTabNavController,
                    startDestination = BottomTabNavItem.Post.name,
                ) {
                    composable(BottomTabNavItem.Post.name) {
                        PostNavGraph(
                            mainNavController = mainNavController,
                            bottomTabNavController = bottomTabNavController,
                            bottomSheet = bottomSheet,
                            postVM = postVM,
                            postNavController = postNavController
                        )
                    }
                    composable(BottomTabNavItem.MyProfile.name) {
                        MyProfileNavGraph(
                            mainNavController = mainNavController,
                            bottomTabNavController = bottomTabNavController,
                            bottomSheet = bottomSheet,
                            profileVM = profileVM,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomTab(
    navController: NavHostController
) {
    val items = listOf(BottomTabNavItem.Post, BottomTabNavItem.MyProfile)

    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter =  painterResource(id = item.icon!!),
                        contentDescription = item.name,
                        modifier = Modifier
                            .width(26.dp)
                            .height(26.dp)
                    )
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.Gray,
                selected = currentRoute == item.name,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(item.name) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
package kr.moare.android.view.navgraph

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.entities.Post
import kr.moare.android.utils.*
import kr.moare.android.view.message.MessageListView
import kr.moare.android.view.profile.FollowListView
import kr.moare.android.view.profile.MyProfileView
import kr.moare.android.view.profile.UserProfileView
import kr.moare.android.viewmodel.post.PostCreateViewModel
import kr.moare.android.viewmodel.post.PostViewModel
import kr.moare.android.viewmodel.profile.ProfileViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kr.moare.android.entities.Profile
import kr.moare.android.entities.SelectedMedia
import kr.moare.android.view.post.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainNavGraph(postNavController: NavController) {
    val mainNavController = rememberAnimatedNavController()

    val main = MainNavItem.MAIN.name
    val addPost = MainNavItem.POSTCREATE.name
    val messageList = MainNavItem.MESSAGELIST.name

    // 여기서 subBottomSheet만해서 bottomSheet을 선언하고 BottomTabNavGraph에서 mainBottomSheet을 넣을 수도 있다.
    val coroutineScope = rememberCoroutineScope()
    val mainBottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val subBottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    var mainCurrentBottomSheet: MainCurrentBottomSheet by remember { mutableStateOf(MainCurrentBottomSheet.Empty) }
    var subCurrentBottomSheet: SubCurrentBottomSheet by remember { mutableStateOf(SubCurrentBottomSheet.Empty) }

    if (mainBottomSheetScaffoldState.bottomSheetState.isCollapsed) { mainCurrentBottomSheet = MainCurrentBottomSheet.Empty }
    if (subBottomSheetScaffoldState.bottomSheetState.isCollapsed) { subCurrentBottomSheet = SubCurrentBottomSheet.Empty }

    val bottomSheet by remember { mutableStateOf(BottomSheet(
        mainBottomSheetScaffoldState,
        subBottomSheetScaffoldState,
        mainCurrentBottomSheet,
        subCurrentBottomSheet,
        coroutineScope,
    )) }

    AnimatedNavHost(
        navController = mainNavController,
        startDestination = main
    ) {
        composable(
            main,
            enterTransition = {
                when (initialState.destination.route) {
                    else -> null
                }
            }
        ) {
            BottomTabNavGraph(mainNavController, bottomSheet, postNavController)
        }
        composable(
            addPost,
            enterTransition = {
                when (initialState.destination.route) {
                    main -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    main -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            PostCreateNavGraph(bottomSheet, mainNavController)
        }
        composable(
            messageList,
            enterTransition = {
                when (initialState.destination.route) {
                    main -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    main -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            MessageListView(mainNavController)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun PostNavGraph(
    mainNavController: NavController,
    bottomTabNavController: NavController,
    bottomSheet: BottomSheet,
    postVM: PostViewModel,
    postNavController: NavController
) {
    val post = PostNavItem.POST.name
    val postDetail = PostNavItem.POSTDETAIL.name
    val userProfile = UserProfileNavItem.USERPROFILE.name
    val followList = UserProfileNavItem.FOLLOWLIST.name

    AnimatedNavHost(
        navController = postNavController as NavHostController,
        startDestination = post
    ) {
        composable(
            post,
            enterTransition = {
                when (initialState.destination.route) {
                    else -> null
                }
            }
        ) {
            PostView(
                mainNavController = mainNavController,
                bottomTabNavController = bottomTabNavController,
                postNavController = postNavController,
                bottomSheet = bottomSheet,
                postVM = postVM,
            )
        }

        composable(
            "$postDetail/{number}",
            enterTransition = {
                when (initialState.destination.route) {
                    post -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    post -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            arguments = listOf(navArgument("number") { type = NavType.IntType }),
            deepLinks = listOf(navDeepLink { uriPattern = "http://10.0.2.2:8080/{number}" })
//            deepLinks = listOf(navDeepLink { uriPattern = "test://test/{number}" })
        ) {
            val post = postNavController.previousBackStackEntry?.savedStateHandle?.get<Post>("post")
                ?: Post("", "", "", "", listOf(), "", listOf(), "", "", "", null)
            val listIndex = postNavController.previousBackStackEntry?.savedStateHandle?.get<Int>("listIndex") ?: 0
            val postIndex = postNavController.previousBackStackEntry?.savedStateHandle?.get<Int>("postIndex") ?: 0
            val number = it.arguments?.getInt("number")
            PostDetailView(navController = postNavController, postVM = postVM, post = post, listIndex = listIndex, postIndex = postIndex)
        }

        composable(
            "$userProfile/{username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    followList -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    followList -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            UserProfileView(mainNavController, postNavController)
        }

        composable(
            "$followList/{page}",
            arguments = listOf(
                navArgument("page") {
                    type = NavType.IntType
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    userProfile -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    userProfile -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            val page = it.arguments?.getInt("page") ?: 0
            val profile = postNavController.previousBackStackEntry?.savedStateHandle?.get<Profile>("profile") ?:
            Profile(createdAt = "", username = "", sportHashtag = listOf(), name = "", profileImage = "", content = "", place = "", isTeam = false)
            FollowListView(
                navController = postNavController,
                profile = profile,
                page = page
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun PostCreateNavGraph(
    bottomSheet: BottomSheet,
    mainNavController: NavController
) {
    val postCreateNavController = rememberAnimatedNavController()

    val postCreateVM: PostCreateViewModel = hiltViewModel()

    val postCreate = PostCreateNavItem.POSTCREATE.name
    val postCreateDetail = PostCreateNavItem.POSTCCREATEDETAIL.name

    AnimatedNavHost(
        navController = postCreateNavController,
        startDestination = postCreate
    ) {
        composable(
            postCreate,
            enterTransition = {
                when (initialState.destination.route) {
                    else -> null
                }
            }
        ) {
            PostCreateView(
                bottomSheet,
                mainNavController,
                postCreateNavController,
                postCreateVM
            )
        }
        composable(
            postCreateDetail,
            enterTransition = {
                when (initialState.destination.route) {
                    postCreate -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    postCreate -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            val selectedMediaList = postCreateNavController.previousBackStackEntry?.savedStateHandle?.get<Array<SelectedMedia>>("selectedMediaList") ?: arrayOf()
            PostCreateDetailView(postCreateNavController, postCreateVM, selectedMediaList)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MyProfileNavGraph(
    mainNavController: NavController,
    bottomTabNavController: NavController,
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel,
) {
    val myProfileNavController = rememberAnimatedNavController()

    val myProfile = MyProfileNavItem.MYPROFILE.name
    val userProfile = UserProfileNavItem.USERPROFILE.name
    val followList = UserProfileNavItem.FOLLOWLIST.name

    AnimatedNavHost(
        navController = myProfileNavController,
        startDestination = myProfile
    ) {
        composable(
            myProfile,
            enterTransition = {
                when (initialState.destination.route) {
                    else -> null
                }
            }
        ) {
            MyProfileView(
                mainNavController = mainNavController,
                myProfileNavController = myProfileNavController,
                profileVM = profileVM,
                bottomSheet = bottomSheet
            )
        }

        composable(
            "$followList/{page}",
            arguments = listOf(
                navArgument("page") {
                    type = NavType.IntType
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    myProfile, "$userProfile/{username}" -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
//            exitTransition = {
//                when (targetState.destination.route) {
//                    else -> null
//                }
//            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    else -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            }
        ) {
            val page = it.arguments?.getInt("page") ?: 0
            val profile = myProfileNavController.previousBackStackEntry?.savedStateHandle?.get<Profile>("profile") ?:
            Profile(createdAt = "", username = "", sportHashtag = listOf(), name = "", profileImage = "", content = "", place = "", isTeam = false)
            FollowListView(
                navController = myProfileNavController,
                profile = profile,
                page = page
            )
        }

        composable(
            "$userProfile/{username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    "$followList/{page}" -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
//            exitTransition = {
//                when (targetState.destination.route) {
//                    else -> null
//                }
//            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    else -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                }
            }
        ) {
            UserProfileView(mainNavController, myProfileNavController)
        }
    }
}




















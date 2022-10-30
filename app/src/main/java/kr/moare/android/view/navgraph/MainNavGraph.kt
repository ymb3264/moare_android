package kr.moare.android.view.navgraph

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.entities.MediaUrl
import kr.moare.android.entities.Post
import kr.moare.android.utils.*
import kr.moare.android.view.message.MessageListView
import kr.moare.android.view.post.PostAddDetailView
import kr.moare.android.view.post.PostAddView
import kr.moare.android.view.post.PostDetailView
import kr.moare.android.view.post.PostView
import kr.moare.android.view.profile.FollowListView
import kr.moare.android.view.profile.MyProfileView
import kr.moare.android.view.profile.TeamProfileView
import kr.moare.android.view.profile.UserProfileView
import kr.moare.android.viewmodel.post.PostAddViewModel
import kr.moare.android.viewmodel.post.PostViewModel
import kr.moare.android.viewmodel.profile.ProfileViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainNavGraph(postNavController: NavController) {
    val mainNavController = rememberAnimatedNavController()

    val main = MainNavItem.MAIN.name
    val addPost = MainNavItem.ADDPOST.name
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
            AddPostNavGraph(bottomSheet, mainNavController)
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
//    val postNavController = rememberAnimatedNavController()

    val post = PostNavItem.POST.name
    val postDetail = PostNavItem.POSTDETAIL.name

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
                ?: Post("", "", "", MediaUrl(listOf(), listOf()),
                    "", mutableListOf(), "", "", "")
            val number = it.arguments?.getInt("number")
            PostDetailView(navController = postNavController, post = post, number = number)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun AddPostNavGraph(
    bottomSheet: BottomSheet,
    mainNavController: NavController
) {
    val addPostNavController = rememberAnimatedNavController()

    val postAddVM: PostAddViewModel = hiltViewModel()

    val addPost = AddPostNavItem.ADDPOST.name
    val addPostDetail = AddPostNavItem.ADDPOSTDETAIL.name

    AnimatedNavHost(
        navController = addPostNavController,
        startDestination = addPost
    ) {
        composable(
            addPost,
            enterTransition = {
                when (initialState.destination.route) {
                    else -> null
                }
            }
        ) {
            PostAddView(
                bottomSheet,
                mainNavController,
                addPostNavController,
                postAddVM
            )
        }
        composable(
            addPostDetail,
            enterTransition = {
                when (initialState.destination.route) {
                    addPost -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    addPost -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
//            val selectedMediaList = addPostNavController.previousBackStackEntry?.savedStateHandle?.get<Array<SelectedMedia>>("mediaUriList") ?: arrayOf()
            PostAddDetailView(addPostNavController, postAddVM)
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
    val followList = MyProfileNavItem.FOLLOWLIST.name
    val userProfile = MyProfileNavItem.USERPROFILE.name
    val teamProfile = MyProfileNavItem.TEAMPROFILE.name

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
                    myProfile -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    myProfile -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            val page = it.arguments?.getInt("page") ?: 0
            FollowListView(
                navController = myProfileNavController,
                profileVM = profileVM,
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
            UserProfileView(myProfileNavController)
        }

        composable(
            "$teamProfile/{username}",
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
            TeamProfileView(myProfileNavController)
        }
    }
}




















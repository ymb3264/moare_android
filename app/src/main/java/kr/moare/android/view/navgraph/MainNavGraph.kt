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
import kr.moare.android.entities.Post
import kr.moare.android.utils.*
import kr.moare.android.view.message.MessageListView
import kr.moare.android.view.profile.FollowListView
import kr.moare.android.view.profile.MyProfileView
import kr.moare.android.view.profile.UserProfileView
import kr.moare.android.viewmodel.post.PostCreateViewModel
import kr.moare.android.viewmodel.post.PostViewModel
import kr.moare.android.viewmodel.profile.MyProfileViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kr.moare.android.entities.Profile
import kr.moare.android.entities.SelectedMedia
import kr.moare.android.view.post.*
import kr.moare.android.view.profile.MyProfilePostDetailView
import kr.moare.android.view.settings.*
import kr.moare.android.viewmodel.start.LoginViewModel
import kotlin.math.log

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainNavGraph(
    loadingNavController: NavController
) {
    val mainNavController = rememberAnimatedNavController()
    val myProfileNavController = rememberAnimatedNavController()

    val main = MainNavItem.MAIN.name
    val postCreate = MainNavItem.POSTCREATE.name
    val postCreateDetail = MainNavItem.POSTCREATEDETAIL.name
    val messageList = MainNavItem.MESSAGELIST.name
    val postUpdate = MainNavItem.POSTUPDATE.name
    val postUpdateDetail = MainNavItem.POSTUPDATEDETAIL.name

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

    val profileVM: MyProfileViewModel = hiltViewModel()
    val postCreateVM: PostCreateViewModel = hiltViewModel()

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
            BottomTabNavGraph(mainNavController, myProfileNavController, bottomSheet, loadingNavController, profileVM)
        }

        composable(
            postCreate,
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
//            PostCreateNavGraph(bottomSheet, mainNavController)
            PostCreateView(
                bottomSheet,
                mainNavController,
                postCreateVM,
                profileVM
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
            val selectedMediaList = mainNavController.previousBackStackEntry?.savedStateHandle?.get<Array<SelectedMedia>>("selectedMediaList") ?: arrayOf()
            PostCreateDetailView(mainNavController, postCreateVM, selectedMediaList)
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
            MessageListView(mainNavController, profileVM)
        }

        composable(
            postUpdate,
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
            PostUpdateView(
                bottomSheet = bottomSheet,
                mainNavController = mainNavController,
                profileVM = profileVM,
                myProfileNavController = myProfileNavController
            )
        }

        composable(
            postUpdateDetail,
            enterTransition = {
                when (initialState.destination.route) {
                    postUpdate -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    postUpdate -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {

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
    postNavController: NavHostController
) {
    val post = PostNavItem.POST.name
    val postDetail = PostNavItem.POSTDETAIL.name
    val userProfile = UserProfileNavItem.USERPROFILE.name
    val followList = UserProfileNavItem.FOLLOWLIST.name
    val deepLinkPostDetail = PostNavItem.DEEPLINKPOSTDETAIL.name

    AnimatedNavHost(
        navController = postNavController,
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
            "$postDetail",
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
            }
        ) {
            val post = postNavController.previousBackStackEntry?.savedStateHandle?.get<Post>("post")
                ?: Post("", "", "", "", "", "", listOf(), "", listOf(), "", "", "")
            val listIndex = postNavController.previousBackStackEntry?.savedStateHandle?.get<Int>("listIndex") ?: 0
            val postIndex = postNavController.previousBackStackEntry?.savedStateHandle?.get<Int>("postIndex") ?: 0
            PostDetailView(postNavController = postNavController, postVM = postVM, post = post, listIndex = listIndex, postIndex = postIndex)
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

        composable(
            "https://www.moare.kr/post/one?yearAndMonth={yearAndMonth}&postCreatedAt={postCreatedAt}",
            exitTransition = {
                when (targetState.destination.route) {
                    post -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            arguments = listOf(
                navArgument("yearAndMonth") { type = NavType.StringType },
                navArgument("postCreatedAt") { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "https://www.moare.kr/post/one?yearAndMonth={yearAndMonth}&postCreatedAt={postCreatedAt}" })
        ) {
            DeepLinkPostDetailView(postNavController)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MyProfileNavGraph(
    mainNavController: NavController,
    bottomTabNavController: NavController,
    bottomSheet: BottomSheet,
    profileVM: MyProfileViewModel,
    loadingNavController: NavController,
    myProfileNavController: NavHostController
) {
    val myProfile = MyProfileNavItem.MYPROFILE.name
    val postDetail = MyProfileNavItem.POSTDETAIL.name
    val userProfile = UserProfileNavItem.USERPROFILE.name
    val followList = UserProfileNavItem.FOLLOWLIST.name
    val settings = MyProfileNavItem.SETTINGS.name
    val accountInfo = MyProfileNavItem.ACCOUNTINFO.name
    val info = MyProfileNavItem.INFO.name
    val contact = MyProfileNavItem.CONTACT.name
    val infoDetail = MyProfileNavItem.INFODETAIL.name

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
            postDetail,
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
            val post = myProfileNavController.previousBackStackEntry?.savedStateHandle?.get<Post>("post")
                ?: Post("", "", "", "", "", "", listOf(), "", listOf(), "", "", "")
            val listIndex = myProfileNavController.previousBackStackEntry?.savedStateHandle?.get<Int>("listIndex") ?: 0
            val postIndex = myProfileNavController.previousBackStackEntry?.savedStateHandle?.get<Int>("postIndex") ?: 0

            MyProfilePostDetailView(
                bottomSheet = bottomSheet,
                mainNavController = mainNavController,
                myProfileNavController = myProfileNavController,
                profileVM = profileVM,
                post = post,
                listIndex = listIndex,
                postIndex = postIndex
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
            },
//            deepLinks =
        ) {
            UserProfileView(mainNavController, myProfileNavController)
        }

        composable(
            settings,
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
            SettingsView(loadingNavController, myProfileNavController, profileVM)
        }

        composable(
            accountInfo,
            enterTransition = {
                when (initialState.destination.route) {
                    settings -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    settings -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            AccountInfoView(loadingNavController, myProfileNavController, profileVM)
        }

        composable(
            info,
            enterTransition = {
                when (initialState.destination.route) {
                    settings -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    settings -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            InfoView(myProfileNavController)
        }

        composable(
            infoDetail,
            enterTransition = {
                when (initialState.destination.route) {
                    info -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    info -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            val title = myProfileNavController.previousBackStackEntry?.savedStateHandle?.get<String>("title") ?: ""
            val url = myProfileNavController.previousBackStackEntry?.savedStateHandle?.get<String>("url") ?: ""
            InfoDetailView(title, url)
        }

        composable(
            contact,
            enterTransition = {
                when (initialState.destination.route) {
                    settings -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    settings -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )
                    else -> null
                }
            }
        ) {
            ContactView(myProfileNavController)
        }
    }
}



















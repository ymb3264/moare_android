package kr.moare.android.view.post

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import kr.moare.android.R
import kr.moare.android.components.PostListView
import kr.moare.android.components.SearchBar
import kr.moare.android.entities.BottomSheet
import kr.moare.android.utils.*
import kr.moare.android.viewmodel.common.SearchViewModel
import kr.moare.android.viewmodel.post.PostViewModel

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PostView(
    mainNavController: NavController,
    bottomTabNavController: NavController,
    postNavController: NavController,
    bottomSheet: BottomSheet,
    postVM: PostViewModel,
    searchVM: SearchViewModel = hiltViewModel(),
) {
    val postList by postVM.postsList.collectAsState()
    val showSearchView by postVM.showSearchView.collectAsState()
    val loading by postVM.loading.collectAsState()
    val noPost by postVM.noPost.collectAsState()

    val currentLocation by postVM.currentLocationFlow.collectAsState("")

    val searchList by searchVM.searchList.collectAsState()

    val shouldSetLocation by postVM.shouldSetLocation.collectAsState()

    var query by remember { mutableStateOf("") }
    var alert by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()
    val scrollingUp = listState.isScrollingUp()
    var offset by remember { mutableStateOf(0) }
    var newOffset by remember { mutableStateOf(0) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (currentLocation.isNotEmpty()) {
                        val placeArr = currentLocation?.split(" ")
                        val placeText = placeArr?.get(placeArr.lastIndex-1)

                        TextButton(
                            onClick = {
                                bottomSheet.mainOpenSheet(MainCurrentBottomSheet.LocationList)
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                        ) {
                            Text(text = placeText!!, style = MaterialTheme.typography.subtitle1)
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_down),
                                contentDescription = "locationList"
                            )
                        }
                    }
                },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    if (showSearchView) {
                        SearchBar(modifier = Modifier
                            .weight(1f),
                            placeholder = StringResources.search,
                            text = query,
                            textClear = { query = "" },
                            onTextChange = {
                                query = it
                                searchVM.search(query)
                            }
//                            keyboardActions = KeyboardActions(onSearch = {
//                                searchVM.search(query)
//                                keyboardController?.hide()
//                            })
                        )
                        TextButton(
                            onClick = { postVM.showSearchView.value = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                        ) {
                           Text(text = StringResources.cancel)
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "searchIcon",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    postVM.showSearchView.value = true
                                },
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "addIcon",
                            modifier = Modifier
                                .padding(start = 10.dp, end = 5.dp)
                                .size(28.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    if (currentLocation.isEmpty()) {
                                        alert = true
                                    } else {
                                        mainNavController.navigate(MainNavItem.POSTCREATE.name)
                                    }
                                },
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            if (shouldSetLocation) {
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = StringResources.setCurrentLocationMessage,
                        color = Color.Gray,
                        textAlign = TextAlign.Center)

                    TextButton(
                        onClick = { bottomSheet.mainOpenSheet(MainCurrentBottomSheet.FindLocation) },
                        modifier = Modifier,
                    ) {
                        Text(text = StringResources.setCurrentLocation)
                    }
                }
            } else {
//                    if (scrollingUp) {
//                        offset = listState.firstVisibleItemScrollOffset
//                        if (((offset - newOffset) / 200) > 0) {
//                            postVM.getMorePost()
//                            newOffset = offset
//                        }
//                    } else {
//                        offset = listState.firstVisibleItemScrollOffset
//                        newOffset = offset
//                    }

                if (noPost) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = StringResources.noPostInCurrentLocation)
                    }
                } else {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(loading),
                        onRefresh = {
//                            postVM.getPosts()
                        }
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            state = listState
                        ) {
                            PostListView(postList, postNavController)
                        }

                    } // swipeRefresh
                }
            } // if else

            if (alert) {
                AlertDialog(
                    onDismissRequest = { alert = false },
                    confirmButton = {
                        TextButton(onClick = {
                            alert = false
                        }) {
                            Text(text = StringResources.confirm)
                        }
                    },
                    title = { Text(text = StringResources.createPostAlertTitle) },
                    text = {
                        Text(text = StringResources.createdPostAlertMessage)
                    }
                )
            }

            // SearchView
            if (showSearchView) {
                PostSearchView(searchList = searchList, postNavController = postNavController)
            }
        } // Box
    } // scaffold

    if (bottomSheet.mainSheet == MainCurrentBottomSheet.LocationList) {
        Box(Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { bottomSheet.mainCloseSheet() }
        )
    }

    listState.LoadMore(buffer = 2) {
        postVM.getMorePost()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PostViewPreview() {
//    MoareTheme {
//        PostView(navController = rememberNavController())
//    }
//}
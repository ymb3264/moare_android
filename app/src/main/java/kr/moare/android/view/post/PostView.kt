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
import kr.moare.android.utils.MainCurrentBottomSheet
import kr.moare.android.utils.MainNavItem
import kr.moare.android.utils.SplashNavItem
import kr.moare.android.utils.isScrollingUp
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
                            placeholder = "검색",
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
                           Text(text = "취소")
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
        Box() {
            if (currentLocation.isEmpty()) {
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            postVM.removeToken()
                        },
                        modifier = Modifier
                            .padding(top = 15.dp),
                    ) {
                        Text(text = "토큰 삭제")
                    }

                    Text(text = "운동을 즐기시는\n지역을 설정해주세요.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center)

                    TextButton(
                        onClick = { bottomSheet.mainOpenSheet(MainCurrentBottomSheet.FindLocation) },
                        modifier = Modifier,
                    ) {
                        Text(text = "지역 설정하기")
                    }
                }
            } else {
                Column(Modifier.padding(padding)) {
                    if (scrollingUp) {
                        offset = listState.firstVisibleItemScrollOffset
                        if (((offset - newOffset) / 200) > 0) {
                            postVM.getMorePost()
                            newOffset = offset
                        }
                    } else {
                        offset = listState.firstVisibleItemScrollOffset
                        newOffset = offset
                    }

                    SwipeRefresh(
                        state = rememberSwipeRefreshState(loading),
                        onRefresh = {
//                            postVM.getPosts()
                        }
                    ) {
                        if (noPost) {
                            Text(text = "주변 지역에 게시물이 없습니다.",
                                modifier = Modifier.fillMaxWidth())
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                state = listState
                            ) {
                                PostListView(postList, postNavController)
                            }
                        }
                    } // swipeRefresh
                } // column
            } // if else

            if (alert) {
                AlertDialog(
                    onDismissRequest = { alert = false },
                    confirmButton = {
                        TextButton(onClick = {
                            alert = false
                        }) {
                            Text(text = "확인")
                        }
                    },
                    title = { Text(text = "게시물 작성") },
                    text = {
                        Text(text = "지역을 설정해야 게시물 작성을 할 수 있습니다.")
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
}

//@Preview(showBackground = true)
//@Composable
//fun PostViewPreview() {
//    MoareTheme {
//        PostView(navController = rememberNavController())
//    }
//}
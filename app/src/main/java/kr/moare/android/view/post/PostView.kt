package kr.moare.android.view.post

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.moare.android.R
import kr.moare.android.components.SearchBar
import kr.moare.android.components.PostListView
import kr.moare.android.entities.BottomSheet
import kr.moare.android.utils.*
import kr.moare.android.viewmodel.post.PostViewModel
import kr.moare.android.viewmodel.common.SearchViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

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
    val scrollState = rememberScrollState()
    var query by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val postList by postVM.postList.collectAsState()
    val showSearchView by postVM.showSearchView.collectAsState()
    val searchList by searchVM.searchList.collectAsState()

    val place by postVM.placeFlow.collectAsState("")
    val x by postVM.xFlow.collectAsState("")
    val y by postVM.yFlow.collectAsState("")

    val listState = rememberLazyListState()
    val scrollingUp = listState.isScrollingUp()
    var offset by remember { mutableStateOf(0) }
    var newOffset by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    val isRefreshing by postVM.isRefreshing.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    if (showSearchView) {
                        SearchBar(modifier = Modifier
                            .weight(1f),
                            placeholder = "검색",
                            text = query, 
                            onTextChange = {
                                query = it
                                searchVM.search(query)
                            },
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
                                    mainNavController.navigate(MainNavItem.POSTCREATE.name)
                                },
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box() {
            if (place == "") {
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(
                        onClick = { bottomSheet.mainOpenSheet(MainCurrentBottomSheet.FindLocation) },
                        modifier = Modifier
                            .padding(top = 15.dp),
                    ) {
                        Text(text = "지역 설정하기")
                    }
                    TextButton(
                        onClick = {
                                  postVM.removeToken()
                        },
                        modifier = Modifier
                            .padding(top = 15.dp),
                    ) {
                        Text(text = "토큰 삭제")
                    }
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                postVM.deletePlace()
                            }
                        },
                    ) {
                        Text(text = "지역 삭제")
                    }
                }
            } else {
                Column(Modifier.padding(padding)) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                postVM.deletePlace()
                            }
                        },
                    ) {
                        Text(text = "지역 삭제")
                    }
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
                        state = rememberSwipeRefreshState(isRefreshing),
                        onRefresh = {
                            coroutineScope.launch { postVM.getAllPost() }
                        }
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            state = listState
                        ) {
                            PostListView(postList, context, postNavController)
                        }
                    }
                }
            } // if

            // SearchView
            if (showSearchView) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(searchList) {
                            if (it.startsWith("#")) {
                                TextButton(
                                    onClick = { },
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(text = it, modifier = Modifier.padding(start = 10.dp))
                                    Spacer(Modifier.weight(1f))
                                }
                            } else {
                                TextButton(
                                    onClick = {
                                        postNavController.navigate("${PostNavItem.USERPROFILE.name}/$it")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(start = 10.dp),
                                        fontSize = 18.sp
                                    )
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        } // Box
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PostViewPreview() {
//    MoareTheme {
//        PostView(navController = rememberNavController())
//    }
//}
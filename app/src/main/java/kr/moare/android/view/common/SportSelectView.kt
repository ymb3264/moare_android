package kr.moare.android.view.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.moare.android.components.SearchBar
import kr.moare.android.components.SelectedSportHashtag
import kr.moare.android.components.SportSelectButton
import kr.moare.android.entities.BottomSheet
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.isScrollingUp
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.viewmodel.common.SportSelectViewModel
import kr.moare.android.viewmodel.common.SearchViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SportSelectView(
    bottomSheet: BottomSheet,
    sportSelectVM: SportSelectViewModel = hiltViewModel(),
    addSport: (List<String>, List<String>) -> Unit = { _,_ -> }
) {
    var query by remember { mutableStateOf("") }

    val loading by sportSelectVM.loading.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val sportList by sportSelectVM.sportList.collectAsState()

    val newSportList by sportSelectVM.newSportList.collectAsState()
    val selectedSport by sportSelectVM.selectedSport.collectAsState()

    BackHandler() {
        bottomSheet.subCloseSheet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = StringResources.sportSelectNavigationTitle) },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
                            bottomSheet.subCloseSheet()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Text(text = StringResources.cancel)
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .noRippleClickable { keyboardController?.hide() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SearchBar(modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
                    placeholder = StringResources.search,
                    text = query,
                    isfocused = false,
                    textClear = { query = "" },
                    onTextChange = {
                        query = it
                        sportSelectVM.searchSport(it)
                    },
                    keyboardActions = KeyboardActions(onSearch = {
                        sportSelectVM.searchSport(query)
                        keyboardController?.hide()
                    })
                )
                Button(
                    onClick = {
                        addSport(selectedSport, sportSelectVM.userHashtag.value)
                        bottomSheet.subCloseSheet()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(text = StringResources.complete)
                }
            }

            LazyRow(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                items(selectedSport) {
                    SelectedSportHashtag(it)
                }
            }

            if (loading) {
                CircularProgressIndicator()
            } else {
                if (query == "") {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .weight(0.7f),
//                    state = scrollState
                    ) {
                        items(sportList.keys.toList()) { sport ->
                            SportSelectButton(selected = sportList[sport], sport = sport) {
                                sportSelectVM.selectSport(sport)
                            }
                        }
                    }
                } else {
                    if (newSportList.isEmpty()) {
                        Text(
                            text = StringResources.add,
                            modifier = Modifier.clickable {
                                sportSelectVM.userHashtag.value.add("#${query}")
                                sportSelectVM.selectedSport.value.add("#${query}")
                                query = ""
                            },
                            color = MaterialTheme.colors.primary
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .weight(0.7f)
                        ) {
                            items(newSportList.keys.toList()) { sport ->
                                SportSelectButton(selected = sportList[sport], sport = sport) {
                                    sportSelectVM.newSelectSport(sport)
                                }
                            }
                        }
                    }
                }
            } // loading
        } // column
    }
}

//    val scrollState = rememberLazyGridState()
//    var scrollEnabled by remember { mutableStateOf(true) }

//    if (!scrollState.isScrollingUp()) {
//        if (scrollState.firstVisibleItemScrollOffset == 0) {
//            scrollEnabled = false
//        }
//    }

fun Modifier.scrollEnabled(
    enabled: Boolean,
) = nestedScroll(
    connection = object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset = if(enabled) Offset.Zero else available
    }
)
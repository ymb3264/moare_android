package kr.moare.android.view.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.moare.android.components.SearchBar
import kr.moare.android.components.SelectedSportHashtag
import kr.moare.android.components.SportSelectButton
import kr.moare.android.entities.BottomSheet
import kr.moare.android.viewmodel.common.SportSelectViewModel
import kr.moare.android.viewmodel.common.SearchViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SportAddView(
    bottomSheet: BottomSheet,
    sportSelectVM: SportSelectViewModel = hiltViewModel(),
    addSport: (List<String>) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }

    val loading by sportSelectVM.loading.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val sportList by sportSelectVM.sportList.collectAsState()
    val newSportList by sportSelectVM.newSportList.collectAsState()
    val selectedSport by sportSelectVM.selectedSport.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "스포츠 종목 추가") },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = { bottomSheet.subCloseSheet() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Text(text = "취소")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SearchBar(modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
                    placeholder = "검색",
                    text = query,
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
                        addSport(sportSelectVM.selectedSport.value)
                        bottomSheet.subCloseSheet()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(text = "완료")
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(0.7f)
                ) {
                    if (query == "") {
                        items(sportList.keys.toList()) { sport ->
                            SportSelectButton(selected = sportList[sport], sport = sport) {
                                sportSelectVM.selectSport(sport)
                            }
                        }
                    } else {
                        items(newSportList.keys.toList()) { sport ->
                            SportSelectButton(selected = sportList[sport], sport = sport) {
                                sportSelectVM.newSelectSport(sport)
                            }
                        }
                    }
                }
            } // loading

//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//            ) {
//                items(searchList) {
//                    TextButton(
//                        onClick = {
//                            if (addPostVM != null) {
//                                addPostVM.sport.value.add(it)
//                                addPostVM.post.sport.add(it)
//                                bottomSheet.subCloseSheet()
//                            }
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(36.dp),
//                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
//                        contentPadding = PaddingValues(0.dp)
//                    ) {
//                        Text(text = it, modifier = Modifier.padding(start = 10.dp))
//                        Spacer(Modifier.weight(1f))
//                    }
//                }
//            }
        }
    }
}

//@OptIn(ExperimentalMaterialApi::class)
//@Preview(showBackground = true)
//@Composable
//fun SearchViewPreview() {
//    MoareTheme {
//        SearchView(bottomSheetScaffoldState = rememberBottomSheetScaffoldState())
//    }
//}
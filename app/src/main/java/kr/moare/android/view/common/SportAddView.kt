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
import kr.moare.android.entities.BottomSheet
import kr.moare.android.viewmodel.common.SportSelectViewModel
import kr.moare.android.viewmodel.post.PostCreateViewModel
import kr.moare.android.viewmodel.search.SearchViewModel
import kr.moare.android.viewmodel.profile.ProfileViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SportAddView(
    bottomSheet: BottomSheet,
    postAddVM: PostCreateViewModel?,
    profileVM: ProfileViewModel?,
    sportSelectVM: SportSelectViewModel = hiltViewModel(),
    searchVM: SearchViewModel = hiltViewModel(),
) {
    var query by remember { mutableStateOf("") }

    val loading by sportSelectVM.loading.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val searchList by searchVM.searchList.collectAsState()
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
                    onTextChange = { query = it },
                    keyboardActions = KeyboardActions(onSearch = {
                        searchVM.search("#$query")
                        keyboardController?.hide()
                    })
                )
                Button(
                    onClick = {
                        if (postAddVM != null) {
                            postAddVM.post.sportHashtag = sportSelectVM.selectedSport.value
                            bottomSheet.subCloseSheet()
                        }
                        if (profileVM != null) {
                            profileVM.newTeamProfile.sport = sportSelectVM.selectedSport.value
                            bottomSheet.subCloseSheet()
                        }
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
                            Button(
                                onClick = {
                                    sportSelectVM.selectSport(sport)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White
                                ),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 0.dp
                                ),
                                shape = RectangleShape,
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .padding(
                                        start = 7.dp,
                                        end = 7.dp,
                                        bottom = 20.dp
                                    )
                                    .height(50.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (sportList[sport] == true) {
                                        Box(
                                            modifier = Modifier
                                                .border(
                                                    BorderStroke(
                                                        1.dp,
                                                        MaterialTheme.colors.primary
                                                    ),
                                                    RoundedCornerShape(15.dp)
                                                )
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = sport, color = MaterialTheme.colors.primary)
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = sport)
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(top = 10.dp)
                                                    .clip(RectangleShape)
                                                    .fillMaxWidth()
                                                    .height(2.dp)
                                                    .background(Color.Gray)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(newSportList.keys.toList()) { sport ->
                            Button(
                                onClick = {
                                    sportSelectVM.newSelectSport(sport)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White
                                ),
                                elevation = ButtonDefaults.elevation(
                                    defaultElevation = 0.dp
                                ),
                                shape = RectangleShape,
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .padding(
                                        start = 7.dp,
                                        end = 7.dp,
                                        bottom = 20.dp
                                    )
                                    .height(50.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (sportList[sport] == true) {
                                        Box(
                                            modifier = Modifier
                                                .border(
                                                    BorderStroke(
                                                        1.dp,
                                                        MaterialTheme.colors.primary
                                                    ),
                                                    RoundedCornerShape(15.dp)
                                                )
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = sport, color = MaterialTheme.colors.primary)
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = sport)
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(top = 10.dp)
                                                    .clip(RectangleShape)
                                                    .fillMaxWidth()
                                                    .height(2.dp)
                                                    .background(Color.Gray)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } // query = ""
                } // LazyVerticalGrid
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
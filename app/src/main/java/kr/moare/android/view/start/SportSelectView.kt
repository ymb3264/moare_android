package kr.moare.android.view.start

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kr.moare.android.components.CircleStartViewButton
import kr.moare.android.components.SearchBar
import kr.moare.android.components.SelectedSportHashtag
import kr.moare.android.utils.SplashNavItem
import kr.moare.android.viewmodel.start.JoinViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun SportSelectView(
    startNavController: NavController,
    splashNavController: NavController,
    joinVM: JoinViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }

    val sportList by joinVM.sportList.collectAsState()
    val newSportList by joinVM.newSportList.collectAsState()
    val selectedSport by joinVM.selectedSport.collectAsState()
    val showAlert by joinVM.showAlert.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    Column(
      modifier = Modifier
          .fillMaxSize()
          .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "스포츠 선택",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(vertical = 12.dp))

        Text(text = "즐기는 스포츠 1개이상을 선택해주세요.",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 12.dp))

        SearchBar(modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
            placeholder = "검색",
            text = query,
            onTextChange = {
                query = it
                joinVM.searchSport(it)
            }
        )

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
                            joinVM.selectSport(sport)
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
                                            BorderStroke(1.dp, MaterialTheme.colors.primary),
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
                            joinVM.newSelectSport(sport)
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
                                            BorderStroke(1.dp, MaterialTheme.colors.primary),
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
            }
        }

//        Button(onClick = {
//                         joinVM.join()
//        },
//            colors = ButtonDefaults.buttonColors(
//                backgroundColor = Color.White
//            ),
//            elevation = ButtonDefaults.elevation(
//                defaultElevation = 0.dp
//            ),
//            border = BorderStroke(1.dp, MaterialTheme.colors.primary),
//            shape = RoundedCornerShape(50),
//            modifier = Modifier
//                .fillMaxWidth()
//                .size(height = 65.dp, width = 0.dp)
//                .padding(
//                    start = 10.dp,
//                    end = 10.dp,
//                    top = 10.dp,
//                    bottom = 10.dp
//                )
//        ) {
//            Text(text = "다음", fontSize = 15.sp, color = Color.Gray)
//        }

//        StartViewButton(
//            modifier = Modifier.padding(vertical = 12.dp),
//            text = "다음",
//            onClick = { joinVM.join() },
//            enabled = false,
//            width = screenWidth
//        )
        CircleStartViewButton(
            modifier = Modifier.padding(vertical = 12.dp),
            enabled = true
        ) {
            joinVM.join(selectedSport.isNotEmpty()) {
                splashNavController.navigate(SplashNavItem.JoinSplash.name)
            }
        }

        if (showAlert) {
            AlertDialog(
                onDismissRequest = { joinVM.showAlert.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        joinVM.join(true) {
                            splashNavController.navigate(SplashNavItem.JoinSplash.name)
                        }
                    }) {
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        joinVM.showAlert.value = false
                    }) {
                        Text(text = "취소")
                    }
                },
                text = {
                    Text(text = "즐기시는 스포츠 추가 없이 회원가입 하시겠습니까?\n" +
                            "(추후에 프로필 편집을 통해 추가가 가능합니다.)")
                }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SportSelectViewPreview() {
//    val joinVM: JoinViewModel = viewModel()
//    MoareTheme {
//        SportSelectView(navController = rememberNavController())
//    }
//}
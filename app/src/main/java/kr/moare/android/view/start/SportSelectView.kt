package kr.moare.android.view.start

import android.annotation.SuppressLint
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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.moare.android.components.SearchBar
import kr.moare.android.components.SelectedSportHashtag
import kr.moare.android.components.SportSelectButton
import kr.moare.android.components.StartViewButton
import kr.moare.android.utils.SplashNavItem
import kr.moare.android.viewmodel.common.SportSelectViewModel
import kr.moare.android.viewmodel.start.JoinViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun SportSelectView(
    startNavController: NavController,
    splashNavController: NavController,
    joinVM: JoinViewModel,
    sportSelectVM: SportSelectViewModel = hiltViewModel()
) {
    val loading by sportSelectVM.loading.collectAsState()
    val sportList by sportSelectVM.sportList.collectAsState()
    val newSportList by sportSelectVM.newSportList.collectAsState()
    val selectedSport by sportSelectVM.selectedSport.collectAsState()
    val showAlert by joinVM.showAlert.collectAsState()

    var query by remember { mutableStateOf("") }

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
            textClear = { query = "" },
            onTextChange = {
                query = it
                sportSelectVM.searchSport(it)
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
//                SelectedSportHashtag(it)
                Text(text = it,
                    modifier = Modifier
                        .padding(start = 10.dp),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption)
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
                            joinVM.account.sportHashtag = selectedSport
                        }
                    }
                } else {
                    items(newSportList.keys.toList()) { sport ->
                        SportSelectButton(selected = sportList[sport], sport = sport) {
                            sportSelectVM.newSelectSport(sport)
                            joinVM.account.sportHashtag = selectedSport
                        }
                    }
                }
            }
        }

        StartViewButton(
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
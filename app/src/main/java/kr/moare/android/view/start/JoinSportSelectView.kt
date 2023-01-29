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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.moare.android.components.SearchBar
import kr.moare.android.components.SelectedSportHashtag
import kr.moare.android.components.SportSelectButton
import kr.moare.android.components.StartViewButton
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.viewmodel.common.SportSelectViewModel
import kr.moare.android.viewmodel.start.JoinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun JoinSportSelectView(
    startNavController: NavController,
    joinVM: JoinViewModel,
    sportSelectVM: SportSelectViewModel = hiltViewModel()
) {
    val loading by sportSelectVM.loading.collectAsState()
    val sportList by sportSelectVM.sportList.collectAsState()
    val newSportList by sportSelectVM.newSportList.collectAsState()
    val selectedSport by sportSelectVM.selectedSport.collectAsState()

    var alert by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
      modifier = Modifier
          .fillMaxSize()
          .background(Color.White)
          .noRippleClickable { keyboardController?.hide() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = StringResources.sportSelectTitle,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(vertical = 12.dp))

        Text(text = StringResources.sportSelectMessage,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 12.dp))

        SearchBar(modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
            placeholder = StringResources.search,
            isfocused = false,
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
            if (selectedSport.isEmpty()) {
                alert = true
            } else {
                startNavController.navigate(StartNavItem.TOS.name)
            }
        }

        if (alert) {
            AlertDialog(
                onDismissRequest = { alert = false },
                confirmButton = {
                    TextButton(onClick = {
                        startNavController.navigate(StartNavItem.TOS.name)
                    }) {
                        Text(text = StringResources.confirm)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        alert = false
                    }) {
                        Text(text = StringResources.cancel)
                    }
                },
                text = {
                    Text(text = StringResources.sportSelectAlertMessage)
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
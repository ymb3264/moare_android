package kr.moare.android.view.post

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.components.ClearButton
import kr.moare.android.components.TextDivideLine
import kr.moare.android.entities.BottomSheet
import kr.moare.android.entities.UserDefaultLocation
import kr.moare.android.utils.MainCurrentBottomSheet
import kr.moare.android.utils.StringResources
import kr.moare.android.viewmodel.common.LocationViewModel
import kr.moare.android.viewmodel.post.PostViewModel

@Composable
fun LocationListView(
    bottomSheet: BottomSheet,
    locationVM: LocationViewModel = hiltViewModel()
) {
    val locationList by locationVM.locationList.collectAsState()
    val currentLocation by locationVM.currentLocationFlow.collectAsState("")
    
    BackHandler() {
        bottomSheet.mainCloseSheet()
    }

    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(locationList) { index, location ->
                Row() {
                    TextButton(
                        onClick = {
                            locationVM.changeCurrentLocation(location)
                            bottomSheet.mainCloseSheet()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = location.address.split(" ")[location.address.split(" ").lastIndex - 1],
                            fontSize = 16.sp
                        )

                        TextDivideLine()

                        Text(
                            text = location.address,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )

                        if (currentLocation.isNotEmpty()) {
                            if (Json.decodeFromString<UserDefaultLocation>(currentLocation) == location) {
                                Box(
                                    Modifier
                                        .padding(start = 12.dp)
                                        .clip(CircleShape)
                                        .size(10.dp)
                                        .background(MaterialTheme.colors.primary)
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        ClearButton(
                            boxModifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                            iconModifier = Modifier.size(10.dp)
                        ) {
                            locationVM.removeLocation(location)
                            bottomSheet.mainCloseSheet()
                        }
                    } // TextButton
                }

                if (index != locationList.size - 1) {
                    Box(
                        Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.LightGray)
                    )
                }
            } // items
        } // LazyColumn

        TextButton(
            onClick = { bottomSheet.modalCloseSheet(MainCurrentBottomSheet.FindLocation) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(StringResources.changeLocation)
        }
    }
}
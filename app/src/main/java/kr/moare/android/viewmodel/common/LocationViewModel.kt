package kr.moare.android.viewmodel.common

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.utils.LocationHelper
import kr.moare.android.entities.AddressItem
import kr.moare.android.entities.Coordinate
import kr.moare.android.network.LocationAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.UserDefaultLocation
import kr.moare.android.utils.LocationDataStore
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.view.post.LocationListView
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @LocationDataStore private val locationDataStore: DataStore<Preferences>,
) : ViewModel() {
    val api = LocationAPI()

    val currentLocationFlow = locationDataStore.data.map {
        it[PreferencesKey.CURRENTLOCATION] ?: ""
    }
    private val locationListFlow = locationDataStore.data.map {
        it[PreferencesKey.LOCATIONLIST] ?: setOf()
    }
    val locationList = MutableStateFlow<MutableList<UserDefaultLocation>>(mutableStateListOf())

    var addressList = MutableStateFlow<MutableList<AddressItem>>(mutableStateListOf())
    var showAlert = MutableStateFlow(false)
    val noResult = MutableStateFlow("")

    var addressItem: AddressItem? = null

    val locationLoading = MutableStateFlow(false)

    val addressListLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            locationListFlow.collect { list ->
                if (list.isNotEmpty()) {
                    locationList.value.clear()
                    for (i in list) {
                        val location = Json.decodeFromString<UserDefaultLocation>(i)
                        locationList.value.add(location)
                    }
                }
            }
        }
    }

    fun searchAddress(query: String) {
        addressListLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                api.searchAddress(query)
            }.onSuccess {
                addressList.value.clear()

                if (it.documents.isEmpty()) {
                    noResult.value = "검색 결과가 없습니다."
                } else {
                    noResult.value = ""
                }

                it.documents.forEach { item ->
                    var addressItem = AddressItem(address = "", x = item.x, y = item.y)

                    item.generalAddress?.let { address ->
                        if (address.address3.isNotEmpty() || address.address3_h.isNotEmpty()) {
                            addressItem.address += address.address1 + " "
                            addressItem.address += address.address2 + " "
                            if (address.address3.isEmpty()) {
                                addressItem.address += address.address3_h + " "
                            } else {
                                addressItem.address += address.address3 + " "
                            }
                        } else {

                        }
                        addressList.value.add(addressItem)
                    }
                }

                addressListLoading.value = false
                Log.d("success", "$it")
            }.onFailure {
                addressListLoading.value = false
                Log.d("fail", "$it")
            }
        }
    }

    fun startLocationUpdates() {
        locationLoading.value = true
        val locationHelper = LocationHelper(context) { x, y -> searchCoordinateAddress(x, y) }
        locationHelper.startLocationUpdates()
    }

    private fun searchCoordinateAddress(x: String, y: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                val coordinate = Coordinate(x, y)
                api.searchCoordinateAddress(coordinate)
            }.onSuccess { response ->
                Log.d("success", "$response")
                // 위치 가져오지 못했을때 indexOutofBounds에러났다
                val address = response.documents[0].generalAddress
                var addressItem =
                    AddressItem(address = "", x = x, y = y)

                address?.let {
                    addressItem.address += it.address1 + " "
                    addressItem.address += it.address2 + " "
                    addressItem.address += it.address3 + " "
                }

                locationLoading.value = false
                showAlert(true, addressItem)
            }.onFailure {
                locationLoading.value = false
                Log.d("fail", "$it")
            }
        }
    }

    fun showAlert(show: Boolean, addressItem: AddressItem?) {
        if (show) {
            showAlert.value = true
            this.addressItem = addressItem
        } else {
            showAlert.value = false
        }
    }

    suspend fun addLocation() {
        // locationList 추가
        val location = UserDefaultLocation(addressItem!!.address, addressItem!!.x, addressItem!!.y)
        val encodedLocation = Json.encodeToString(location)

        viewModelScope.launch {
            locationListFlow.collect { locationList ->
                val newList = if (locationList.isEmpty()) mutableListOf() else locationList.toMutableList()
                newList.add(encodedLocation)
                locationDataStore.edit {
                    it[PreferencesKey.LOCATIONLIST] = newList.toSet()
                }
                locationDataStore.edit {
                    it[PreferencesKey.CURRENTLOCATION] = Json.encodeToString(location)
                }

                coroutineContext.job.cancel()
            }
        }

        showAlert.value = false
    }

    fun changeCurrentLocation(location: UserDefaultLocation) {
        viewModelScope.launch {
            locationDataStore.edit {
                it[PreferencesKey.CURRENTLOCATION] = Json.encodeToString(location)
            }
        }
    }

    fun removeLocation(location: UserDefaultLocation) {
        viewModelScope.launch {
            locationListFlow.collect { locationList ->
                val newList = locationList.toMutableList()

                newList.remove(Json.encodeToString(location))

                locationDataStore.edit {
                    it[PreferencesKey.LOCATIONLIST] = newList.toSet()
                }

                currentLocationFlow.collect { currentLocation ->
                    if (newList.isEmpty()) {
                        locationDataStore.edit {
                            it[PreferencesKey.CURRENTLOCATION] = ""
                        }
                    } else if (Json.decodeFromString<UserDefaultLocation>(currentLocation) == location) {
                        locationDataStore.edit {
                            it[PreferencesKey.CURRENTLOCATION] = newList.first()
                        }
                    }
                }
            }
        }
    }
}

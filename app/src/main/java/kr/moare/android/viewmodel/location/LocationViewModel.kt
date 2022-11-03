package kr.moare.android.viewmodel.location

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.utils.LocationHelper
import kr.moare.android.entities.AddressItem
import kr.moare.android.entities.Coordinate
import kr.moare.android.network.LocationAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val api = LocationAPI()

    var addressList = MutableStateFlow<MutableList<AddressItem>>(mutableStateListOf())
    var showAlert = MutableStateFlow(false)
    val noResult = MutableStateFlow("")

    var addressItem: AddressItem? = null

    val PLACE = stringPreferencesKey("place")
    val X = stringPreferencesKey("x")
    val Y = stringPreferencesKey("y")

    val loading = MutableStateFlow(false)

    fun searchAddress(query: String) {
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

                it.documents.forEach { address ->
                    var addressItem = AddressItem(address = "", roadAddress = "", x = address.x, y = address.y)

                    if (address.generalAddress != null) {
                        addressItem.address += address.generalAddress!!.address1 + " "
                        addressItem.address += address.generalAddress!!.address2 + " "
                        if (address.generalAddress!!.address3 == "") {
                            addressItem.address += address.generalAddress!!.address3_h + " "
                        } else {
                            addressItem.address += address.generalAddress!!.address3 + " "
                        }

                        if (address.roadAddress != null) {
                            addressItem.roadAddress += address.roadAddress!!.address1 + " "
                            addressItem.roadAddress += address.roadAddress!!.address2 + " "
                            addressItem.roadAddress += address.roadAddress!!.roadName + " "
                        }

                        addressList.value.add(addressItem)
                    } else {
                        addressItem.address += address.roadAddress!!.address1 + " "
                        addressItem.address += address.roadAddress!!.address2 + " "
                        addressItem.address += address.roadAddress!!.address3 + " "

                        addressItem.roadAddress += address.roadAddress!!.address1 + " "
                        addressItem.roadAddress += address.roadAddress!!.address2 + " "
                        addressItem.roadAddress += address.roadAddress!!.roadName + " "

                        addressList.value.add(addressItem)
                    }
                }

                Log.d("success", "$it")
            }.onFailure {
                Log.d("fail", "$it")
            }
        }
    }

    fun startLocationUpdates() {
        loading.value = true
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
                val address = response.documents[0].generalAddress
                val roadAddress = response.documents[0].roadAddress
                var addressItem =
                    AddressItem(address = "", roadAddress = "", x = x, y = y)

                address?.let {
                    addressItem.address += it.address1 + " "
                    addressItem.address += it.address2 + " "
                    addressItem.address += it.address3 + " "
                }

                roadAddress?.let {
                    addressItem.roadAddress += it.address1 + " "
                    addressItem.roadAddress += it.address2 + " "
                    addressItem.roadAddress += it.roadName + " "
                }
                loading.value = false
                showAlert(true, addressItem)
            }.onFailure {
                loading.value = false
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
        dataStore.edit {
            it[PLACE] = this.addressItem!!.address
        }
        dataStore.edit {
            it[X] = this.addressItem!!.x
        }
        dataStore.edit {
            it[Y] = this.addressItem!!.y
        }
        showAlert.value = false
    }
}

package kr.moare.android.viewmodel.common

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.network.JoinAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SportSelectViewModel @Inject constructor(

): ViewModel() {
    val api = JoinAPI()

    val loading = MutableStateFlow(true)

    var sportList = MutableStateFlow<MutableMap<String, Boolean>>(mutableStateMapOf())
    var newSportList = MutableStateFlow<MutableMap<String, Boolean>>(mutableStateMapOf())
    var selectedSport = MutableStateFlow<MutableList<String>>(mutableStateListOf())

    init {
        getSportList()
    }

    private fun getSportList() {
        viewModelScope.launch {
            loading.value = true
            kotlin.runCatching {
                api.getSportList()
            }.onSuccess {
                it.sportList.forEach { sport ->
                    sportList.value[sport] = false
                }
                loading.value = false
                Log.d("success", "$it")
            }.onFailure {
                loading.value = false
                Log.d("fail", "$it")
            }
        }
    }

    fun selectSport(sport: String) {
        if (sportList.value[sport] == true) {
            sportList.value[sport] = false
            selectedSport.value.remove(sport)
        } else {
            sportList.value[sport] = true
            selectedSport.value.add(sport)
        }
    }

    fun newSelectSport(sport: String) {
        if (newSportList.value[sport] == true) {
            newSportList.value[sport] = false
            sportList.value[sport] = false
            selectedSport.value.remove(sport)
        } else {
            newSportList.value[sport] = true
            sportList.value[sport] = true
            selectedSport.value.add(sport)
        }
    }

    fun searchSport(query: String) {
        newSportList.value = sportList.value.filter { sport ->
            sport.key.contains(query)
        }.toMutableMap()
    }
}

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

    var sportList = MutableStateFlow<MutableMap<String, Boolean>>(mutableStateMapOf())
    var newSportList = MutableStateFlow<MutableMap<String, Boolean>>(mutableStateMapOf())

    var selectedSport = MutableStateFlow<MutableList<String>>(mutableStateListOf())
    var userHashtag = MutableStateFlow<MutableList<String>>(mutableStateListOf())

    val loading = MutableStateFlow(true)

    init {
        getSportList()
    }

    fun getSportList(sportHashtag: List<String> = listOf()) {
        Log.d("sss","dsfdfsd")
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                api.getSportList()
            }.onSuccess {
                loading.value = false

                it.sportList.forEach { sport ->
                    sportList.value[sport] = false
                }

                if (sportHashtag.isNotEmpty()) {
                    sportHashtag.forEach { sport ->
                        selectSport(sport)
                    }
                }
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
            if (sportList.value.keys.contains(sport)) {
                sportList.value[sport] = true
            }
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

    fun deleteSelectedSport() {

    }
}

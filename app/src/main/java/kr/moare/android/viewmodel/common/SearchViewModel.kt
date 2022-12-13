package kr.moare.android.viewmodel.common

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.network.SearchAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kr.moare.android.network.JoinAPI
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(

) : ViewModel() {
    private val searchApi = SearchAPI()
    private val joinApi = JoinAPI()

    var searchList = MutableStateFlow<List<String>>(mutableStateListOf())
    var loading = MutableStateFlow(false)

    var sportList = listOf<String>()

    init {
        getSportList()
    }

    fun search(query: String) {
        searchList.value = listOf()
        if (query.isEmpty()) {
            return
        }

        loading.value = true
        if (query.startsWith("#")) {
            loading.value = false
            searchList.value = sportList.filter {
                it.contains(query)
            }
        } else {
            viewModelScope.launch {
                kotlin.runCatching {
                    searchApi.searchUsername(query)
                }.onSuccess {
                    loading.value = false
                    searchList.value = it
                    Log.d("success", "$it")
                }.onFailure {
                    loading.value = false
                    Log.d("fail", "$it")
                }
            }
        }
    }

    private fun getSportList() {
        viewModelScope.launch {
            kotlin.runCatching {
                joinApi.getSportList()
            }.onSuccess {
                sportList = it.sportList
                Log.d("success", "$it")
            }.onFailure {
                Log.d("fail", "$it")
            }
        }
    }
}
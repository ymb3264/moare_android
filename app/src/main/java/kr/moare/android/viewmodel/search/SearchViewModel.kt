package kr.moare.android.viewmodel.search

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.network.SearchAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(

) : ViewModel() {
    val api = SearchAPI()

    var searchList = MutableStateFlow<List<String>>(mutableStateListOf())

    fun search(query: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                if (query.startsWith("#")) {
                    api.searchHashtag(query)
                } else {
                    api.searchUsername(query)
                }
            }.onSuccess {
                searchList.value = it
                Log.d("success", "$it")
            }.onFailure {
                Log.d("fail", "$it")
            }
        }
    }
}
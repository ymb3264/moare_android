package kr.moare.android.viewmodel.common

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.network.SearchAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.Profile
import kr.moare.android.network.JoinAPI
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UserInfoDataStore
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>
) : ViewModel() {
    private val searchApi = SearchAPI()
    private val joinApi = JoinAPI()

    var searchList = MutableStateFlow<List<String>>(mutableStateListOf())
    var loading = MutableStateFlow(false)

    var sportList = listOf<String>()

    private val profileFlow = userInfoDataStore.data.map {
        it[PreferencesKey.PROFILE] ?: ""
    }

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
                profileFlow.collect { encodedProfile ->
                    kotlin.runCatching {
                        searchApi.searchUsername(query)
                    }.onSuccess { response ->
                        var mutableList = response.toMutableList()
                        var newList = response.toMutableList()
                        val profile = Json.decodeFromString<Profile>(encodedProfile)
                        profile.blockedBy?.let { blockedBy ->
                            // ConcurrentModificationException에러 때문에 newList에서 obj remove
                            for (obj in mutableList) {
                                val blockedUser = obj.userID + "+" + obj.createdAt
                                if (blockedBy.contains(blockedUser)) {
                                    newList.remove(obj)
                                }
                            }
                        }

                        searchList.value = newList.map { obj ->
                            obj.username
                        }

                        loading.value = false
                        Log.d("success", "$response")
                    }.onFailure {
                        loading.value = false
                        Log.d("fail", "$it")
                    }
                    coroutineContext.job.cancel()
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
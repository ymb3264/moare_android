package kr.moare.android.viewmodel.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.network.FollowAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    private val api = FollowAPI()

    val host = encryptedSharedPreferences.getString("username", " ") ?: ""
//    private val profile: UserProfile? = savedStateHandle["profile"]
//
//    var profileState = MutableStateFlow<UserProfile>(UserProfile("", "", "", "", "",
//        listOf(), listOf(), listOf(), listOf()))

    init {
//        profileState.value = profile!!
    }

//    fun follow(username: String) {
//        viewModelScope.launch {
//            kotlin.runCatching {
//                api.follow(host, username)
//            }.onSuccess {
//                Log.d("success", "$it")
//            }.onFailure {
//                Log.d("FAIL", "message: $it")
//            }
//        }
//    }
}
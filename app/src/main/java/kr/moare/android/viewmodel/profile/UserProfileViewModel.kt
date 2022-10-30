package kr.moare.android.viewmodel.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.entities.TeamProfile
import kr.moare.android.entities.UserProfile
import kr.moare.android.network.ProfileAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = ProfileAPI()

    val host = encryptedSharedPreferences.getString("username", "") ?: ""

    private val username: String = savedStateHandle["username"] ?: "moare1"

    var profile = MutableStateFlow<UserProfile>(
        UserProfile("", listOf(), "", "", "",
            "", false, listOf(), listOf(), listOf())
    )

    var teamProfile = MutableStateFlow<TeamProfile>(
        TeamProfile("", "", "", "", "",
            "", listOf(), listOf(), listOf(), listOf())
    )

    init {
        if (username.endsWith("-t")) {
            getTeamProfile()
        } else {
            getUserProfile()
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getUserProfile(username)
            }.onSuccess {
                profile.value = it
                Log.d("success", "$it")
            }.onFailure {
                Log.d("FAIL", "message: $it")
            }
        }
    }

    fun getTeamProfile() {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getTeamProfile(username)
            }.onSuccess {
                teamProfile.value = it
                Log.d("success", "$it")
            }.onFailure {
                Log.d("FAIL", "message: $it")
            }
        }
    }

}
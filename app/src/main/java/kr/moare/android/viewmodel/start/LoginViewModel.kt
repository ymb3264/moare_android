package kr.moare.android.viewmodel.start

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.entities.LoginAccount
import kr.moare.android.network.LoginAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UserInfoDataStore
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedSharedPreferences: SharedPreferences,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
) : ViewModel() {
    private val api: LoginAPI = LoginAPI()

    val login = MutableStateFlow(false)
    val showErrorText = MutableStateFlow(false)

    val loginLoading = MutableStateFlow(false)
    val meLoading = MutableStateFlow(false)

    init {
        val token = encryptedSharedPreferences.getString("token", "") ?: ""
        me(token)
    }

    fun checkEmail() {

    }

    fun login(email: String, pwd: String) {
        loginLoading.value = true
        showErrorText.value = false
        viewModelScope.launch {
            kotlin.runCatching {
                val account = LoginAccount(email, pwd)
                api.login(account)
            }.onSuccess { response ->
                encryptedSharedPreferences.edit().putString("token", response.token).apply()
                userInfoDataStore.edit {
                    it[PreferencesKey.USERNAME] = response.username
                }

                login.value = true
                loginLoading.value = false
                Log.d("success", "$response")
            }.onFailure {
                loginLoading.value = false
                showErrorText.value = true
                Log.d("fail", "$it")
            }
        }
    }

    private fun me(token: String) {
        meLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                api.me(token)
            }.onSuccess { response ->
                meLoading.value = false

                encryptedSharedPreferences.edit().putString("token", response.token).apply()
                userInfoDataStore.edit {
                    it[PreferencesKey.USERNAME] = response.username
                }

                login.value = true
                Log.d("success", "$response")
            }.onFailure {
                meLoading.value = false
                Log.d("fail", "$it")
            }
        }
    }
}
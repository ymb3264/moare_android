package kr.moare.android.viewmodel.start

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.entities.LoginAccount
import kr.moare.android.network.LoginAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    private val api: LoginAPI = LoginAPI()
    val login = MutableStateFlow(false)
    val loading = MutableStateFlow(true)
    val showErrorText = MutableStateFlow(false)

    init {
        val token = encryptedSharedPreferences.getString("token", "") ?: ""
        me(token)
    }

    fun login(email: String, pwd: String) {
        val account = LoginAccount(email, pwd)

        viewModelScope.launch {
            kotlin.runCatching {
                api.login(account)
            }.onSuccess {
                encryptedSharedPreferences.edit().putString("token", it.token).apply()
                encryptedSharedPreferences.edit().putString("username",it.username).apply()
                login.value = true
                Log.d("success", "$it")
            }.onFailure {
                showErrorText.value = true
                Log.d("fail", "$it")
            }
        }
    }

    private fun me(token: String) {
        viewModelScope.launch {
//            loading.value = true
            kotlin.runCatching {
                api.me(token)
            }.onSuccess {
                encryptedSharedPreferences.edit().putString("token", it.token).apply()
                encryptedSharedPreferences.edit().putString("username",it.username).apply()
                login.emit(true)
                loading.value = false
                Log.d("success", "$it")
            }.onFailure {
                loading.value = false
                Log.d("fail", "$it")
            }
        }
    }
}
package kr.moare.android.viewmodel.start

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kr.moare.android.entities.NewPwdObj
import kr.moare.android.entities.ResponseForNewPwd
import kr.moare.android.network.JoinAPI
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UserIdUsernameDataStore
import kr.moare.android.utils.UserInfoDataStore
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedSharedPreferences: SharedPreferences,
    @UserIdUsernameDataStore private val userIdUsernameDataStore: DataStore<Preferences>
) : ViewModel() {
    private val loginApi = LoginAPI()
    private val joinApi = JoinAPI()

    var response = ResponseForNewPwd("", 0)

    val pwd = MutableStateFlow("")
    val email = MutableStateFlow("")

    val emailBtn = MutableStateFlow(false)
    val pwdBtn = MutableStateFlow(false)

    val loading = MutableStateFlow(false)
    val loginLoading = MutableStateFlow(false)
    val meLoading = MutableStateFlow(false)

    val showErrorText = MutableStateFlow(false)
    val showErrorText2 = MutableStateFlow(false)
    val networkError = MutableStateFlow(false)
    var networkErrorText = ""

    val login = MutableStateFlow(false)

    private val pattern = Patterns.EMAIL_ADDRESS

    init {
        val accessToken = encryptedSharedPreferences.getString("AccessToken", "") ?: ""
        val refreshToken = encryptedSharedPreferences.getString("RefreshToken", "") ?: ""
        me(accessToken, refreshToken)
    }

    fun login(email: String, pwd: String, cb: () -> Unit) {
        loginLoading.value = true
        showErrorText.value = false
        viewModelScope.launch {
            kotlin.runCatching {
                val account = LoginAccount(email, pwd)
                loginApi.login(account)
            }.onSuccess { response ->
                encryptedSharedPreferences.edit().putString("AccessToken", response.accessToken).apply()
                encryptedSharedPreferences.edit().putString("RefreshToken", response.refreshToken).apply()
                userIdUsernameDataStore.edit {
                    it[PreferencesKey.USERID] = response.userID
                    it[PreferencesKey.USERNAME] = response.username
                }

                login.value = true
                loginLoading.value = false
                cb()
                Log.d("success", "$response")
            }.onFailure {
                loginLoading.value = false
                showErrorText.value = true
                Log.d("fail", "$it")
            }
        }
    }

    private fun me(accessToken: String, refreshToken: String) {
        meLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                loginApi.me(accessToken, refreshToken)
            }.onSuccess { response ->
                encryptedSharedPreferences.edit().putString("AccessToken", response.accessToken).apply()
                encryptedSharedPreferences.edit().putString("RefreshToken", response.refreshToken).apply()
                userIdUsernameDataStore.edit {
                    it[PreferencesKey.USERID] = response.userID
                    it[PreferencesKey.USERNAME] = response.username
                }

                login.value = true
                meLoading.value = false
                Log.d("success", "$response")
            }.onFailure {
                meLoading.value = false
                Log.d("fail", "$it")
            }
        }
    }

    fun getEmailCode(goNext: () -> Unit = {}) {
        loading.value = true
        resetError()
        viewModelScope.launch {
            kotlin.runCatching {
                loginApi.getEmailCode(email.value)
            }.onSuccess {
                loading.value = false
                response.createdAt = it.createdAt
                response.serverCode = it.serverCode
                goNext()
                Log.d("success", "$it")
            }.onFailure {
                loading.value = false

                it.message?.let { message ->
                    if (message == "User not found") {
                        networkError.value = true
                        networkErrorText = "해당 이메일로 가입된 계정이 존재하지 않습니다"
                    }
                }
            }
        }
    }

    fun setNewPwd(completion: () -> Unit) {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val obj = NewPwdObj(email.value, response.createdAt, pwd.value)
                loginApi.setNewPwd(obj)
            }.onSuccess {
                loading.value = false
                completion()
                Log.d("setPwd", "$it")
            }.onFailure {
                loading.value = false
                Log.d("setPwdFail", "$it")
            }
        }
    }

    fun checkEmail(email: String) {
        this.email.value = email
        emailBtn.value = pattern.matcher(email).matches()
        if (emailBtn.value) showErrorText.value = false
    }

    fun checkCode(clientCode: String, goNext: () -> Unit) {
        showErrorText.value = false
        if (clientCode == response.serverCode.toString()) {
            goNext()
        } else {
            resetError()
            showErrorText.value = true
        }
    }

    fun checkPwd(pwd: String) {
        this.pwd.value = pwd

        val pwdRegex1 =
            "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,20}$" // 영문, 숫자
        val pwdRegex2 =
            "^(?=.*[0-9])(?=.*[$@$!%*#?&.])[[0-9]$@$!%*#?&.]{8,20}$" // 숫자, 특수문자
        val pwdRegex3 =
            "^(?=.*[A-Za-z])(?=.*[$@$!%*#?&.])[A-Za-z$@$!%*#?&.]{8,20}$" // 영문, 특수문자
        val pwdRegex4 =
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,20}$" // 영문, 숫자, 특수문자

        val isValid = (Pattern.matches(pwdRegex1, pwd) ||
                Pattern.matches(pwdRegex2, pwd) ||
                Pattern.matches(pwdRegex3, pwd) ||
                Pattern.matches(pwdRegex4, pwd))

        pwdBtn.value = isValid
        showErrorText.value = !isValid
    }

    fun checkSecondPwd(pwdForCheck: String) {
        showErrorText2.value = pwd.value != pwdForCheck
        pwdBtn.value = !showErrorText2.value
    }

    private fun resetError() {
        showErrorText.value = false
        showErrorText2.value = false
        networkError.value = false
    }
}
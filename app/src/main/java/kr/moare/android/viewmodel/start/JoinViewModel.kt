package kr.moare.android.viewmodel.start

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.entities.JoinAccount
import kr.moare.android.network.JoinAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UserIdUsernameDataStore
import kr.moare.android.utils.UserInfoDataStore
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedSharedPreferences: SharedPreferences,
    @UserIdUsernameDataStore private val userIdUsernameDataStore: DataStore<Preferences>
) : ViewModel() {
    private val api: JoinAPI = JoinAPI()

    var account = JoinAccount(userID = "", createdAt = "", password = "", username = "", allTermsAgreed = false)
    var servercode = ""

    val email = MutableStateFlow("")
    val pwd = MutableStateFlow("")
    val username = MutableStateFlow("")

    val emailBtn = MutableStateFlow(false)
    val pwdBtn = MutableStateFlow(false)
    val usernameBtn = MutableStateFlow(false)

    val loading = MutableStateFlow(false)
    val usernameLoading = MutableStateFlow(false)

    val showErrorText = MutableStateFlow(false)
    val showErrorText2 = MutableStateFlow(false)
    val networkError = MutableStateFlow(false)

    val joinSuccess = MutableStateFlow(false)

    private val pattern = Patterns.EMAIL_ADDRESS

    // api
    fun getEmailCode(goNext: () -> Unit = {}) {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                api.getEmailCode(email.value)
            }.onSuccess {
                resetError()
                loading.value = false
                account.userID = email.value
                servercode = it.serverCode.toString()
                goNext()
                Log.d("success", "$it")
            }.onFailure {
                loading.value = false
                Log.d("fail", "$it")
            }
        }
    }

    fun checkUsername2(username: String) {
        usernameLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                api.checkUsername(username)
            }.onSuccess {
                usernameLoading.value = false

                if (it.message == "available") {
                    usernameBtn.value = true
                    account.username = username
                } else {
                    showErrorText2.value = true
                    usernameBtn.value = false
                }
                Log.d("success", "$it")
            }.onFailure {
                usernameLoading.value = false
                showErrorText2.value = true
                usernameBtn.value = false
                Log.d("fail", "$it")
            }
        }
    }

    fun join(saveLoginInfo: Boolean, showSplash: () -> Unit) {
        showSplash()

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
        account.createdAt = formatter.format(Date())

        viewModelScope.launch {
            kotlin.runCatching {
                api.join(account)
            }.onSuccess { response ->
                joinSuccess.value = true

                if (saveLoginInfo) {
                    encryptedSharedPreferences.edit().putString("AccessToken", response.accessToken).apply()
                    encryptedSharedPreferences.edit().putString("RefreshToken", response.refreshToken).apply()
                }
                userIdUsernameDataStore.edit {
                    it[PreferencesKey.USERID] = response.userID
                    it[PreferencesKey.USERNAME] = response.username
                }

                Log.d("success", "$response")
            }.onFailure {
                Log.d("fail", "$it")
            }
        }
    }

    // internal
    fun checkEmail(email: String) {
        this.email.value = email
        emailBtn.value = pattern.matcher(email).matches()
        if (emailBtn.value) showErrorText.value = false
    }

    fun checkCode(clientCode: String, goNext: () -> Unit) {
        showErrorText.value = false
        if (clientCode == servercode) {
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

    fun addPwd(goNext: () -> Unit) {
        if (!showErrorText.value) {
            account.password = pwd.value
            goNext()
        }
    }

    fun checkUsername(username: String) {
        this.username.value = username
        usernameBtn.value = false
        showErrorText2.value = false

        val usernameRegex =
            "^[A-Za-z_.[0-9]]{1,30}$"
        showErrorText.value = !Pattern.matches(usernameRegex, username)

        if (!showErrorText.value) {
            checkUsername2(username)
        }
    }

    fun showNetworkError() {
        showErrorText.value = false
        showErrorText2.value = false
        networkError.value = true
    }

    private fun resetError() {
        showErrorText.value = false
        showErrorText2.value = false
        networkError.value = false
    }
}
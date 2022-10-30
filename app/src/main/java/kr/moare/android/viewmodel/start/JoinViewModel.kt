package kr.moare.android.viewmodel.start

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.entities.JoinAccount
import kr.moare.android.network.JoinAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
//    var serverCode = MutableStateFlow<EmailCode>(EmailCode(""))
    val api: JoinAPI = JoinAPI()
    var servercode = ""
    var account = JoinAccount(email = "", password = "", username = "", sportHashtag = mutableListOf<String>())

    var showErrorText = MutableStateFlow(false)
    val joinSuccess = MutableStateFlow(false)
    var sportList = MutableStateFlow<MutableMap<String, Boolean>>(mutableStateMapOf("축구" to false))
    var newSportList = MutableStateFlow<MutableMap<String, Boolean>>(mutableStateMapOf())
    var selectedSport = MutableStateFlow<MutableList<String>>(mutableStateListOf())

    var email = MutableStateFlow("")
    var emailBtn = MutableStateFlow(false)
    var pwd = MutableStateFlow("")
    var pwdBtn = MutableStateFlow(false)
    var username = MutableStateFlow("")
    var usernameBtn = MutableStateFlow(false)
    var showErrorText2 = MutableStateFlow(false)

    var showAlert = MutableStateFlow(false)

    private val pattern = Patterns.EMAIL_ADDRESS

    init {
        getSportList()
    }

    fun getEmailCode() {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getEmailCode(account)
            }.onSuccess {
                Log.d("success", "$it")
                servercode = it.serverCode.toString()
            }.onFailure {
                Log.d("fail", "$it")
            }
        }
    }

    fun checkUsername(username: String) {
        this.username.value = username
        showErrorText2.value = false

        val pwdRegex1 =
            "^[A-Za-z_.[0-9]]{1,30}$"
        val isValid = Pattern.matches(pwdRegex1, username)

        usernameBtn.value = isValid
        showErrorText.value = !isValid
    }

    fun checkUsername2(username: String, goNext: (Boolean) -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                api.checkUsername(username)
            }.onSuccess {
                if (it.message == "available") {
                    showErrorText2.value = false
                    account.username = username
                    goNext(true)
                } else {
                    showErrorText2.value = true
                }
                Log.d("success", "$it")
            }.onFailure {
                Log.d("fail", "$it")
            }
        }
    }

    fun getSportList() {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getSportList()
            }.onSuccess {
                it.sportList.forEach { sport ->
                    sportList.value.put(sport, false)
                }
                Log.d("success", "$it")
            }.onFailure {
                Log.d("fail", "$it")
            }
        }
    }

    fun join(sportSelected: Boolean, showSplash: () -> Unit) {
        if (account.sportHashtag.isEmpty() && !sportSelected) {
            showAlert.value = true
        } else {
            viewModelScope.launch {
                kotlin.runCatching {
                    showSplash()
                    api.join(account)
                }.onSuccess {
                    encryptedSharedPreferences.edit().putString("token", it.token).apply()
                    encryptedSharedPreferences.edit().putString("username", it.username).apply()
                    joinSuccess.emit(true)
                    Log.d("success", "$it")
                }.onFailure {
                    Log.d("fail", "$it")
                }
            }
        }
    }

    fun checkCode(clientCode: String, goNext: (Boolean) -> Unit) {
        if (clientCode == servercode) {
            showErrorText.value = false
            goNext(true)
            Log.d("checkcode", "true")
        } else {
            showErrorText.value = true
            Log.d("checkcode", "false")
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

    fun addPwd(goNext: (Boolean) -> Unit) {
        if (!showErrorText.value) {
            account.password = pwd.value
            goNext(true)
        }
    }

    fun selectSport(sport: String) {
        if (sportList.value[sport] == true) {
            sportList.value[sport] = false
            account.sportHashtag = account.sportHashtag.filter {
                it != sport
            }.toMutableList()
            selectedSport.value.remove(sport)
        } else {
            sportList.value[sport] = true
            account.sportHashtag.add(sport)
            selectedSport.value.add(sport)
        }
    }

    fun newSelectSport(sport: String) {
        if (newSportList.value[sport] == true) {
            newSportList.value[sport] = false
            sportList.value[sport] = false
            account.sportHashtag = account.sportHashtag.filter {
                it != sport
            }.toMutableList()
            selectedSport.value.remove(sport)
        } else {
            newSportList.value[sport] = true
            sportList.value[sport] = true
            account.sportHashtag.add(sport)
            selectedSport.value.add(sport)
        }
    }

    fun searchSport(query: String) {
        newSportList.value = sportList.value.filter { sport ->
            sport.key.contains(query)
        }.toMutableMap()
    }

    fun checkEmail(email: String) {
        this.email.value = email
        emailBtn.value = pattern.matcher(email).matches()
        if (emailBtn.value) showErrorText.value = false
    }
}
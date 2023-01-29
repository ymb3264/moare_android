package kr.moare.android.network

import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kr.moare.android.entities.*

class LoginAPI {
    suspend fun login(account: LoginAccount): TokenResponse {
        return KtorClient.httpClient.post(APIRoutes.login) {
            setBody(account)
        }.body()
    }

    suspend fun me(accessToken: String, refreshToken: String): TokenResponse {
        val response = KtorClient.httpClient.get(APIRoutes.login) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }

        if (response.status.value in 400..499) {
            val message = response.body<MessageResponse>()

            if (message.message == "Accesstoken Expired") {
                    val refreshResponse = KtorClient.httpClient.get(APIRoutes.refresh) {
                        headers {
                            append(HttpHeaders.Authorization, "Bearer $refreshToken")
                        }
                    }

                    if (refreshResponse.status.value in 400..499) {
                        val refreshMessage = refreshResponse.body<MessageResponse>()
                        throw Throwable(refreshMessage.message)
                    }
                return refreshResponse.body()
            }
            throw Throwable(message.message)
        }

        return response.body()
    }

    suspend fun getEmailCode(email: String): ResponseForNewPwd {
        val response = KtorClient.httpClient.get(APIRoutes.loginEmailCode) {
            url {
                parameters.append("email", email)
            }
        }

        if (response.status.value in 400..499) {
            val message = response.body<MessageResponse>()
            throw Throwable(message.message)
        }

        return response.body()
    }

    suspend fun setNewPwd(obj: NewPwdObj): MessageResponse {
        return KtorClient.httpClient.post(APIRoutes.newPwd) {
            setBody(obj)
        }.body()
    }
}
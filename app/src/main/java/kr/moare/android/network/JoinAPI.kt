package kr.moare.android.network

import kr.moare.android.entities.*
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class JoinAPI {
    suspend fun getEmailCode(email: String): EmailCode {
        return KtorClient.httpClient.get(APIRoutes.emailcode) {
            url {
                parameters.append("email", email)
            }
        }.body()
    }

    suspend fun checkUsername(username: String): MessageResponse {
        val response =  KtorClient.httpClient.get(APIRoutes.username) {
            url {
                parameters.append("username", username)
            }
        }

        if (response.status.value in 400..499) {
            val message = response.body<MessageResponse>()
            throw Throwable(message.message)
        }

        return response.body()
    }

    suspend fun  getSportList(): SportHashtagList {
        return KtorClient.httpClient.get(APIRoutes.sport).body()
    }

    suspend fun join(account: JoinAccount): TokenResponse {
        return KtorClient.httpClient.post(APIRoutes.join) {
            setBody(account)
        }.body()
    }
}
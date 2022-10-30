package kr.moare.android.network

import kr.moare.android.entities.*
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class JoinAPI {
    suspend fun getEmailCode(account: JoinAccount): EmailCode {
        return KtorClient.httpClient.post(APIRoutes.emailcode) {
            setBody(account)
        }.body()
    }

    suspend fun checkUsername(username: String): MessageResponse {
        return KtorClient.httpClient.get(APIRoutes.username) {
            url {
                parameters.append("username", username)
            }
        }.body()
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
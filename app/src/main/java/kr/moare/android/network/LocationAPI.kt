package kr.moare.android.network

import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import kr.moare.android.entities.AddressResponse
import kr.moare.android.entities.Coordinate
import kr.moare.android.entities.CoordinateResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class LocationAPI {
    suspend fun searchAddress(query: String): AddressResponse {
        return KtorClient.httpClient.get(APIRoutes.location) {
            url {
                parameters.append("query", query)
            }
            headers {
                append(HttpHeaders.Authorization, "KakaoAK 964b84968914e77804587272bb857d25")
            }
        }.body()
    }

    suspend fun searchCoordinateAddress(coordinate: Coordinate): CoordinateResponse {
        return KtorClient.httpClient.get(APIRoutes.coordinate) {
            url {
                parameters.append("x", coordinate.x)
                parameters.append("y", coordinate.y)
                parameters.append("input_coord", "WGS84")
            }
            headers {
                append(HttpHeaders.Authorization, "KakaoAK 964b84968914e77804587272bb857d25")
            }
        }.body()
    }
}
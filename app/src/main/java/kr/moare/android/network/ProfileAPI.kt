package kr.moare.android.network

import kr.moare.android.entities.Post
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import kr.moare.android.entities.UserProfile
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.UpdateProfile
import java.io.File

class ProfileAPI {
    suspend fun getMyProfile(token: String, username: String): UserProfile {
        return KtorClient.httpClient.get(APIRoutes.profile) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun getUserProfile(username: String): UserProfile {
        return KtorClient.httpClient.get(APIRoutes.userProfile) {
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun getUserPosts(username: String): List<Post> {
        return KtorClient.httpClient.get(APIRoutes.post) {
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun createTeamProfile(token: String, teamProfile: UserProfile, profileImage: File?): UserProfile {
        val jsonTeamProfile = Json.encodeToString(teamProfile)

        return KtorClient.httpClient.submitFormWithBinaryData(
            url = APIRoutes.teamProfile,
            formData = formData {
                append("profile", jsonTeamProfile)

                if (profileImage != null) {
                    append("profileImage", profileImage.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(HttpHeaders.ContentDisposition, "filename=\"${profileImage.name}\"")
                    })
                }
            }
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.body()
    }

    suspend fun updateProfile(token: String, profile: UpdateProfile, profileImage: File?): UserProfile {
        val jsonProfile = Json.encodeToString(profile)

        return KtorClient.httpClient.submitFormWithBinaryData(
           url = APIRoutes.profile,
            formData = formData {
                append("profile", jsonProfile)

                if (profileImage != null) {
                    append("profileImage", profileImage.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(HttpHeaders.ContentDisposition, "filename=\"${profileImage.name}\"")
                    })
                }
            }
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.body()
    }

    suspend fun getMyAccounts(token: String): List<UserProfile> {
        return KtorClient.httpClient.get(APIRoutes.myAccounts) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.body()
    }
}
package kr.moare.android.network

import android.util.Log
import kr.moare.android.entities.Post
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.CreateTeamProfile
import kr.moare.android.entities.Profile
import kr.moare.android.entities.UpdateProfile
import java.io.File

class ProfileAPI {
    suspend fun getMyProfile(token: String, username: String): Profile {
        return KtorClient.httpClient.get(APIRoutes.profile) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun getUserProfile(username: String): Profile {
        return KtorClient.httpClient.get(APIRoutes.userProfile) {
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun getUserPosts(username: String): List<Post> {
        return KtorClient.httpClient.get(APIRoutes.userPost) {
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun createTeamProfile(token: String, profile: CreateTeamProfile, profileImage: File?): Profile {
        val json = Json { encodeDefaults = true }
        val jsonTeamProfile = json.encodeToString(profile)
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

    suspend fun updateProfile(token: String, profile: UpdateProfile, profileImage: File?): Profile {
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

    suspend fun getMyAccounts(token: String): List<Profile> {
        return KtorClient.httpClient.get(APIRoutes.myAccounts) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.body()
    }
}
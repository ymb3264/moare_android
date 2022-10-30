package kr.moare.android.network

import kr.moare.android.entities.MessageResponse
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.entities.UserDefaultPlace
import kr.moare.android.entities.Post
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File

class PostAPI {

    suspend fun createPost(content: String, files: List<File>): MessageResponse {
        return KtorClient.httpClient.submitFormWithBinaryData(
            url = APIRoutes.post,
            formData = formData {
                append("content", content)
                files.forEach {
                    append("image", it.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(HttpHeaders.ContentDisposition, "filename=\"${it.name}\"")
                    })
                }
            }
        ).body()
    }

    suspend fun getPosts(yearAndMonth: String, place: UserDefaultPlace, username: String, date: String): List<Post> {
        return KtorClient.httpClient.get(APIRoutes.post) {
            url {
                parameters.append("yearAndMonth", yearAndMonth)
                parameters.append("x", place.x)
                parameters.append("y", place.y)
                parameters.append("username", username)
                parameters.append("date", date)
            }
        }.body()
    }

    // ---------
    suspend fun getPost(number: Int): Post {
        return KtorClient.httpClient.get(APIRoutes.onePost) {
            url {
                parameters.append("number", number.toString())
            }
        }.body()
    }

    suspend fun updatePost(post: Post): String {
        return KtorClient.httpClient.put(APIRoutes.post) {
            url {
//                parameters.append("id", post.postNum.toString())
            }
            setBody(post)
        }.body()
    }

    suspend fun deletePost(id: String): String {
        return KtorClient.httpClient.delete(APIRoutes.post) {
            url {
                parameters.append("id", id)
            }
        }.body()
    }
}
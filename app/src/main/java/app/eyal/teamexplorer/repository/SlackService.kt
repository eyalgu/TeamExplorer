package app.eyal.teamexplorer.repository

import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserList(
    val ok: Boolean,
    val members: List<User>?,
    val error: String?
)

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val deleted: Boolean = false,
    val profile: Profile
)

@JsonClass(generateAdapter = true)
data class Profile(
    val image_24: String,
    val image_32: String,
    val image_48: String,
    val image_72: String,
    val image_192: String,
    val image_512: String,
    val image_original: String?,
    val title: String,
    val team: String,
    val status_text: String,
    val real_name: String,
    val display_name: String,
    val real_name_normalized: String,
    val display_name_normalized: String
)

interface SlackService {
    @GET("users.list")
    suspend fun userList(@Query("token") token: String): UserList

    companion object {
        const val TOKEN = "xoxp-5048173296-5048487710-19045732087-b5427e3b46"
    }
}
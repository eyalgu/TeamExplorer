package app.eyal.teamexplorer.repository

import retrofit2.http.GET
import retrofit2.http.Query


interface SlackService {
    @GET("users.list")
    suspend fun userList(@Query("token") token: String): UserList

    companion object {
        const val TOKEN = "xoxp-5048173296-5048487710-19045732087-b5427e3b46"
    }
}
package app.eyal.teamexplorer.wiring

import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context
import androidx.room.Room
import app.eyal.teamexplorer.repository.SlackService
import app.eyal.teamexplorer.repository.RealSlackRepository
import app.eyal.teamexplorer.repository.SlackDatabase
import app.eyal.teamexplorer.repository.SlackRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@ExperimentalCoroutinesApi
@FlowPreview
interface UserComponent {
    val slackRepository: SlackRepository
}

@ExperimentalCoroutinesApi
@FlowPreview
internal class RealUserComponent(applicationContext: Context, token: String) : UserComponent {
    override val slackRepository: SlackRepository = provideSlackRepository(applicationContext, token)
}

private fun provideSlackService(token: String): SlackService {
    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(TokenInterceptor(token))
        .build()

    val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https:/slack.com/api/")
        .client(client)
        .build()
    return retrofit.create(SlackService::class.java)
}

private fun provideSlackDatabase(context: Context): SlackDatabase {
    return Room.inMemoryDatabaseBuilder(context, SlackDatabase::class.java)
        .build()
}

@ExperimentalCoroutinesApi
@FlowPreview
private fun provideSlackRepository(context: Context, token: String) = RealSlackRepository(
    service = provideSlackService(token),
    dao = provideSlackDatabase(context).slackDao()
)

class TokenInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.newBuilder()
            .addQueryParameter("token", token)
            .build()
        val request = chain.request().newBuilder()
            .url(url)
            .build()
        return chain.proceed(request)
    }
}
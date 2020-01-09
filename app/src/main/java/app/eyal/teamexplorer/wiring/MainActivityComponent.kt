package app.eyal.teamexplorer.wiring

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.room.Room
import app.eyal.teamexplorer.MainActivity
import app.eyal.teamexplorer.R
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
interface MainActivityComponent {
    val slackRepository: SlackRepository
}

@ExperimentalCoroutinesApi
@FlowPreview
internal class RealMainActivityComponent(mainActivity: MainActivity) : MainActivityComponent {
    override val slackRepository: SlackRepository = provideSlackRepository(mainActivity.applicationContext)
}

private fun provideSlackService(): SlackService {
    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

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
private fun provideSlackRepository(context: Context) = RealSlackRepository(
    service = provideSlackService(),
    dao = provideSlackDatabase(context).slackDao()
)

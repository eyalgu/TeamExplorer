package app.eyal.teamexplorer.wiring

import android.app.Application
import android.content.Context
import androidx.navigation.findNavController
import androidx.room.Room
import app.eyal.teamexplorer.MainActivity
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.TeamExplorerApplication
import app.eyal.teamexplorer.repository.SlackService
import app.eyal.teamexplorer.presenter.TeamListPresenter
import app.eyal.teamexplorer.repository.RealSlackRepository
import app.eyal.teamexplorer.repository.SlackDatabase
import app.eyal.teamexplorer.repository.SlackRepository
import com.airbnb.mvrx.ViewModelContext
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@ExperimentalCoroutinesApi
@FlowPreview
interface Component {
    val teamListPresenterFactory: TeamListPresenter.Factory
    val slackRepository: SlackRepository
}

@ExperimentalCoroutinesApi
@FlowPreview
internal class RealComponent(private val mainActivity: MainActivity) : Component {
    override val teamListPresenterFactory: TeamListPresenter.Factory
        get() = TeamListPresenter.Factory(
            slackRepository,
            mainActivity.findNavController(R.id.nav_host_fragment)
        )

    override val slackRepository: SlackRepository = provideSlackRepository(mainActivity.applicationContext)
}

// @ExperimentalCoroutinesApi
// @FlowPreview
// val ViewModelContext.component: Component
//     get() = (app() as TeamExplorerApplication).component

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

package app.eyal.teamexplorer.wiring

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.room.Room
import app.eyal.teamexplorer.TeamExplorerApplication
import app.eyal.teamexplorer.repository.SlackService
import app.eyal.teamexplorer.presenter.Presenter
import app.eyal.teamexplorer.repository.RealSlackRepository
import app.eyal.teamexplorer.repository.SlackDatabase
import app.eyal.teamexplorer.repository.SlackRepository
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.ViewModelContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@ExperimentalCoroutinesApi
interface Component {
    val presenterFactory: Presenter.Factory
}

@ExperimentalCoroutinesApi
@FlowPreview
internal class RealComponent(context: Application) : Component {
    override val presenterFactory: Presenter.Factory
        get() = Presenter.Factory(slackRepository)

    private val slackRepository: SlackRepository = provideSlackRepository(context)
}

@ExperimentalCoroutinesApi
@FlowPreview
fun newComponent(application: TeamExplorerApplication): Component = RealComponent(application)

@ExperimentalCoroutinesApi
val ViewModelContext.component: Component
    get() = (app() as TeamExplorerApplication).component

private fun provideSlackService(): SlackService {
    val interceptor = HttpLoggingInterceptor()
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
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

package app.eyal.teamexplorer.wiring

import android.app.Activity
import androidx.fragment.app.Fragment
import app.eyal.teamexplorer.repository.SlackService
import app.eyal.teamexplorer.presenter.Presenter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface Component {
    val presenterFactory: Presenter.Factory
}

object RealComponent : Component {
    override val presenterFactory: Presenter.Factory
        get() = Presenter.Factory(slackService)

    private val slackService: SlackService = provideSlackService()
}

val Activity.component: Component
    get() = RealComponent

val Fragment.component: Component
    get() = RealComponent

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

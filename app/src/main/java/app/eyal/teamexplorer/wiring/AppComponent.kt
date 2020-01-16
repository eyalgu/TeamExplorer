package app.eyal.teamexplorer.wiring

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import app.eyal.teamexplorer.repository.RealTokenStorage
import app.eyal.teamexplorer.repository.TokenStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

interface AppComponent {
    val tokenStorage: TokenStorage
    val context: Context
}

@ExperimentalCoroutinesApi
@FlowPreview
class RealAppComponent(override val context: Application): AppComponent {
    override val tokenStorage: TokenStorage =
        RealTokenStorage(context.getSharedPreferences("token", Context.MODE_PRIVATE))
}
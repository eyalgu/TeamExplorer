package app.eyal.teamexplorer

import android.app.Application
import app.eyal.teamexplorer.wiring.AppComponent
import app.eyal.teamexplorer.wiring.RealAppComponent
// import app.eyal.teamexplorer.wiring.newComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class TeamExplorerApplication : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = RealAppComponent(this)
    }
}
package app.eyal.teamexplorer

import android.app.Application
import app.eyal.teamexplorer.wiring.Component
import app.eyal.teamexplorer.wiring.newComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class TeamExplorerApplication : Application() {

    private lateinit var _component: Component
    val component: Component
        get() = _component

    override fun onCreate() {
        super.onCreate()
        _component = newComponent(this)
    }

}
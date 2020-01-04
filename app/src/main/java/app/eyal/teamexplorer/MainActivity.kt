package app.eyal.teamexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.eyal.teamexplorer.wiring.Component
import app.eyal.teamexplorer.wiring.RealComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {

    lateinit var component: Component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        component = RealComponent(this)
    }
}

package app.eyal.teamexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import app.eyal.teamexplorer.wiring.AppComponent
import app.eyal.teamexplorer.wiring.UserComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {

    lateinit var presenter: MainActivityPresenter

    val userComponent: UserComponent?
        get() = presenter.userComponent
    val appComponent: AppComponent
        get() = (application as TeamExplorerApplication).component

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = ViewModelProvider(this, MainActivityPresenter.Factory(appComponent))[MainActivityPresenter::class.java]
        setContentView(R.layout.main_activity)

        lifecycleScope.launch {
            for (destination in presenter.nextDestination) {
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph, false).build()
                findNavController(R.id.nav_host_fragment).navigate(destination, null, navOptions)
            }
        }
    }
}

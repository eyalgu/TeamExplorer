package app.eyal.teamexplorer.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import app.eyal.teamexplorer.presenter.BasePresenter
import com.airbnb.mvrx.BaseMvRxFragment
import kotlinx.coroutines.launch

abstract class BaseFragment<P: BasePresenter<*>>: BaseMvRxFragment() {
    protected abstract val presenter: P

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            with(presenter.navDirections.receive()) {
                findNavController().navigate(first, second)
            }
        }
    }
}

private fun NavController.navigate(directions: NavDirections, extras: Navigator.Extras?) =
    if (extras!= null) {
        navigate(directions, extras)
    } else {
        navigate(directions)
    }
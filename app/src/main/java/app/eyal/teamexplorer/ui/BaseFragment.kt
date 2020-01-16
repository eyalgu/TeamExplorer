package app.eyal.teamexplorer.ui

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import app.eyal.teamexplorer.presenter.BaseFragmentPresenter
import com.airbnb.mvrx.BaseMvRxFragment
import kotlinx.coroutines.launch

abstract class BaseFragment<Presenter: BaseFragmentPresenter<*>, Binding: ViewDataBinding>: BaseMvRxFragment() {
    protected abstract val presenter: Presenter
    private var _binding: Binding? = null

    protected var binding: Binding
        get() = checkNotNull(_binding)
        set(value) {
            _binding = value
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            with(presenter.navDirections.receive()) {
                findNavController().navigate(first, second)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun NavController.navigate(directions: NavDirections, extras: Navigator.Extras?) =
    if (extras!= null) {
        navigate(directions, extras)
    } else {
        navigate(directions)
    }
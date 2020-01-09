package app.eyal.teamexplorer.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.eyal.teamexplorer.presenter.BasePresenter
import com.airbnb.mvrx.BaseMvRxFragment
import kotlinx.coroutines.launch

abstract class BaseFragment<P: BasePresenter<*>>: BaseMvRxFragment() {
    protected abstract val presenter: P

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            findNavController().navigate(presenter.navDirections.receive())
        }
    }
}
package app.eyal.teamexplorer.presenter

import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

abstract class BasePresenter<S : MvRxState>(
    initialState: S,
    debugMode: Boolean
) : BaseMvRxViewModel<S>(initialState, debugMode) {

    private val _navDirections: Channel<Pair<NavDirections, FragmentNavigator.Extras?>> =
        Channel(Channel.CONFLATED)


    val navDirections: ReceiveChannel<Pair<NavDirections, FragmentNavigator.Extras?>>
        get() = _navDirections

    protected fun nextDestinations(navDirections: NavDirections, extras: FragmentNavigator.Extras? = null) {
        _navDirections.offer(navDirections to extras)
    }
}
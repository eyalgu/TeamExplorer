package app.eyal.teamexplorer.presenter

import androidx.navigation.NavDirections
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

abstract class BasePresenter<S : MvRxState>(
    initialState: S,
    debugMode: Boolean
) : BaseMvRxViewModel<S>(initialState, debugMode) {

    private val _navDirections: Channel<NavDirections> by lazy {
        Channel<NavDirections>(Channel.CONFLATED)
    }

    val navDirections: ReceiveChannel<NavDirections>
        get() = _navDirections

    protected fun nextDestinations(navDirections: NavDirections) {
        _navDirections.offer(navDirections)
    }
}
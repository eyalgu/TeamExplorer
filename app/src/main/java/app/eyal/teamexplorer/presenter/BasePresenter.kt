package app.eyal.teamexplorer.presenter

import android.app.Activity
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

abstract class BaseFragmentPresenter<S : MvRxState>(
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

abstract class BaseActivityPresenter<S: MvRxState>(
    initialState: S,
    debugMode: Boolean
) : BaseMvRxViewModel<S>(initialState, debugMode) {

    private val _navDirections: Channel<Class<out Activity>> =
        Channel(Channel.CONFLATED)


    val navDirections: ReceiveChannel<Class<out Activity>>
        get() = _navDirections

    protected fun nextActivity(clazz: Class<out Activity>) {
        _navDirections.offer(clazz)
    }
}
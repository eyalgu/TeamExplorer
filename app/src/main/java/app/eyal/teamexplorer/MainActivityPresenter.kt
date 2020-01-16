package app.eyal.teamexplorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.eyal.teamexplorer.wiring.AppComponent
import app.eyal.teamexplorer.wiring.RealUserComponent
import app.eyal.teamexplorer.wiring.UserComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivityPresenter(appComponent: AppComponent): ViewModel() {

    class Factory(private val appComponent: AppComponent) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            check(modelClass == MainActivityPresenter::class.java)
            return MainActivityPresenter(appComponent) as T
        }
    }

    private var _userComponent: UserComponent? = null
    val userComponent: UserComponent?
        get() = _userComponent

    private val _nextDestination: Channel<Int> = Channel(Channel.CONFLATED)
    val nextDestination: ReceiveChannel<Int>
        get() = _nextDestination

    init {
        viewModelScope.launch {
            appComponent.tokenStorage.token
                .onEach {
                    if (it == null) {
                        _nextDestination.offer(R.id.loginFragment)
                    } else {
                        _userComponent = RealUserComponent(appComponent.context, it)
                        _nextDestination.offer(R.id.teamListFragment)
                    }
                }.collect()
        }

    }
}
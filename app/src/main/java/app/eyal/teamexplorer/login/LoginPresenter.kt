package app.eyal.teamexplorer.login

import androidx.lifecycle.viewModelScope
import app.eyal.teamexplorer.TeamExplorerApplication
import app.eyal.teamexplorer.presenter.BaseActivityPresenter
import app.eyal.teamexplorer.presenter.BaseFragmentPresenter
import app.eyal.teamexplorer.repository.TokenStorage
import app.eyal.teamexplorer.teamlist.TeamListFragmentDirections
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class LoginViewState(val token: String): MvRxState {
}

@ExperimentalCoroutinesApi
@FlowPreview
class LoginPresenter(
    initialState: LoginViewState,
    private val tokenStorage: TokenStorage
): BaseFragmentPresenter<LoginViewState>(initialState = initialState, debugMode = true) {
    companion object : MvRxViewModelFactory<LoginPresenter, LoginViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: LoginViewState
        ): LoginPresenter? = with(viewModelContext.activity) {
            val tokenStorage = (application as TeamExplorerApplication).component.tokenStorage
            LoginPresenter(state, tokenStorage)
        }

        override fun initialState(viewModelContext: ViewModelContext): LoginViewState? {
            return LoginViewState("")
        }
    }

    fun onTextChange(token: String) = withState {
        if (it.token != token) {
            setState { copy(token = token) }
        }
    }

    fun onOkClicked() = withState {
        if (!it.token.isBlank()) {
            viewModelScope.launch {
                tokenStorage.setToken(it.token)
            }
            // nextDestinations(
            //     LoginFragmentDirections.actionLoginFragmentToTeamListFragment()
            // )
        }
    }
}
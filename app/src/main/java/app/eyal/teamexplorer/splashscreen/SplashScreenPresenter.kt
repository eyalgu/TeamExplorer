package app.eyal.teamexplorer.splashscreen

import app.eyal.teamexplorer.TeamExplorerApplication
import app.eyal.teamexplorer.presenter.BaseFragmentPresenter
import app.eyal.teamexplorer.repository.TokenStorage
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

data class SlashScreenState(val unit: Unit): MvRxState {
    companion object {
        val Only = SlashScreenState(Unit)
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
class SplashScreenPresenter(
    initialViewState: SlashScreenState,
    tokenStorage: TokenStorage)
    : BaseFragmentPresenter<SlashScreenState>(initialState = initialViewState, debugMode = true) {

    companion object : MvRxViewModelFactory<SplashScreenPresenter, SlashScreenState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: SlashScreenState
        ): SplashScreenPresenter? = with(viewModelContext.activity) {
            val tokenStorage = (application as TeamExplorerApplication).component.tokenStorage
            SplashScreenPresenter(state, tokenStorage)
        }

        override fun initialState(viewModelContext: ViewModelContext): SlashScreenState? = SlashScreenState.Only
    }


    // init {
    //     if (tokenStorage.token != null) {
    //         nextDestinations(SplashScreenFragmentDirections.actionSplashScreenFragmentToTeamListFragment())
    //     } else {
    //         nextDestinations(SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment())
    //     }
    // }
}
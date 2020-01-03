package app.eyal.teamexplorer.presenter

import androidx.lifecycle.viewModelScope
import app.eyal.teamexplorer.repository.FeedEntity
import app.eyal.teamexplorer.repository.SlackRepository
import app.eyal.teamexplorer.wiring.component
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class Presenter(initialViewState: MainViewState, slackRepository: SlackRepository) :
    BaseMvRxViewModel<MainViewState>(initialState = initialViewState, debugMode = true) {

    class Factory(
        private val slackRepository: SlackRepository
    ) {
        fun create(initialViewState: MainViewState): Presenter =
            Presenter(
                initialViewState = initialViewState,
                slackRepository = slackRepository
            )
    }

    companion object : MvRxViewModelFactory<Presenter, MainViewState> {
        override fun create(viewModelContext: ViewModelContext, state: MainViewState): Presenter? {
            return viewModelContext.component.presenterFactory.create(state)
        }

        override fun initialState(viewModelContext: ViewModelContext): MainViewState? =
            MainViewState.Loading
    }

    init {
        viewModelScope.launch {
            slackRepository.userList()
                .flowOn(Dispatchers.IO)
                .map {
                    when (it) {
                        is SlackRepository.FetchResult.Loading -> MainViewState.Loading
                        is SlackRepository.FetchResult.Error -> MainViewState.Error(it.errorMessage)
                        is SlackRepository.FetchResult.Data -> MainViewState.Data(it.value.map { it.toRowState()})
                    }
                }.onEach { setState { it } }
                // .catch { setState { MainViewState.Error(it.message ?: it.javaClass.simpleName) } }
                .collect()
        }
    }
}

private fun FeedEntity.toRowState() = UserRowState(
    imageUrl = imageUrl, // TODO select based on screen size
    name = displayName,
    id = userId
)

package app.eyal.teamexplorer.presenter

import androidx.lifecycle.viewModelScope
import app.eyal.teamexplorer.repository.SlackService
import app.eyal.teamexplorer.repository.User
import app.eyal.teamexplorer.repository.UserList
import app.eyal.teamexplorer.wiring.component
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Presenter(initialViewState: MainViewState, slackService: SlackService) :
    BaseMvRxViewModel<MainViewState>(initialState = initialViewState, debugMode = true) {

    class Factory(
        private val slackService: SlackService
    ) {
        fun create(initialViewState: MainViewState): Presenter =
            Presenter(
                initialViewState = initialViewState,
                slackService = slackService
            )
    }

    companion object : MvRxViewModelFactory<Presenter, MainViewState> {
        override fun create(viewModelContext: ViewModelContext, state: MainViewState): Presenter? {

            val component = if (viewModelContext is FragmentViewModelContext) {
                // If the ViewModel has a fragment scope it will be a FragmentViewModelContext, and you can access the fragment.
                viewModelContext.fragment.component
            } else {
                // The activity owner will be available for both fragment and activity view models.
                viewModelContext.activity.component
            }
            return component.presenterFactory.create(state)
        }

        override fun initialState(viewModelContext: ViewModelContext): MainViewState? =
            MainViewState.Loading
    }

    init {
        viewModelScope.launch {
            try {
                val list = withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                    slackService.userList(SlackService.TOKEN)
                }
                setState {
                    if (list.ok) {
                        MainViewState.Data(list.members!!.toRowState())
                    } else {
                        MainViewState.Error(list.error!!)
                    }
                }
            } catch (e: Exception) {
                setState { MainViewState.Error(e.message ?: e.javaClass.simpleName) }
            }
        }
    }
}

private fun List<User>.toRowState() = map {
    UserRowState(
        imageUrl = it.profile.image_192, // TODO select based on screen size
        name = it.profile.display_name,
        id = it.id
    )
}

package app.eyal.teamexplorer.presenter

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import app.eyal.teamexplorer.repository.FeedEntity
import app.eyal.teamexplorer.repository.SlackRepository
import app.eyal.teamexplorer.ui.TeamListFragment
import app.eyal.teamexplorer.ui.TeamListFragmentDirections
import app.eyal.teamexplorer.ui.loadImage
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class UserRowState(
    val imageUrl: String,
    // val request: RequestBuilder<Bitmap>,
    val name: String,
    val id: String
) {
    val onClickAction: TeamListAction = TeamListAction.UserRowClicked(id)
}

data class TeamListViewState(
    val loadingIndicatorVisibility: Int = View.GONE,
    val errorMessageVisibility: Int = View.GONE,
    val userListVisibility: Int = View.GONE,
    val errorMessage: String? = null,
    val userList: List<UserRowState>? = null
) : MvRxState {

    companion object {
        val Loading =
            TeamListViewState(loadingIndicatorVisibility = View.VISIBLE)

        fun Error(errorMessage: String) = TeamListViewState(
            errorMessageVisibility = View.VISIBLE,
            errorMessage = errorMessage
        )

        fun Data(userList: List<UserRowState>) =
            TeamListViewState(
                userListVisibility = View.VISIBLE,
                userList = userList
            )
    }
}

sealed class TeamListAction {
    data class UserRowClicked(val userId: String): TeamListAction()
}

@ExperimentalCoroutinesApi
@FlowPreview
class TeamListPresenter(
    initialViewState: TeamListViewState,
    slackRepository: SlackRepository,
    private val navController: NavController,
    private val glide: RequestManager) :
    BaseMvRxViewModel<TeamListViewState>(initialState = initialViewState, debugMode = true) {

    class Factory(
        private val slackRepository: SlackRepository,
        private val glide: RequestManager
    ) {
        fun create(initialViewState: TeamListViewState, navController: NavController): TeamListPresenter =
            TeamListPresenter(
                initialViewState = initialViewState,
                slackRepository = slackRepository,
                navController = navController,
                glide = glide
            )
    }

    companion object : MvRxViewModelFactory<TeamListPresenter, TeamListViewState> {
        override fun create(viewModelContext: ViewModelContext, state: TeamListViewState): TeamListPresenter? {
            return viewModelContext.teamListFragment.component.presenterFactory.create(state, viewModelContext.teamListFragment.findNavController())
        }

        override fun initialState(viewModelContext: ViewModelContext): TeamListViewState? =
            TeamListViewState.Loading

        private val ViewModelContext.teamListFragment
            get() = (this as FragmentViewModelContext).fragment as TeamListFragment

    }

    init {
        viewModelScope.launch {
            slackRepository.userList()
                .flowOn(Dispatchers.IO)
                .map {
                    when (it) {
                        is SlackRepository.FetchResult.Loading -> TeamListViewState.Loading
                        is SlackRepository.FetchResult.Error -> TeamListViewState.Error(it.errorMessage)
                        is SlackRepository.FetchResult.Data -> TeamListViewState.Data(it.value.map { it.toRowState()})
                    }
                }.onEach { setState { it } }
                // .catch { setState { MainViewState.Error(it.message ?: it.javaClass.simpleName) } }
                .collect()
        }
    }

    private fun FeedEntity.toRowState() = UserRowState(
        imageUrl = imageUrl, // TODO select based on screen size
        name = displayName,
        id = userId
    )


    fun performAction(action: TeamListAction): Unit = when(action) {
        is TeamListAction.UserRowClicked -> {
            navController.navigate(TeamListFragmentDirections.actionTeamListFragmentToUserProfileFragment(action.userId))
        }

    }
}

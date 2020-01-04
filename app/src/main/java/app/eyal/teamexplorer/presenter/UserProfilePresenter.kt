package app.eyal.teamexplorer.presenter

import android.view.View
import androidx.lifecycle.viewModelScope
import app.eyal.teamexplorer.repository.SlackRepository
import app.eyal.teamexplorer.repository.UserEntity
import app.eyal.teamexplorer.ui.UserProfileFragment
import app.eyal.teamexplorer.ui.UserProfileFragmentArgs
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ProfileDetailsState(val name: String, val status: String, val profilePictureUrl: String)

data class UserProfileViewState(
    val loadingIndicatorVisibility: Int = View.GONE,
    val errorMessageVisibility: Int = View.GONE,
    val userProfileVisibility: Int = View.GONE,
    val errorMessage: String? = null,
    val profileDetailsState: ProfileDetailsState? = null
) : MvRxState {
    companion object {
        val Loading =
            UserProfileViewState(loadingIndicatorVisibility = View.VISIBLE)

        fun Error(errorMessage: String) = UserProfileViewState(
            errorMessageVisibility = View.VISIBLE,
            errorMessage = errorMessage
        )

        fun Data(profileDetailsState: ProfileDetailsState) =
            UserProfileViewState(
                userProfileVisibility = View.VISIBLE,
                profileDetailsState = profileDetailsState
            )
    }
}

@ExperimentalCoroutinesApi
@FlowPreview
class UserProfilePresenter(
    initialViewState: UserProfileViewState,
    private val slackRepository: SlackRepository,
    private val args: UserProfileFragmentArgs
) :
    BaseMvRxViewModel<UserProfileViewState>(initialState = initialViewState, debugMode = true) {

    class Factory(
        private val slackRepository: SlackRepository,
        private val args: UserProfileFragmentArgs
    ) {
        fun create(initialViewState: UserProfileViewState) = UserProfilePresenter(
            initialViewState = initialViewState,
            slackRepository = slackRepository,
            args = args
        )
    }

    companion object : MvRxViewModelFactory<UserProfilePresenter, UserProfileViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: UserProfileViewState
        ): UserProfilePresenter? {
            return viewModelContext.userProfileFragment.userComponent.userProfilePresenterFactory.create(
                state
            )
        }

        override fun initialState(viewModelContext: ViewModelContext): UserProfileViewState? =
            UserProfileViewState.Loading

        private val ViewModelContext.userProfileFragment
            get() = (this as FragmentViewModelContext).fragment as UserProfileFragment
    }

    init {
        viewModelScope.launch {
            slackRepository.user(args.userId)
                .flowOn(Dispatchers.IO)
                .map {
                    when (it) {
                        is SlackRepository.FetchResult.Loading -> UserProfileViewState.Loading
                        is SlackRepository.FetchResult.Error -> UserProfileViewState.Error(it.errorMessage)
                        is SlackRepository.FetchResult.Data -> UserProfileViewState.Data(it.value.toUserProfileViewState())
                    }
                }.onEach { setState { it } }
                // .catch { setState { MainViewState.Error(it.message ?: it.javaClass.simpleName) } }
                .collect()
        }
    }
}

private fun UserEntity.toUserProfileViewState() = ProfileDetailsState(
    name = display_name,
    profilePictureUrl = image_192,
    status = status_text

)

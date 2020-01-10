package app.eyal.teamexplorer.presenter

import android.view.View
import androidx.lifecycle.viewModelScope
import app.eyal.teamexplorer.repository.SlackRepository
import app.eyal.teamexplorer.repository.UserEntity
import app.eyal.teamexplorer.ui.UserProfileFragment
import app.eyal.teamexplorer.ui.UserProfileFragmentArgs
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class UserProfileDetailsState(
    val name: String = "",
    val status: String = "",
    val profilePictureUrl: String = ""
) {
    companion object {
        val Empty = UserProfileDetailsState()
    }
}

data class UserProfileViewState(
    val userProfileDetailsVisibility: Int = View.GONE,
    val loadingIndicatorVisibility: Int = View.GONE,
    val errorMessageVisibility: Int = View.GONE,
    val errorMessage: String = "",
    val userProfileDetailsState: UserProfileDetailsState = UserProfileDetailsState.Empty
): MvRxState {
    companion object {
        val Loading = UserProfileViewState(loadingIndicatorVisibility = View.VISIBLE)
        fun Error(errorMessage: String) = UserProfileViewState(
            errorMessageVisibility = View.VISIBLE,
            errorMessage = errorMessage
        )
        fun Data(userProfileDetailsState: UserProfileDetailsState) = UserProfileViewState(
            userProfileDetailsVisibility = View.VISIBLE,
            userProfileDetailsState = userProfileDetailsState
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
    BasePresenter<UserProfileViewState>(initialState = initialViewState, debugMode = true) {

    class Factory(
        private val slackRepository: SlackRepository,
        private val args: UserProfileFragmentArgs,
        private val glide: RequestManager
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
            return viewModelContext.userProfileFragment.component.presenterFactory.create(
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
                .map {
                    when(it) {
                        is SlackRepository.FetchResult.Loading -> UserProfileViewState.Loading
                        is SlackRepository.FetchResult.Error -> UserProfileViewState.Error(it.errorMessage)
                        is SlackRepository.FetchResult.Data<UserEntity> -> UserProfileViewState.Data(it.value.toUserProfileDetailsState())
                    }
                }
                .onEach { setState { it } }
                .collect()
        }
    }

}

fun UserEntity.toUserProfileDetailsState() = UserProfileDetailsState(
    name = display_name,
    status = status_text,
    profilePictureUrl = image_192
)
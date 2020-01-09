package app.eyal.teamexplorer.presenter

import android.os.Parcelable
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
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Parcelize
data class UserProfileViewState(
    val name: String,
    val status: String,
    val profilePictureUrl: String
): MvRxState, Parcelable

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
            viewModelContext.userProfileFragment.args.user

        private val ViewModelContext.userProfileFragment
            get() = (this as FragmentViewModelContext).fragment as UserProfileFragment
    }

}

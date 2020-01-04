package app.eyal.teamexplorer.wiring

import app.eyal.teamexplorer.presenter.UserProfilePresenter
import app.eyal.teamexplorer.ui.UserProfileFragment
import app.eyal.teamexplorer.ui.UserProfileFragmentArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
interface UserComponent {
    val userProfilePresenterFactory: UserProfilePresenter.Factory
}

@ExperimentalCoroutinesApi
@FlowPreview
class RealUserComponent(component: Component, args: UserProfileFragmentArgs): UserComponent {
    override val userProfilePresenterFactory: UserProfilePresenter.Factory =
        UserProfilePresenter.Factory(
            slackRepository = component.slackRepository,
            args = args
        )
}

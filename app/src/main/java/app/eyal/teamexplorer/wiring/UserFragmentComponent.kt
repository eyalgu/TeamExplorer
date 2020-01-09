package app.eyal.teamexplorer.wiring

import app.eyal.teamexplorer.presenter.UserProfilePresenter
import app.eyal.teamexplorer.ui.UserProfileFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
interface UserFragmentComponent {
    val presenterFactory: UserProfilePresenter.Factory
    val glide: RequestManager
}

@ExperimentalCoroutinesApi
@FlowPreview
class RealUserFragmentComponent(
    mainActivityComponent: MainActivityComponent,
    fragment: UserProfileFragment): UserFragmentComponent {

    override val glide = Glide.with(fragment)

    override val presenterFactory: UserProfilePresenter.Factory =
        UserProfilePresenter.Factory(
            slackRepository = mainActivityComponent.slackRepository,
            args = fragment.args,
            glide = glide
        )

}

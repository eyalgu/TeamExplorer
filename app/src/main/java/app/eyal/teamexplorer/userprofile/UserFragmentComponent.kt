package app.eyal.teamexplorer.userprofile

import app.eyal.teamexplorer.wiring.UserComponent
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
interface UserFragmentComponent {
    val presenterFactory: UserProfileFragmentPresenter.Factory
    val glide: RequestManager
}

@ExperimentalCoroutinesApi
@FlowPreview
class RealUserFragmentComponent(
    userComponent: UserComponent,
    fragment: UserProfileFragment
): UserFragmentComponent {

    override val glide = Glide.with(fragment)

    override val presenterFactory: UserProfileFragmentPresenter.Factory =
        UserProfileFragmentPresenter.Factory(
            slackRepository = userComponent.slackRepository,
            args = fragment.args,
            glide = glide
        )

}

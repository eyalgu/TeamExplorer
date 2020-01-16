package app.eyal.teamexplorer.teamlist

import app.eyal.teamexplorer.wiring.UserComponent
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
interface TeamListFragmentComponent {
    val presenterFactory: TeamListFragmentPresenter.Factory
    val glide: RequestManager
}

@ExperimentalCoroutinesApi
@FlowPreview
internal class RealTeamListFragmentComponent(
    userComponent: UserComponent,
    fragment: TeamListFragment
) :
    TeamListFragmentComponent {

    override val glide = Glide.with(fragment)

    override val presenterFactory: TeamListFragmentPresenter.Factory =
        TeamListFragmentPresenter.Factory(
            userComponent.slackRepository
        )
}
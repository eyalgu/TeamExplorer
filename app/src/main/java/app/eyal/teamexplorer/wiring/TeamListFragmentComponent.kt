package app.eyal.teamexplorer.wiring

import app.eyal.teamexplorer.presenter.TeamListPresenter
import app.eyal.teamexplorer.ui.TeamListFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
interface TeamListFragmentComponent {
    val presenterFactory: TeamListPresenter.Factory
    val glide: RequestManager
}

@ExperimentalCoroutinesApi
@FlowPreview
internal class RealTeamListFragmentComponent(
    activityComponent: MainActivityComponent,
    fragment: TeamListFragment) :
    TeamListFragmentComponent {

    override val glide = Glide.with(fragment)

    override val presenterFactory: TeamListPresenter.Factory =
        TeamListPresenter.Factory(
            activityComponent.slackRepository
        )
}
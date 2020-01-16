package app.eyal.teamexplorer.teamlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.transition.TransitionInflater
import app.eyal.teamexplorer.MainActivity
import app.eyal.teamexplorer.PlaceholderRowItemBindingModel_
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.UserRowItemBindingModel_
import app.eyal.teamexplorer.databinding.TeamListFragmentBinding
import app.eyal.teamexplorer.databinding.UserRowItemBinding
import app.eyal.teamexplorer.ui.BaseFragment
import app.eyal.teamexplorer.ui.loadImage
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class TeamListFragment : BaseFragment<TeamListFragmentPresenter, TeamListFragmentBinding>() {

    override val presenter: TeamListFragmentPresenter by fragmentViewModel()
    lateinit var component: TeamListFragmentComponent
    lateinit var controller: MyController

    private val glide: RequestManager
        get() = component.glide

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = RealTeamListFragmentComponent(
            userComponent = (activity!! as MainActivity).userComponent!!,
            fragment = this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.team_list_fragment, container, false)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        binding.recyclerView.addGlidePreloader(
            glide,
            preloader = glidePreloader { requestManager, epoxyModel: UserRowItemBindingModel_, _ ->
                requestManager.loadImage(epoxyModel.viewState().imageUrl)
            }
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        binding.recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
        controller = MyController()
        binding.recyclerView.setController(controller)
        invalidate()
    }

    override fun invalidate(): Unit = withState(presenter) { state ->
        binding.viewState = state
        state.userList?.let {
            controller.submitList(it)
        }
    }

    inner class MyController: PagedListEpoxyController<UserRowState>() {
        override fun buildItemModel(currentPosition: Int, item: UserRowState?): EpoxyModel<*> {
            return if (item == null) {
                PlaceholderRowItemBindingModel_().id("loading")
            } else {
                UserRowItemBindingModel_().apply {
                    viewState(item)
                    onClick { v ->
                        val extras = FragmentNavigatorExtras(
                            v.findViewById<ImageView>(R.id.avatar) to item.imageUrl
                        )
                        presenter.performAction(item.onClickActionBuilder, extras)
                    }
                    id(item.id)
                    onBind { _, view, _ ->
                        val binding = view.dataBinding as UserRowItemBinding
                        glide.loadImage(item.imageUrl).into(binding.avatar)
                    }

                    onUnbind { _, view ->
                        val binding = view.dataBinding as UserRowItemBinding
                        glide.clear(binding.avatar)
                    }
                }
            }
        }
    }
}
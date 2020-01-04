package app.eyal.teamexplorer.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.UserRowItemBindingModel_
import app.eyal.teamexplorer.databinding.TeamListFragmentBinding
import app.eyal.teamexplorer.databinding.UserRowItemBinding
import app.eyal.teamexplorer.presenter.TeamListPresenter
import app.eyal.teamexplorer.userRowItem
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class TeamListFragment : BaseMvRxFragment() {

    private val presenter: TeamListPresenter by fragmentViewModel()
    lateinit var binding: TeamListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.team_list_fragment, container, false)
        binding.recyclerView.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, epoxyModel: UserRowItemBindingModel_, _ ->
                requestManager.loadImage(epoxyModel.viewState().imageUrl)
            }
        )
        return binding.root
    }

    override fun invalidate() = withState(presenter) { state ->
        binding.viewState = state
        binding.recyclerView.withModels {
            state.userList?.forEach {
                userRowItem {
                    viewState(it)
                    actionHandler(presenter)
                    id(it.id)
                    onBind  { _, view, _ ->
                        val binding =  view.dataBinding as UserRowItemBinding
                        Glide.with(this@TeamListFragment).loadImage(it.imageUrl).into(binding.avatar)
                    }

                    onUnbind { _, view ->
                        val binding =  view.dataBinding as UserRowItemBinding
                        Glide.with(this@TeamListFragment).clear(binding.avatar)
                    }
                }
            }
        }
    }
}

// TODO move to presenter
fun RequestManager.loadImage(url: String): RequestBuilder<Bitmap> {

    val options = RequestOptions
        .diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)
        .dontAnimate()

    return asBitmap()
        .apply(options)
        .load(url)
}
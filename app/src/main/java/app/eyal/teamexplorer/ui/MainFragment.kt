package app.eyal.teamexplorer.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.UserRowItemBindingModel_
import app.eyal.teamexplorer.databinding.MainFragmentBinding
import app.eyal.teamexplorer.databinding.UserRowItemBinding
import app.eyal.teamexplorer.presenter.Presenter
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
import com.bumptech.glide.signature.ObjectKey
import kotlinx.android.synthetic.main.user_row_item.*

class MainFragment : BaseMvRxFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val presenter: Presenter by fragmentViewModel()
    lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.recyclerView.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, epoxyModel: UserRowItemBindingModel_, viewData ->
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
                    id(it.id)
                    onBind  { model , view, position ->
                        val binding =  view.dataBinding as UserRowItemBinding
                        Glide.with(this@MainFragment).loadImage(it.imageUrl).into(binding.avatar)
                    }

                    onUnbind { model, view ->
                        val binding =  view.dataBinding as UserRowItemBinding
                        Glide.with(this@MainFragment).clear(binding.avatar)
                    }
                }
            }
        }
    }


}

fun RequestManager.loadImage(url: String): RequestBuilder<Bitmap> {

    val options = RequestOptions
        .diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)
        .dontAnimate()

    return asBitmap()
        .apply(options)
        .load(url)
}
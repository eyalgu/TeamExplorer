package app.eyal.teamexplorer.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.databinding.MainFragmentBinding
import app.eyal.teamexplorer.presenter.Presenter
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
            preloader = glidePreloader { requestManager, epoxyModel: UserRowModel, viewData ->
                requestManager.loadImage(epoxyModel.imageUrl)
            }
        )
        return binding.root
    }

    override fun invalidate() = withState(presenter) { state ->
        binding.viewState = state
        binding.recyclerView.withModels {
            state.userList?.forEach {
                userRow {
                    id("image_id_${it.imageUrl}")
                    imageUrl(it.imageUrl)
                    displayName(it.name)
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
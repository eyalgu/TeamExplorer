package app.eyal.teamexplorer.userprofile

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import app.eyal.teamexplorer.MainActivity
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.databinding.UserProfileFragmentBinding
import app.eyal.teamexplorer.ui.BaseFragment
import app.eyal.teamexplorer.ui.loadImage
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class UserProfileFragment : BaseFragment<UserProfileFragmentPresenter, UserProfileFragmentBinding>() {

    val args: UserProfileFragmentArgs by navArgs()
    lateinit var component: UserFragmentComponent

    override val presenter: UserProfileFragmentPresenter by fragmentViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = RealUserFragmentComponent(
            userComponent = (activity!! as MainActivity).userComponent!!,
            fragment = this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.user_profile_fragment, container, false)

        return binding.root
    }

    override fun invalidate(): Unit = withState(presenter) { state ->
        binding.viewState = state
        val listener: RequestListener<Bitmap> = object: RequestListener<Bitmap> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                startPostponedEnterTransition()
                return false
            }

            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                startPostponedEnterTransition()
                return false
            }
        }
        if (state.userProfileDetailsState != UserProfileDetailsState.Empty) {
            component.glide.loadImage(state.userProfileDetailsState.profilePictureUrl)
                .listener(listener)
                .into(binding.profilePicture)

        } else {
            component.glide.clear(binding.profilePicture)
        }
    }
}

package app.eyal.teamexplorer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import app.eyal.teamexplorer.MainActivity
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.databinding.UserProfileFragmentBinding
import app.eyal.teamexplorer.presenter.UserProfilePresenter
import app.eyal.teamexplorer.wiring.RealUserFragmentComponent
import app.eyal.teamexplorer.wiring.UserFragmentComponent
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class UserProfileFragment : BaseMvRxFragment() {

    val args: UserProfileFragmentArgs by navArgs()
    lateinit var component: UserFragmentComponent

    private val presenter: UserProfilePresenter by fragmentViewModel()
    lateinit var binding: UserProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.user_profile_fragment, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = RealUserFragmentComponent(
            mainActivityComponent = (activity!! as MainActivity).mainActivityComponent,
            fragment = this
        )
    }

    override fun invalidate(): Unit = withState(presenter) { state ->
        binding.viewState = state
        // TODO image loading logic to presenter
        if (state.profileDetailsState != null ) {
            component.glide.loadImage(state.profileDetailsState.profilePictureUrl)
                .into(binding.profilePicture)
        } else {
            component.glide.clear(binding.profilePicture)
        }
    }
}

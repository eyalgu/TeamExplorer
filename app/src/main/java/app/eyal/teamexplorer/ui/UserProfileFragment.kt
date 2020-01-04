package app.eyal.teamexplorer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import app.eyal.teamexplorer.MainActivity
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.TeamExplorerApplication
import app.eyal.teamexplorer.UserRowItemBindingModel_
import app.eyal.teamexplorer.databinding.UserProfileFragmentBinding
import app.eyal.teamexplorer.databinding.UserRowItemBinding
import app.eyal.teamexplorer.presenter.UserProfilePresenter
import app.eyal.teamexplorer.userRowItem
import app.eyal.teamexplorer.wiring.Component
import app.eyal.teamexplorer.wiring.RealUserComponent
import app.eyal.teamexplorer.wiring.UserComponent
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.user_row_item.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class UserProfileFragment : BaseMvRxFragment() {

    private val args: UserProfileFragmentArgs by navArgs()
    lateinit var userComponent: UserComponent

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
        userComponent = RealUserComponent(
            component = (activity!! as MainActivity).component,
            args = args
        )
    }

    override fun invalidate(): Unit = withState(presenter) { state ->
        binding.viewState = state
        // TODO image loading logic to presenter
        if (state.profileDetailsState != null ) {
            Glide.with(this@UserProfileFragment)
                .loadImage(state.profileDetailsState.profilePictureUrl)
                .into(binding.profilePicture)
        } else {
            Glide.with(this@UserProfileFragment).clear(binding.profilePicture)
        }
    }
}

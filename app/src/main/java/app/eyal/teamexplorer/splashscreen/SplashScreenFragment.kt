package app.eyal.teamexplorer.splashscreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.databinding.SplashScreenBinding
import app.eyal.teamexplorer.ui.BaseFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class SplashScreenFragment : BaseFragment<SplashScreenPresenter, SplashScreenBinding>() {

    override val presenter: SplashScreenPresenter by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.splash_screen, container, false)
        return binding.root
    }

    override fun invalidate() {}

}
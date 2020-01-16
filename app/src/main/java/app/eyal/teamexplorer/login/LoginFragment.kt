package app.eyal.teamexplorer.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.databinding.LoginScreenBinding
import app.eyal.teamexplorer.ui.BaseFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import kotlinx.android.synthetic.main.login_screen.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class LoginFragment : BaseFragment<LoginPresenter, LoginScreenBinding>() {

    override val presenter: LoginPresenter by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_screen, container, false)
        return binding.root
    }

    override fun invalidate() = withState(presenter) { state ->
        binding.viewState = state
        binding.token.doAfterTextChanged { presenter.onTextChange(it.toString()) }
        binding.okBotten.setOnClickListener { presenter.onOkClicked() }
        Unit
    }
}
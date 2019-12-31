package app.eyal.teamexplorer.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import app.eyal.teamexplorer.R
import app.eyal.teamexplorer.databinding.MainFragmentBinding
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import kotlinx.android.synthetic.main.main_fragment.*

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
        return binding.root
    }

    override fun invalidate() = withState(presenter) { state ->
        binding.viewState = state
        binding.recyclerView.withModels {
            state.userList?.forEach {

            }
        }
    }


}

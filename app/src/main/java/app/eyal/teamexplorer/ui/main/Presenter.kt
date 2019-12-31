package app.eyal.teamexplorer.ui.main

import android.view.View
import androidx.lifecycle.viewModelScope
import app.eyal.teamexplorer.repository.SlackService
import app.eyal.teamexplorer.repository.UserList
import app.eyal.teamexplorer.wiring.component
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UserRowState(
    val imageUrl: String,
    val name: String
)

data class MainState(
    val loadingIndicatorVisibility: Int = View.GONE,
    val errorMessageVisibility: Int = View.GONE,
    val userListVisibility: Int = View.GONE,
    val errorMessage: String? = null,
    val userList: List<UserRowState>? = null
) : MvRxState {

    companion object {
        val Loading = MainState(loadingIndicatorVisibility = View.VISIBLE)

        fun Error(errorMessage: String) = MainState(
            errorMessageVisibility = View.VISIBLE,
            errorMessage = errorMessage
        )

        fun Data(userList: List<UserRowState>) = MainState(
            userListVisibility = View.VISIBLE,
            userList = userList
        )
    }
}

class Presenter(initialState: MainState, slackService: SlackService) :
    BaseMvRxViewModel<MainState>(initialState = initialState, debugMode = true) {

    class Factory(
        private val slackService: SlackService
    ) {
        fun create(initialState: MainState): Presenter = Presenter(
            initialState = initialState,
            slackService = slackService
        )
    }

    companion object : MvRxViewModelFactory<Presenter, MainState> {
        override fun create(viewModelContext: ViewModelContext, state: MainState): Presenter? {
            val component = if (viewModelContext is FragmentViewModelContext) {
                // If the ViewModel has a fragment scope it will be a FragmentViewModelContext, and you can access the fragment.
                viewModelContext.fragment.component
            } else {
                // The activity owner will be available for both fragment and activity view models.
                viewModelContext.activity.component
            }
            return component.presenterFactory.create(state)
        }

        override fun initialState(viewModelContext: ViewModelContext): MainState? =
            MainState.Loading
    }

    // private val userId: LiveData<String> = MutableLiveData()
    init {
        // liveData {
        //     emit(database.loadUserById(id))
        // }

        viewModelScope.launch {
            val list = withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                    slackService.userList(SlackService.TOKEN)
                }
            setState {
                MainState.Data(list.toRowState())
            }
        }
    }
}

private fun UserList.toRowState() = members.map { UserRowState(
    imageUrl = it.profile.image_512,
    name = it.profile.display_name)
}

// class UserListAdapter : Typed2EpoxyController() {
//     fun bindSearchData(data: SearchData) {
//         header.setCity(data.city)
//         guidebookRow.showIf(data.hasGuideBook())
//         for (neighborhood in data.neighborhoods) {
//             addModel(NeighborhoodCarouselModel(neighborhood))
//         }
//         loader.showIf(data.hasMoreToLoad())
//         notifyModelsChanged()
//     }
// }

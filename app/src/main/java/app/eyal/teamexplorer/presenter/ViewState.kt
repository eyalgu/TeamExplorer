package app.eyal.teamexplorer.presenter

import android.view.View
import com.airbnb.mvrx.MvRxState

data class UserRowState(
    val imageUrl: String,
    val name: String,
    val id: String
)

data class MainViewState(
    val loadingIndicatorVisibility: Int = View.GONE,
    val errorMessageVisibility: Int = View.GONE,
    val userListVisibility: Int = View.GONE,
    val errorMessage: String? = null,
    val userList: List<UserRowState>? = null
) : MvRxState {

    companion object {
        val Loading =
            MainViewState(loadingIndicatorVisibility = View.VISIBLE)

        fun Error(errorMessage: String) = MainViewState(
            errorMessageVisibility = View.VISIBLE,
            errorMessage = errorMessage
        )

        fun Data(userList: List<UserRowState>) =
            MainViewState(
                userListVisibility = View.VISIBLE,
                userList = userList
            )
    }
}

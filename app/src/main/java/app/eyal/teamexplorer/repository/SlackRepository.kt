package app.eyal.teamexplorer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import androidx.paging.toLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface SlackRepository {

    sealed class FetchResult<T: Any> {
        class Loading<T: Any> : FetchResult<T>()
        data class Data<T: Any>(val value: T) : FetchResult<T>()
        data class Error<T: Any>(val errorMessage: String) : FetchResult<T>()
    }

    fun <T> userList(map: (FeedEntity) -> T): Flow<FetchResult<PagedList<T>>>
    fun user(id: String): Flow<FetchResult<UserEntity>>
}

@FlowPreview
@ExperimentalCoroutinesApi
class RealSlackRepository(
    private val service: SlackService,
    private val dao: SlackDao
) : SlackRepository {
    override fun <T> userList(map: (FeedEntity) -> T): Flow<SlackRepository.FetchResult<PagedList<T>>> {

        val networkFlow = flow<SlackRepository.FetchResult<List<T>>> {
            emit(SlackRepository.FetchResult.Loading())
            with(service.userList()) {
                if (ok) {
                    with(members!!.map { it.toEntity() }) {
                        dao.insertUserList(this)
                        // Not emitting here because the value will be sent up via disk flow.
                    }
                } else {
                    dao.clearUserList()
                    emit(SlackRepository.FetchResult.Error(error!!))
                }
            }
        }

        val diskFlow = flow<SlackRepository.FetchResult<PagedList<T>>> {
            emit(SlackRepository.FetchResult.Loading())
            val f =
                dao.loadUserList()
                    .map(map)
                    .toLiveData(pageSize = 5)
                    .asFlow()
                    .map { SlackRepository.FetchResult.Data(it) }
            emitAll(f)
        }

        return diskFlow.combine(networkFlow) { diskResult, networkResult ->
            if (networkResult is SlackRepository.FetchResult.Error) {
                SlackRepository.FetchResult.Error(networkResult.errorMessage)
            } else {
                diskResult
            }
        }.distinctUntilChanged()
    }

    override fun user(id: String): Flow<SlackRepository.FetchResult<UserEntity>> {
        val networkFlow = flow<SlackRepository.FetchResult<UserEntity>> {
            emit(SlackRepository.FetchResult.Loading())
            with(service.userList()) {
                if (ok) {
                    with(members!!.map { it.toEntity() }) {
                        dao.insertUserList(this)
                        // Not emitting here because the value will be sent up via disk flow.
                    }
                } else {
                    dao.clearUserList()
                    emit(SlackRepository.FetchResult.Error(error!!))
                }
            }
        }.catch {
            when (it) {
                is HttpException -> {
                    it.response()?.errorBody()?.let {
                        val list = Moshi.Builder().build()
                            .adapter(UserList::class.java)
                            .fromJson(JsonReader.of(it.source()))

                        emit(SlackRepository.FetchResult.Error(list!!.error!!))
                    }

                }
            }
        }.flowOn(Dispatchers.IO)

        val diskFlow = flow<SlackRepository.FetchResult<UserEntity>> {
            emit(SlackRepository.FetchResult.Loading())
            emitAll(dao.loadUser(id).filterNotNull().map { SlackRepository.FetchResult.Data(it) } )
        }

        return diskFlow.combine(networkFlow) { diskResult, networkResult ->
            if (networkResult is SlackRepository.FetchResult.Error) {
                networkResult
            } else {
                diskResult
            }
        }.distinctUntilChanged()
    }
}

@ExperimentalCoroutinesApi
fun <T> LiveData<T>.asFlow(): Flow<T> = flow {
    val channel = Channel<T>(Channel.CONFLATED)
    val observer = Observer<T> {
        channel.offer(it)
    }
    withContext(Dispatchers.Main.immediate) {
        observeForever(observer)
    }
    try {
        for (value in channel) {
            emit(value)
        }
    } finally {
        GlobalScope.launch(Dispatchers.Main.immediate) {
            removeObserver(observer)
        }
    }
}
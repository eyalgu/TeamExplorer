package app.eyal.teamexplorer.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface SlackRepository {

    sealed class FetchResult<T: Any> {
        class Loading<T: Any> : FetchResult<T>()
        data class Data<T: Any>(val value: T) : FetchResult<T>()
        data class Error<T: Any>(val errorMessage: String) : FetchResult<T>()
    }

    fun userList(): Flow<FetchResult<List<FeedEntity>>>
    fun user(id: String): Flow<FetchResult<UserEntity>>
}

@FlowPreview
@ExperimentalCoroutinesApi
class RealSlackRepository(
    private val service: SlackService,
    private val dao: SlackDao
) : SlackRepository {
    override fun userList(): Flow<SlackRepository.FetchResult<List<FeedEntity>>> {

        val networkFlow = flow<SlackRepository.FetchResult<List<FeedEntity>>> {
            emit(SlackRepository.FetchResult.Loading())
            with(service.userList(SlackService.TOKEN)) {
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

        val diskFlow = flow<SlackRepository.FetchResult<List<FeedEntity>>> {
            emit(SlackRepository.FetchResult.Loading())
            emitAll(dao.loadUserList(20).map { SlackRepository.FetchResult.Data(it) } )
        }

        return diskFlow.combine(networkFlow) { diskResult, networkResult ->
            if (networkResult is SlackRepository.FetchResult.Error) {
                networkResult
            } else {
                diskResult
            }
        }.distinctUntilChanged()
    }

    override fun user(id: String): Flow<SlackRepository.FetchResult<UserEntity>> {
        val networkFlow = flow<SlackRepository.FetchResult<UserEntity>> {
            emit(SlackRepository.FetchResult.Loading())
            with(service.userList(SlackService.TOKEN)) {
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
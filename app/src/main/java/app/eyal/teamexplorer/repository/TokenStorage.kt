package app.eyal.teamexplorer.repository

import android.annotation.SuppressLint
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

interface TokenStorage {
    suspend fun setToken(token: String)
    val token: Flow<String?>
}

@ExperimentalCoroutinesApi
@FlowPreview
class RealTokenStorage(
    private val sharedPreferences: SharedPreferences
): TokenStorage {

    @SuppressLint("ApplySharedPref")
    override suspend fun setToken(token: String) {
        sharedPreferences.edit().putString("token", token).commit()
    }

    override val token: Flow<String?> = flow {
        val channel = Channel<String?>(Channel.CONFLATED)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "token") {
                channel.offer(sharedPreferences.getString("token", null))

            }
        }
        val subFlow = flow {
            emit(sharedPreferences.getString("token", null))
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
            for (value in channel) {
                emit(value)
            }
        }.onCompletion {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }.distinctUntilChanged()
        emitAll(subFlow)
    }
}
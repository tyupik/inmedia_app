package ru.netology.inmedia.auth

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.inmedia.api.PostApiService
import ru.netology.inmedia.api.token
import ru.netology.inmedia.dto.PushToken
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"

    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val id = prefs.getLong(idKey, 0)
        val token = prefs.token

        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun postApiService(): PostApiService
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                getPostApiService(context).save(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getPostApiService(context: Context): PostApiService {
        val hiltEntryPint = EntryPointAccessors.fromApplication(
            context,
            AppAuthEntryPoint::class.java
        )
        return hiltEntryPint.postApiService()
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String?) {
        _authStateFlow.value = AuthState(id, token)
        prefs.token = token
        with(prefs.edit()) {
            putLong(idKey, id)
            apply()
        }
        sendPushToken(token)
    }

    @Synchronized
    fun setAuthTyupik(login: String, pass: String, token: String) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                getPostApiService(context).sendAuth(login, pass)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            sendPushToken(token)
        }
    }



    @Synchronized
    fun setRegistration(login: String, pass: String, name: String) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                getPostApiService(context).registration(login, pass, name)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
        sendPushToken()
    }
}

data class AuthState(val id: Long = 0, val token: String? = null)
data class RecipientInfo(val recipientId: String, val content: String)
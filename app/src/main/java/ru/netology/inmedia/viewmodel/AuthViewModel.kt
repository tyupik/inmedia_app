package ru.netology.inmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.auth.AuthState
import javax.inject.Inject

@HiltViewModel
class   AuthViewModel @Inject constructor(
    private val auth: AppAuth
) : ViewModel() {
    val data: LiveData<AuthState> = auth
        .authStateFlow
        .asLiveData(Dispatchers.Default)

    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L
}
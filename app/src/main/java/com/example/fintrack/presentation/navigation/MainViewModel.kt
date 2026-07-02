package com.example.fintrack.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _destination = MutableStateFlow<String?>(null)
    val destination: StateFlow<String?> = _destination.asStateFlow()

    private val _sessionExpired = MutableSharedFlow<Unit>()
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    init {
        checkAuthState()
        observeSessionExpired()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val token = tokenManager.getToken().firstOrNull()
            _destination.value = if (token != null) {
                FinTrackScreens.HomeScreen.route
            } else {
                FinTrackScreens.SignInScreen.route
            }
        }
    }

    private fun observeSessionExpired() {
        viewModelScope.launch {
            tokenManager.sessionExpired.collect {
                tokenManager.clearAll()
                _sessionExpired.emit(Unit)
            }
        }
    }
}

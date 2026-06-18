package com.example.fintrack.presentation.screens.sign_in

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.BuildConfig
import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.usecase.GoogleSignInUseCase
import com.example.fintrack.domain.usecase.LoginUseCase
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow(SignInActionState())
    val actionState: StateFlow<SignInActionState> = _actionState.asStateFlow()

    private val googleSignInRequest = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .build()
        )
        .build()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            validationErrors = _uiState.value.validationErrors.copy(emailError = null)
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            validationErrors = _uiState.value.validationErrors.copy(passwordError = null)
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun login() {
        viewModelScope.launch {
            _actionState.value = SignInActionState(isLoading = true)
            _uiState.value = _uiState.value.copy(validationErrors = SignInValidationErrors())
            val result = loginUseCase(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            )
            when (result) {
                is Resource.Success -> _actionState.value = SignInActionState(isSuccess = true)
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        validationErrors = mapErrorToValidation(result.message, result.fieldErrors)
                    )
                    _actionState.value = SignInActionState(isLoading = false)
                }
                is Resource.Loading -> _actionState.value = SignInActionState(isLoading = true)
            }
        }
    }

    fun launchGoogleSignIn(context: Context) {
        viewModelScope.launch {
            _actionState.value = SignInActionState(isLoading = true)
            try {
                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context, googleSignInRequest)
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    when (val authResult = googleSignInUseCase(idToken)) {
                        is Resource.Success -> _actionState.value = SignInActionState(isSuccess = true)
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                validationErrors = SignInValidationErrors(emailError = authResult.message)
                            )
                            _actionState.value = SignInActionState(isLoading = false)
                        }
                        is Resource.Loading -> Unit
                    }
                } else {
                    _actionState.value = SignInActionState(isLoading = false)
                }
            } catch (e: GetCredentialException) {
                _uiState.value = _uiState.value.copy(
                    validationErrors = SignInValidationErrors(emailError = "Google ile giriş iptal edildi.")
                )
                _actionState.value = SignInActionState(isLoading = false)
            }
        }
    }

    private fun mapErrorToValidation(
        message: String?,
        fieldErrors: Map<String, String>?
    ): SignInValidationErrors {
        return if (!fieldErrors.isNullOrEmpty()) {
            SignInValidationErrors(
                emailError = fieldErrors["email"],
                passwordError = fieldErrors["password"]
            )
        } else {
            SignInValidationErrors(emailError = message ?: "Giriş başarısız.")
        }
    }
}

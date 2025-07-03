package com.example.artsyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsyapp.model.User
import com.example.artsyapp.repository.UserRepository
import com.example.artsyapp.network.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<UIState<User>>(UIState.Idle)
    val loginState: StateFlow<UIState<User>> = _loginState

    /**
     * Used when user enters credentials and hits "Log In"
     */
    fun signIn(email: String, password: String) = viewModelScope.launch {
        _loginState.value = UIState.Loading
        Log.d("LoginViewModel", "Attempting login for: $email")

        val result = repo.signIn(email, password)

        result.fold(
            onSuccess = {
                Log.d("LoginViewModel", "Login successful for: $email")
                _loginState.value = UIState.Success(it.user)
            },
            onFailure = {
                Log.e("LoginViewModel", "Login failed: ${it.message}")
                _loginState.value = UIState.Error("Username or Password is incorrect")
            }
        )
    }

    /**
     * Used when app launches to check if cookie-based login is valid
     */
    fun validateSession() = viewModelScope.launch {
        // Only change state to loading if it's not already in a success state
        if (_loginState.value !is UIState.Success) {
            _loginState.value = UIState.Loading
        }

        Log.d("LoginViewModel", "Validating user session")
        val user = repo.getCurrentUser()

        if (user != null) {
            Log.d("LoginViewModel", "Session valid, user: ${user.fullName}")
            _loginState.value = UIState.Success(user)
        } else {
            Log.d("LoginViewModel", "Session invalid or expired")
            TokenManager.clearAll()
            _loginState.value = UIState.Idle
        }
    }

    /**
     * Used on logout
     */
    fun signOut() = viewModelScope.launch {
        Log.d("LoginViewModel", "Signing out user")
        repo.signOut()
        TokenManager.clearAll()
        _loginState.value = UIState.Idle
    }

    fun refreshUserProfile() {
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Refreshing user profile")
                // Get the current user from your repository
                val user = repo.getCurrentUser()

                // Only update if user is not null
                if (user != null) {
                    _loginState.value = UIState.Success(user)
                    Log.d("LoginViewModel", "User profile refreshed successfully: ${user.fullName}")
                } else {
                    Log.w("LoginViewModel", "Failed to refresh user profile - user is null")
                }
            } catch (e: Exception) {
                // Handle any errors
                Log.e("LoginViewModel", "Error refreshing user profile: ${e.message}", e)
            }
        }
    }

    // Try to validate session with retries in case of network issues
    fun validateSessionWithRetry(maxRetries: Int = 3) = viewModelScope.launch {
        var retries = 0
        var success = false

        while (retries < maxRetries && !success) {
            try {
                Log.d("LoginViewModel", "Validating session (attempt ${retries + 1})")
                val user = repo.getCurrentUser()

                if (user != null) {
                    Log.d("LoginViewModel", "Session valid on attempt ${retries + 1}")
                    _loginState.value = UIState.Success(user)
                    success = true
                } else {
                    Log.d("LoginViewModel", "Session validation failed on attempt ${retries + 1}")
                    retries++
                    delay(500) // Wait before retrying
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error validating session: ${e.message}")
                retries++
                delay(500) // Wait before retrying
            }
        }

        if (!success) {
            Log.d("LoginViewModel", "Session validation failed after $maxRetries attempts")
            TokenManager.clearAll()
            _loginState.value = UIState.Idle
        }
    }
}
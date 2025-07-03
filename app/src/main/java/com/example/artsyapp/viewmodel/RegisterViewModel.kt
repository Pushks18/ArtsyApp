package com.example.artsyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsyapp.model.User
import com.example.artsyapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {

    private val _registerState = MutableStateFlow<UIState<User>>(UIState.Idle)
    val registerState: StateFlow<UIState<User>> = _registerState

    fun signUp(
        fullName: String,
        email: String,
        password: String,
        avatarUrl: String?
    ) {
        viewModelScope.launch {
            _registerState.value = UIState.Loading

            // Log the registration attempt for debugging
            Log.d("RegisterViewModel", "Attempting to register user: $email")

            val result = repo.signUp(fullName, email, password, avatarUrl)

            result.fold(
                onSuccess = { response ->
                    // Log success
                    Log.d("RegisterViewModel", "Registration successful for: $email")
                    _registerState.value = UIState.Success(response.user)
                },
                onFailure = { error ->
                    // Log failure
                    Log.e("RegisterViewModel", "Registration failed: ${error.message}")
                    _registerState.value = UIState.Error(error.message ?: "Registration failed")
                }
            )
        }
    }

    fun resetState() {
        _registerState.value = UIState.Idle
    }
}
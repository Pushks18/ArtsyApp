package com.example.artsyapp.viewmodel

/**
 * Represents loading / success / error states for any UI data.
 */
sealed class UIState<out T> {
    object Idle : UIState<Nothing>()
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val message: String) : UIState<Nothing>()
}


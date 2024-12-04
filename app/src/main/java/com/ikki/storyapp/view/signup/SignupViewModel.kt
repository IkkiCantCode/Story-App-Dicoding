package com.ikki.storyapp.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikki.storyapp.data.AuthRepository
import com.ikki.storyapp.response.RegisterResponse
import kotlinx.coroutines.launch

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    fun register(name: String, email: String, password: String) {
        Log.d("SignupViewModel", "Starting registration for $email")
        viewModelScope.launch {
            try {
                val response = authRepository.register(name, email, password)
                Log.d("SignupViewModel", "Registration successful for $email: ${response.message}")
                _registerResult.value = Result.success(response)
            } catch (e: Exception) {
                when (e) {
                    is IllegalArgumentException -> {
                        Log.e("SignupViewModel", "Validation failed: ${e.message}")
                        _registerResult.value = Result.failure(Exception(e.message))
                    }
                    else -> {
                        Log.e("SignupViewModel", "Registration failed for $email: ${e.message}")
                        _registerResult.value = Result.failure(e)
                    }
                }
            }
        }
    }
}
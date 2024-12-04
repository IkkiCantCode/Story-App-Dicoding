package com.ikki.storyapp.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikki.storyapp.data.AuthRepository
import com.ikki.storyapp.data.UserRepository
import com.ikki.storyapp.data.pref.UserModel
import com.ikki.storyapp.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                Log.d("LoginViewModel", "Login response: $response")
                _loginResult.value = Result.success(response)

                val loginResult = response.loginResult
                if (loginResult != null) {
                    userRepository.saveSession(UserModel(
                        email = email,
                        token = loginResult.token ?: "",
                    ))
                } else {
                    _loginResult.value = Result.failure(Exception("Login result is null"))
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error: ${e.message}")
                _loginResult.value = Result.failure(e)
            }
        }
    }
}
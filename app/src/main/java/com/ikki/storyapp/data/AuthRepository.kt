package com.ikki.storyapp.data

import com.ikki.storyapp.response.LoginResponse
import com.ikki.storyapp.response.RegisterResponse

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): LoginResponse {
        val loginRequest = mapOf("email" to email, "password" to password)
        return try {
            apiService.login(loginRequest)
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        val registerRequest = mapOf("name" to name, "email" to email, "password" to password)
        return try {
            apiService.register(registerRequest)
        } catch (e: Exception) {
            throw Exception("Registration failed: ${e.message}")
        }
    }
}
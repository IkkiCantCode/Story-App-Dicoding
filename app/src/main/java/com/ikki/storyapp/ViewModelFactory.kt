package com.ikki.storyapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ikki.storyapp.data.AuthRepository
import com.ikki.storyapp.data.StoryRepository
import com.ikki.storyapp.data.UserRepository
import com.ikki.storyapp.di.Injection
import com.ikki.storyapp.view.login.LoginViewModel
import com.ikki.storyapp.view.main.MainViewModel
import com.ikki.storyapp.view.signup.SignupViewModel
import com.ikki.storyapp.view.ui.StoryViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val authRepository = Injection.provideAuthRepository(context)
                val userRepository = Injection.provideUserRepository(context)
                val storyRepository = Injection.provideRepository(context)
                INSTANCE ?: ViewModelFactory(authRepository, userRepository, storyRepository).also {
                    INSTANCE = it
                }
            }
        }
    }
}
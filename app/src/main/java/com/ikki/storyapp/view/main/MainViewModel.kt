package com.ikki.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikki.storyapp.data.StoryRepository
import com.ikki.storyapp.data.pref.UserModel
import com.ikki.storyapp.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _session = MutableLiveData<UserModel>()
    val session: LiveData<UserModel> = _session

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getSession() {
        viewModelScope.launch {
            try {
                _session.value = repository.getSession()
            } catch (e: Exception) {
                _error.value = "Failed to retrieve session: ${e.message}"
            }
        }
    }

    fun getStories(token: String) {
        viewModelScope.launch {
            try {
                val result = repository.getStories(token)
                _stories.value = result
            } catch (e: Exception) {
                _error.value = "Failed to retrieve stories: ${e.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                _error.value = "Failed to log out: ${e.message}"
            }
        }
    }
}
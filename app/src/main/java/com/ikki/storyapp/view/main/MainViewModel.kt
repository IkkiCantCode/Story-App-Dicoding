package com.ikki.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ikki.storyapp.data.StoryEntity
import com.ikki.storyapp.data.StoryRepository
import com.ikki.storyapp.data.pref.UserModel
import com.ikki.storyapp.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _session = MutableLiveData<UserModel>()
    val session: LiveData<UserModel> = _session

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // LiveData for triggering refresh
    private val _refreshTrigger = MutableLiveData<Unit>()
    val refreshTrigger: LiveData<Unit> = _refreshTrigger

    // Use LiveData to expose stories as a Flow
    val stories: LiveData<Flow<PagingData<ListStoryItem>>> = liveData {
        // Emit a Flow based on the session state
        _session.value?.let { user ->
            if (user.isLogin) {
                // Emit the PagingData Flow based on the user token
                emit(repository.getPagedStories(user.token).cachedIn(viewModelScope))
            } else {
                // Emit an empty PagingData Flow if not logged in
                emit(flowOf(PagingData.empty<ListStoryItem>()))
            }
        }
    }

    fun getSession() {
        viewModelScope.launch {
            try {
                _session.value = repository.getSession()
            } catch (e: Exception) {
                _error.value = "Failed to retrieve session: ${e.message}"
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

    // Method to trigger data refresh
    fun triggerRefresh() {
        _refreshTrigger.value = Unit
    }
}
package com.example.questmapgps.ui.screens.main_content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questmapgps.data.FirebaseRepository
import com.example.questmapgps.ui.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RankingViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserData>>(emptyList())
    val users: StateFlow<List<UserData>> = _users

    private var lastPoints: Int? = null
    private var isLoading = false

    fun loadNextPage(pageSize: Int = 10) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                val nextPage = repository.getUsersPage(pageSize, lastPoints)
                val validPage = nextPage.filter { it.username.isNotBlank() }

                if (validPage.isNotEmpty()) {
                    _users.value = _users.value + validPage
                    lastPoints = validPage.last().points
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    init {
        loadNextPage()
    }
}
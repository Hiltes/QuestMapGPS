package com.example.questmapgps.ui.screens.main_content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questmapgps.data.FirebaseRepository
import com.example.questmapgps.ui.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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
            val nextPage = repository.getUsersPage(pageSize, lastPoints)

            if (nextPage.isNotEmpty()) {
                _users.value = _users.value + nextPage
                lastPoints = nextPage.last().points
            }

            isLoading = false
        }
    }

    init {
        loadNextPage()  // startujemy od pierwszej strony
    }
}

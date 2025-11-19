package com.example.questmapgps.ui.screens.main_content


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questmapgps.data.FirebaseRepository
import com.example.questmapgps.ui.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GameViewModel : ViewModel() {
    private val repository = FirebaseRepository()


    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()


    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()


    init {
        viewModelScope.launch {
            val userId = repository.signInAnonymously()
            _userId.value = userId
            repository.getUserDataFlow(userId).collect { dataFromDb ->
                _userData.value = dataFromDb
            }
        }
    }


    fun saveUsername(username: String) {
        viewModelScope.launch {
            _userId.value?.let { uid ->
                if (_userData.value == null) {
                    repository.initializeUser(uid, username)
                }
            }
        }
    }


    // Oznacz punkt jako rozwiązany kodem
    fun markCodeAsSolved(pointName: String) {
        viewModelScope.launch {
            _userId.value?.let { uid ->
                repository.addCodeSolvedPoint(uid, pointName)
            }
        }
    }


    // Oznacz punkt jako odwiedzony
    fun markPointAsVisited(pointName: String) {
        viewModelScope.launch {
            _userId.value?.let { uid ->
                repository.addVisitedPoint(uid, pointName)
            }
        }
    }

    fun checkCodeSolvedPoints() {
        viewModelScope.launch {
            _userId.value?.let { uid ->
                val data = repository.getUserData(uid)?.codesSolvedPoints

            }
        }

    }
}
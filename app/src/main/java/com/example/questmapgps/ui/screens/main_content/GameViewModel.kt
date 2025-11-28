package com.example.questmapgps.ui.screens.main_content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.questmapgps.data.FirebaseRepository
import com.example.questmapgps.ui.data.UserData
import kotlinx.coroutines.Job
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

    private var dataCollectionJob: Job? = null

    init {
        viewModelScope.launch {
            val uid = repository.signInAnonymously()
            _userId.value = uid
            startObservingUserData(uid)
        }
    }
    private fun startObservingUserData(uid: String) {
        dataCollectionJob?.cancel()

        dataCollectionJob = viewModelScope.launch {
            repository.getUserDataFlow(uid).collect { dataFromDb ->
                _userData.value = dataFromDb
            }
        }
    }

    fun saveUsername(username: String) {
        viewModelScope.launch {
            val currentUid = _userId.value ?: repository.signInAnonymously()
            if (_userId.value == null) {
                _userId.value = currentUid
            }


            if (_userData.value == null) {
                repository.initializeUser(currentUid, username)
                startObservingUserData(currentUid)
            }
        }
    }

    fun logout() {
        dataCollectionJob?.cancel()
        repository.signOut()
        _userData.value = null
        _userId.value = null
    }

    fun markCodeAsSolved(pointName: String) {
        viewModelScope.launch {
            val uid = _userId.value ?: return@launch
            repository.addCodeSolvedPoint(uid, pointName)
        }
    }

    fun markPointAsVisited(pointName: String) {
        viewModelScope.launch {
            val uid = _userId.value ?: return@launch
            repository.addVisitedPoint(uid, pointName)
        }
    }

    fun checkCodeSolvedPoints() {}
}
package com.example.questmapgps.data


import com.example.questmapgps.ui.data.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirebaseRepository {


    private val database = Firebase.database("https://questmap-d58ca-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("users")
    private val auth = Firebase.auth


    suspend fun signInAnonymously(): String {
        if (auth.currentUser == null) {
            auth.signInAnonymously().await()
        }
        return auth.currentUser!!.uid
    }

    suspend fun getUserData(userId: String): UserData? {
        val snapshot = database.child(userId).get().await()
        return snapshot.getValue<UserData>()
    }

    fun getUserDataFlow(userId: String): Flow<UserData?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue<UserData>())
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.child(userId).addValueEventListener(listener)
        awaitClose { database.child(userId).removeEventListener(listener) }
    }


    suspend fun initializeUser(userId: String, username: String) {
        val initialData = UserData(username = username)
        database.child(userId).setValue(initialData).await()
    }


    // Dodaje punkt jako "odwiedzony" i sprawdza warunek zaliczenia
    suspend fun addVisitedPoint(userId: String, pointName: String) {
        val userRef = database.child(userId)
        val snapshot = userRef.get().await()
        val currentUserData = snapshot.getValue<UserData>() ?: return


        if (!currentUserData.visitedPoints.contains(pointName)) {
            val updates = mutableMapOf<String, Any>()


            updates["visitedPoints"] = currentUserData.visitedPoints + pointName
            updates["points"] = currentUserData.points + 20


            if (currentUserData.codesSolvedPoints.contains(pointName)) {
                updates["solvedPoints"] = currentUserData.solvedPoints + pointName
            }


            userRef.updateChildren(updates).await()
        }
    }


    // Dodaje punkt jako "z rozwiązanym kodem" i sprawdza warunek zaliczenia
    suspend fun addCodeSolvedPoint(userId: String, pointName: String) {
        val userRef = database.child(userId)
        val snapshot = userRef.get().await()
        val currentUserData = snapshot.getValue<UserData>() ?: return


        if (!currentUserData.codesSolvedPoints.contains(pointName)) {
            val updates = mutableMapOf<String, Any>()


            updates["codesSolvedPoints"] = currentUserData.codesSolvedPoints + pointName
            updates["points"] = currentUserData.points + 10


            if (currentUserData.visitedPoints.contains(pointName)) {
                updates["solvedPoints"] = currentUserData.solvedPoints + pointName
            }


            userRef.updateChildren(updates).await()
        }
    }
    fun getTopUsersFlow(limit: Int = 10): Flow<List<UserData>> = callbackFlow {
        val query = database.orderByChild("points").limitToLast(limit)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children
                    .mapNotNull { it.getValue<UserData>() }
                    .sortedByDescending { it.points }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun getUsersPage(
        pageSize: Int,
        lastPoints: Int? = null
    ): List<UserData> {

        val query = if (lastPoints == null) {
            database.orderByChild("points").limitToLast(pageSize)
        } else {
            database.orderByChild("points")
                .endBefore(lastPoints.toDouble())
                .limitToLast(pageSize)
        }

        val snapshot = query.get().await()

        return snapshot.children
            .mapNotNull { it.getValue<UserData>() }
            .sortedByDescending { it.points }
    }


}
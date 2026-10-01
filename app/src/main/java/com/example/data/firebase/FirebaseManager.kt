package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    private const val TAG = "FirebaseManager"

    fun isFirebaseAvailable(context: Context): Boolean {
        return try {
            val apps = FirebaseApp.getApps(context)
            apps.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    fun getAuth(context: Context): FirebaseAuth? {
        return if (isFirebaseAvailable(context)) {
            try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseAuth not ready: ${e.message}")
                null
            }
        } else null
    }

    fun getFirestore(context: Context): FirebaseFirestore? {
        return if (isFirebaseAvailable(context)) {
            try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseFirestore not ready: ${e.message}")
                null
            }
        } else null
    }

    suspend fun syncDocToFirestore(
        context: Context,
        collectionName: String,
        docId: String,
        data: Map<String, Any?>
    ): Boolean {
        val firestore = getFirestore(context) ?: return false
        return try {
            firestore.collection(collectionName)
                .document(docId)
                .set(data.filterValues { it != null }, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed syncing to Firestore collection $collectionName/$docId: ${e.message}")
            false
        }
    }

    suspend fun deleteDocFromFirestore(
        context: Context,
        collectionName: String,
        docId: String
    ): Boolean {
        val firestore = getFirestore(context) ?: return false
        return try {
            firestore.collection(collectionName).document(docId).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed deleting from Firestore: ${e.message}")
            false
        }
    }
}

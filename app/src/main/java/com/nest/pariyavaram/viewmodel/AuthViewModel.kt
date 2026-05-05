package com.nest.pariyavaram.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val role: String = "user",
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val isLoggedIn get() = currentUser != null
    val isAdmin get() = role == "admin"
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState =
        MutableStateFlow(
            AuthUiState(
                currentUser = auth.currentUser,
                isLoading = true
            )
        )

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUserRole()
    }

    // APP OPEN -> CHECK USER ROLE
    private fun loadCurrentUserRole() {

        val user = auth.currentUser

        if (user == null) {
            _uiState.value = AuthUiState()
            return
        }

        viewModelScope.launch {
            try {
                val doc = db.collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                val role = doc.getString("role") ?: "user"

                _uiState.value = AuthUiState(
                    currentUser = user,
                    role = role,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    currentUser = user,
                    role = "user",
                    isLoading = false
                )
            }
        }
    }

    // SIGNUP
    fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ) {

        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter all fields")
            }
            return
        }

        if (password != confirmPassword) {
            _uiState.update {
                it.copy(errorMessage = "Passwords do not match")
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }

                val result =
                    auth.createUserWithEmailAndPassword(
                        email,
                        password
                    ).await()

                val uid = result.user?.uid ?: ""

                val userData = hashMapOf(
                    "email" to email,
                    "role" to "user"
                )

                db.collection("users")
                    .document(uid)
                    .set(userData)
                    .await()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = result.user,
                        role = "user",
                        successMessage = "Account Created"
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Signup Failed"
                    )
                }
            }
        }
    }

    // LOGIN
    fun login(email: String, password: String) {

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }

                val result =
                    auth.signInWithEmailAndPassword(
                        email,
                        password
                    ).await()

                val uid = result.user?.uid ?: ""

                val doc = db.collection("users")
                    .document(uid)
                    .get()
                    .await()

                val role =
                    doc.getString("role") ?: "user"

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = result.user,
                        role = role
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Login Failed"
                    )
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = AuthUiState()
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun sendPasswordReset(email: String) {

        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()

                _uiState.update {
                    it.copy(
                        successMessage = "Reset Email Sent"
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}
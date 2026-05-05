//package com.nest.pariyavaram.admin.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.firebase.Timestamp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ListenerRegistration
//import com.google.firebase.firestore.Query
//import com.google.firebase.firestore.FieldValue
//import com.nest.pariyavaram.data.model.ReportStatus
//import com.nest.pariyavaram.admin.model.UserModel
//import com.nest.pariyavaram.data.model.WasteReport
//import com.paryavarankavalu.admin.model.*
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//class AdminViewModel : ViewModel() {
//
//    private val auth = FirebaseAuth.getInstance()
//    private val db = FirebaseFirestore.getInstance()
//
//    private val usersCollection = db.collection("users")
//    private val reportsCollection = db.collection("waste_reports")
//
//    private val _uiState = MutableStateFlow(AdminUiState())
//    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()
//
//    private val _events = Channel<AdminEvent>(Channel.BUFFERED)
//    val events = _events.receiveAsFlow()
//
//    private var reportsListener: ListenerRegistration? = null
//    private var usersListener: ListenerRegistration? = null
//
//    init {
//        checkAdminAuth()
//    }
//
//    fun checkAdminAuth() {
//        val currentUser = auth.currentUser
//
//        if (currentUser == null) {
//            _uiState.update {
//                it.copy(authState = AdminAuthState.Unauthenticated)
//            }
//            return
//        }
//
//        viewModelScope.launch {
//            try {
////                val doc = usersCollection.document(currentUser.uid).get().await()
//
//                if (doc.exists()) {
//
//                    val role = doc.getString("role") ?: "user"
//
//                    if (role == "admin") {
//
//                        val admin =
//                            UserModel.fromMap(doc.data ?: emptyMap())
//
//                        _uiState.update {
//                            it.copy(
//                                authState =
//                                    AdminAuthState.Authenticated(admin)
//                            )
//                        }
//
//                        loadDashboardStats()
//                        startReportsListener()
//                        startUsersListener()
//
//                    } else {
//                        _uiState.update {
//                            it.copy(authState = AdminAuthState.Unauthorized)
//                        }
//                    }
//
//                } else {
//                    _uiState.update {
//                        it.copy(authState = AdminAuthState.Unauthorized)
//                    }
//                }
//
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(authState = AdminAuthState.Unauthorized)
//                }
//            }
//        }
//    }
//
//    fun logout() {
//        reportsListener?.remove()
//        usersListener?.remove()
//
//        auth.signOut()
//
//        _uiState.value = AdminUiState(
//            authState = AdminAuthState.Unauthenticated
//        )
//    }
//
//    fun loadDashboardStats() {
//        viewModelScope.launch {
//
//            _uiState.update {
//                it.copy(dashboardState = DashboardState.Loading)
//            }
//
//            try {
//
//                val usersSnap = usersCollection.get().await()
//                val reportsSnap = reportsCollection.get().await()
//
//                val reports = reportsSnap.documents.mapNotNull { doc ->
//                    doc.data?.let { WasteReport.fromMap(it, doc.id) }
//                }
//
//                val stats = DashboardStats(
//                    totalUsers = usersSnap.size(),
//                    totalReports = reports.size,
//                    pendingReports = reports.count {
//                        it.status == ReportStatus.PENDING.name
//                    },
//                    inProgressReports = reports.count {
//                        it.status == ReportStatus.IN_PROGRESS.name
//                    },
//                    cleanedReports = reports.count {
//                        it.status == ReportStatus.CLEANED.name
//                    },
//                    rejectedReports = reports.count {
//                        it.status == ReportStatus.REJECTED.name
//                    }
//                )
//
//                _uiState.update {
//                    it.copy(
//                        dashboardState =
//                            DashboardState.Success(stats)
//                    )
//                }
//
//            } catch (e: Exception) {
//
//                _uiState.update {
//                    it.copy(
//                        dashboardState =
//                            DashboardState.Error(
//                                e.message ?: "Error"
//                            )
//                    )
//                }
//            }
//        }
//    }
//
//    private fun startReportsListener() {
//
//        reportsListener?.remove()
//
//        reportsListener =
//            reportsCollection
//                .orderBy("createdAt", Query.Direction.DESCENDING)
//                .addSnapshotListener { snapshot, _ ->
//
//                    val reports =
//                        snapshot?.documents?.mapNotNull { doc ->
//                            doc.data?.let {
//                                WasteReport.fromMap(it, doc.id)
//                            }
//                        } ?: emptyList()
//
//                    _uiState.update {
//                        it.copy(
//                            reportsState =
//                                ReportsState.Success(
//                                    reports = reports,
//                                    filteredReports = reports,
//                                    selectedFilter = null,
//                                    searchQuery = ""
//                                )
//                        )
//                    }
//                }
//    }
//
//    private fun startUsersListener() {
//
//        usersListener?.remove()
//
//        usersListener =
//            usersCollection
//                .whereEqualTo("role", "user")
//                .addSnapshotListener { snapshot, _ ->
//
//                    val users =
//                        snapshot?.documents?.mapNotNull { doc ->
//                            doc.data?.let {
//                                UserModel.fromMap(it)
//                            }
//                        } ?: emptyList()
//
//                    _uiState.update {
//                        it.copy(
//                            usersState =
//                                UsersState.Success(
//                                    users = users,
//                                    filteredUsers = users,
//                                    searchQuery = ""
//                                )
//                        )
//                    }
//                }
//    }
//
//    fun markReportAsCleaned(reportId: String) {
//
//        viewModelScope.launch {
//
//            reportsCollection.document(reportId).update(
//                mapOf(
//                    "status" to ReportStatus.CLEANED.name,
//                    "updatedAt" to Timestamp.now(),
//                    "resolvedAt" to Timestamp.now()
//                )
//            ).await()
//
//            loadDashboardStats()
//        }
//    }
//
//    fun markReportInProgress(reportId: String) {
//
//        viewModelScope.launch {
//
//            reportsCollection.document(reportId).update(
//                mapOf(
//                    "status" to ReportStatus.IN_PROGRESS.name,
//                    "updatedAt" to Timestamp.now()
//                )
//            ).await()
//
//            loadDashboardStats()
//        }
//    }
//
//    fun deleteReport(reportId: String) {
//
//        viewModelScope.launch {
//
//            reportsCollection.document(reportId)
//                .delete()
//                .await()
//
//            loadDashboardStats()
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//
//        reportsListener?.remove()
//        usersListener?.remove()
//    }
//}
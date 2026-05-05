package com.nest.pariyavaram.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nest.pariyavaram.data.model.WasteReport
import com.nest.pariyavaram.data.model.WasteType
import com.nest.pariyavaram.local.ReportDatabase
import com.nest.pariyavaram.local.ReportStatus
import com.nest.pariyavaram.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// ─────────────────────────────────────────────────────────────────────────────
//  UI STATE
// ─────────────────────────────────────────────────────────────────────────────

data class ReportUiState(
    val reports       : List<WasteReport> = emptyList(),
    val selectedReport: WasteReport?      = null,
    val submitting    : Boolean           = false,
    val toastMessage  : String?           = null
) {
    /** Derived – count reports whose status string equals "Pending" */
    val pendingCount: Int
        get() = reports.count { it.status == ReportStatus.PENDING.value }

    /** Derived – count reports whose status string equals "Cleaned" */
    val cleanedCount: Int
        get() = reports.count { it.status == ReportStatus.CLEANED.value }

    /** Derived – sum of all ecoKarmaPoints */
    val totalKarma: Int
        get() = reports.sumOf { it.ecoKarmaPoints }
}

// ─────────────────────────────────────────────────────────────────────────────
//  FORM STATE  (new-report screen)
// ─────────────────────────────────────────────────────────────────────────────

data class ReportFormState(
    val lat           : Double    = 0.0,
    val lng           : Double    = 0.0,
    val locationReady : Boolean   = false,
    val photoUri      : Uri?      = null,
    val isProcessing  : Boolean   = false,
    val compressedPath: String?   = null,
    val wasteType     : WasteType = WasteType.PLASTIC,
    val description   : String    = ""
)

// ─────────────────────────────────────────────────────────────────────────────
//  VIEW MODEL
// ─────────────────────────────────────────────────────────────────────────────

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReportRepository

    private val _uiState   = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ReportFormState())
    val formState: StateFlow<ReportFormState> = _formState.asStateFlow()

    init {
        val dao = ReportDatabase.getDatabase(application).reportDao()
        repository = ReportRepository(dao)

        viewModelScope.launch {
            repository.allReports.collect { list ->
                _uiState.update { it.copy(reports = list) }
            }
        }
    }

    // ── Selection ────────────────────────────────────────────────────────────

    fun selectReport(report: WasteReport?) {
        _uiState.update { it.copy(selectedReport = report) }
    }

    // ── Form helpers ─────────────────────────────────────────────────────────

    fun updateLocation(lat: Double, lng: Double) {
        _formState.update { it.copy(lat = lat, lng = lng, locationReady = true) }
    }

    fun updateWasteType(type: WasteType) {
        _formState.update { it.copy(wasteType = type) }
    }

    fun updateDescription(desc: String) {
        _formState.update { it.copy(description = desc) }
    }

    fun updatePhoto(uri: Uri) {
        _formState.update { it.copy(photoUri = uri, isProcessing = true, compressedPath = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val compressed = compressImage(uri)
            _formState.update { it.copy(isProcessing = false, compressedPath = compressed) }
        }
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    fun submitReport() {
        val form = _formState.value
        if (!form.locationReady || form.compressedPath == null) return

        _uiState.update { it.copy(submitting = true) }

        viewModelScope.launch {
            val report = WasteReport(
                latitude       = form.lat,
                longitude      = form.lng,
                photoPath      = form.compressedPath,
                wasteType      = form.wasteType.name,       // store enum name as String
                description    = form.description,
                status         = ReportStatus.PENDING.value, // "Pending"
                ecoKarmaPoints = 10,
                aiCategory     = null
            )
            repository.addReport(report)

            _uiState.update { it.copy(submitting = false, toastMessage = "✅ Report submitted! +10 Eco-Karma") }
            _formState.value = ReportFormState()            // reset form
        }
    }

    // ── Mark cleaned ──────────────────────────────────────────────────────────

    fun markAsCleaned(report: WasteReport) {
        viewModelScope.launch {
            repository.markAsCleaned(report.id)             // passes Int id ✅
            _uiState.update { it.copy(toastMessage = "🎉 Marked as cleaned! Great work!") }
        }
    }

    // ── Toast ─────────────────────────────────────────────────────────────────

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // ── Image compression helper ──────────────────────────────────────────────

    private suspend fun compressImage(uri: Uri): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val context   = getApplication<Application>()
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val bitmap    = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Scale down if needed
            val maxDim  = 1024
            val scaled  = if (bitmap.width > maxDim || bitmap.height > maxDim) {
                android.graphics.Bitmap.createScaledBitmap(
                    bitmap,
                    if (bitmap.width > bitmap.height) maxDim else (maxDim * bitmap.width / bitmap.height),
                    if (bitmap.height >= bitmap.width) maxDim else (maxDim * bitmap.height / bitmap.width),
                    true
                )
            } else bitmap

            val outFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            outFile.outputStream().use { out ->
                scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 75, out)
            }
            outFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
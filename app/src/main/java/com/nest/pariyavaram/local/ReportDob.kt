package com.nest.pariyavaram.local

import androidx.room.*
import com.nest.pariyavaram.data.model.WasteReport
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Query("SELECT * FROM waste_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<WasteReport>>

    @Query("SELECT * FROM waste_reports WHERE status = :status ORDER BY timestamp DESC")
    fun getReportsByStatus(status: String): Flow<List<WasteReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: WasteReport): Long

    @Update
    suspend fun updateReport(report: WasteReport)

    @Delete
    suspend fun deleteReport(report: WasteReport)

    @Query("UPDATE waste_reports SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE waste_reports SET aiCategory = :category WHERE id = :id")
    suspend fun updateAiCategory(id: Int, category: String)

    @Query("SELECT * FROM waste_reports WHERE id = :id")
    suspend fun getById(id: Int): WasteReport?
}
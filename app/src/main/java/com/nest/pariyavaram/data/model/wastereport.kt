package com.nest.pariyavaram.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "waste_reports")
data class WasteReport(
    @PrimaryKey(autoGenerate = true)
    val id          : Int     = 0,
    val latitude    : Double  = 0.0,
    val longitude   : Double  = 0.0,
    val photoPath   : String? = null,
    val wasteType   : String  = WasteType.PLASTIC.name,   // stored as String in DB
    val description : String  = "",
    val status      : String  = "Pending",                 // stored as String in DB
    val timestamp   : Long    = System.currentTimeMillis(),
    val ecoKarmaPoints: Int   = 10,                        // ✅ correct field name used in screens
    val aiCategory  : String? = null
) {
    /** Convenience accessor – converts stored String → WasteType enum */
    @get:Ignore
    val wasteTypeEnum: WasteType
        get() = WasteType.fromName(wasteType)
}
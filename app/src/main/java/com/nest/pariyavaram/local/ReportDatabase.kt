package com.nest.pariyavaram.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nest.pariyavaram.data.model.WasteReport

@Database(
    entities  = [WasteReport::class],
    version   = 2,
    exportSchema = false
)
abstract class ReportDatabase : RoomDatabase() {

    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: ReportDatabase? = null

        fun getDatabase(context: Context): ReportDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ReportDatabase::class.java,
                    "waste_report_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
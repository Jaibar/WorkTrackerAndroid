package com.example.worktracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "work_entries")
data class WorkEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val hours: Double = 0.0,
    val latitude: Double? = null,
    val longitude: Double? = null
)
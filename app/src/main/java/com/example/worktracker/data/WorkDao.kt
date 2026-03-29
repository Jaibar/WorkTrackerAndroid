package com.example.worktracker.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface WorkDao {
    @Insert suspend fun insert(entry: WorkEntry)
    @Update suspend fun update(entry: WorkEntry)
    @Query("SELECT * FROM work_entries WHERE endTime IS NULL LIMIT 1")
    suspend fun getCurrent(): WorkEntry?
    @Query("SELECT * FROM work_entries WHERE startTime >= :since")
    suspend fun getSince(since: LocalDateTime): List<WorkEntry>
}
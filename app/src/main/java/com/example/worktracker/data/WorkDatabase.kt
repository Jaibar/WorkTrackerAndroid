package com.example.worktracker.data
import android.content.Context
import androidx.room.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(entities = [WorkEntry::class], version = 1)
@TypeConverters(TimeConverter::class)
abstract class WorkDatabase : RoomDatabase() {
    abstract fun dao(): WorkDao
    companion object {
        fun build(context: Context) =
            Room.databaseBuilder(context, WorkDatabase::class.java, "workdb").build()
    }
}

class TimeConverter {
    private val f = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    @TypeConverter fun toDate(v: String?) = v?.let { LocalDateTime.parse(it, f) }
    @TypeConverter fun toString(v: LocalDateTime?) = v?.format(f)
}
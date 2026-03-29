package com.example.worktracker.viewmodel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worktracker.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.*

class WorkViewModel(context: Context) : ViewModel() {
    private val dao = WorkDatabase.build(context).dao()
    private val _isWorking = MutableStateFlow(false)
    val isWorking = _isWorking.asStateFlow()
    private val _startTime = MutableStateFlow<LocalDateTime?>(null)
    val startTime = _startTime.asStateFlow()

    fun loadCurrent() {
        viewModelScope.launch {
            val cur = dao.getCurrent()
            _isWorking.value = cur != null
            _startTime.value = cur?.startTime
        }
    }

    fun startShift(lat: Double?, lon: Double?) {
        viewModelScope.launch {
            val start = LocalDateTime.now()
            dao.insert(WorkEntry(startTime = start, latitude = lat, longitude = lon))
            _isWorking.value = true
            _startTime.value = start
        }
    }

    fun endShift(lat: Double?, lon: Double?) {
        viewModelScope.launch {
            val cur = dao.getCurrent() ?: return@launch
            val end = LocalDateTime.now()
            val diff = Duration.between(cur.startTime, end).toMinutes() / 60.0
            dao.update(cur.copy(endTime = end, hours = diff, latitude = lat, longitude = lon))
            _isWorking.value = false
            _startTime.value = null
        }
    }

    suspend fun monthlyHours(startDay: Int): Double {
        val today = LocalDate.now()
        val start = if (today.dayOfMonth >= startDay)
            today.withDayOfMonth(startDay).atStartOfDay()
        else today.minusMonths(1).withDayOfMonth(startDay).atStartOfDay()
        return dao.getSince(start).sumOf { it.hours }
    }
}
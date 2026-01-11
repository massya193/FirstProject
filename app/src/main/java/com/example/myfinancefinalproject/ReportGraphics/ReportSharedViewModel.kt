package com.example.myfinancefinalproject.ReportGraphics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.asLiveData
class ReportSharedViewModel : ViewModel() {

    private val _range = MutableStateFlow(DateRange(0L, System.currentTimeMillis()))
    val range = _range.asStateFlow()
    val rangeLiveData = range.asLiveData()
    fun setRange(from: Long, to: Long) {
        _range.value = DateRange(from, to)
    }
}

data class DateRange(val from: Long, val to: Long)

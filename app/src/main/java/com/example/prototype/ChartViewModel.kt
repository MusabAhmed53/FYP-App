package com.example.prototype

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChartViewModel : ViewModel() {
    val studentMarksLiveData: MutableLiveData<List<StudentMarks>> by lazy {
        MutableLiveData<List<StudentMarks>>()
    }
}
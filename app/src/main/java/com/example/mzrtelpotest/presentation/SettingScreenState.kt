package com.gicproject.onepagequeuekioskapp.presentation

import com.example.mzrtelpotest.domain.model.Branch
import com.example.mzrtelpotest.domain.model.Counter
import com.example.mzrtelpotest.domain.model.Department


data class SettingScreenState(
    val isLoading: Boolean = false,
    val isLoadingPagination: Boolean = false,
    val branches: List<Branch> = emptyList(),
    val counters: List<Counter> = emptyList(),
    val department: List<Department> = emptyList(),
    val error: String = ""
)

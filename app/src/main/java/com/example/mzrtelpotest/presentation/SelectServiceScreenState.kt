package com.example.mzrtelpotest.presentation

import com.example.mzrtelpotest.domain.model.SelectService


data class SelectServiceScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val success: String = "",
    val services: List<SelectService> = emptyList(),
    val isAppointment: String = "",
)

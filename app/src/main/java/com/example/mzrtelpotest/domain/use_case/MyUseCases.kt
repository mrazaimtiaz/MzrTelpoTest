package com.example.mzrtelpotest.domain.use_case

import com.example.mzrtelpotest.domain.use_case.*

data class MyUseCases(
    val getBranches: GetBranches,
    val getCounters: GetCounters,
    val getDepartments: GetDepartments,
    val getSelectDepartments: GetSelectDepartments,
    val getDoctors: GetDoctors,
    val getPrintTicket: GetPrintTicket,
    val getSelectServices: GetSelectServices,
    val getBookTicket: GetBookTicket,
    val getTicket: GetTicket,
)

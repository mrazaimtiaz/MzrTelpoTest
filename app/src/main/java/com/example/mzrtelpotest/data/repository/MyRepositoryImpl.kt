package com.example.mzrtelpotest.data.repository


import com.example.mzrtelpotest.data.remote.dto.*
import com.example.mzrtelpotest.data.remote.MyApi
import com.example.mzrtelpotest.domain.repository.MyRepository
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val api: MyApi
) : MyRepository {


    override suspend fun getBookTicket(
        serviceID: String,
        isHandicap: Boolean,
        isVip: Boolean,
        languageID: String,
        appointmentCode: String,
        isaapt: Boolean,
        refid: String,
        DoctorServiceID: String,
    ): List<BookTicketDto>? {
        return api.getBookTicket(serviceID, isHandicap,isVip,languageID,appointmentCode,isaapt,refid,DoctorServiceID)
    }

    override suspend fun getTicket(
        QueueID: Int,language: Int
    ): List<GetTicketDto>? {
        return api.getTicket(QueueID, language)

    }


    override suspend fun getSelectServices(
        branchId: String,
        deptId: String,
    ): List<SelectServiceDto>? {
        return api.getSelectServices(branchId, deptId)
    }

    override suspend fun getSelectDepartments(
        branchId: String,
        deptParentId: String,
    ): List<SelectDepartmentDto>? {
        return api.getSelectDepartments(branchId, deptParentId)
    }


    override suspend fun getDepartments(): List<DepartmentDto>? {
        return api.getDepartments()
    }

    override suspend fun getCounters(branchId: String): List<CounterDto>? {
        return api.getCounters(branchId)
    }

    override suspend fun getBranches(): List<BranchDto>? {
        return api.getBranches()
    }


}
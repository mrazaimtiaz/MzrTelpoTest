package com.example.mzrtelpotest.data.remote.dto

import com.example.mzrtelpotest.domain.model.Department
import com.google.gson.annotations.SerializedName
data class DepartmentDto(
    @SerializedName("ParentID") var ParentID: Int? = null,
    @SerializedName("Department_Name_EN") var DepartmentNameEn: String? = null,
    @SerializedName("Department_Name_AR") var DepartmentNameAr: String? = null,
){
    fun toDepartment(): Department {
        return Department(
            ParentID = ParentID,
            DepartmentNameEn = DepartmentNameEn,
            DepartmentNameAr = DepartmentNameAr,
        )
    }
}






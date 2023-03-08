package com.example.mzrtelpotest.data.remote.dto

import com.example.mzrtelpotest.domain.model.SelectDepartment
import com.google.gson.annotations.SerializedName
data class SelectDepartmentDto(
    @SerializedName("Department_PK_ID"        ) var DepartmentPKID         : Int?    = null,
    @SerializedName("Department_Name_EN"      ) var DepartmentNameEN       : String? = null,
    @SerializedName("Department_Name_AR"      ) var DepartmentNameAR       : String? = null,
    @SerializedName("Department_Location"     ) var DepartmentLocation     : String? = null,
    @SerializedName("Department_Descriptions" ) var DepartmentDescriptions : String? = null,
    @SerializedName("Department_Parent_ID"    ) var DepartmentParentID     : Int?    = null,
    @SerializedName("Department_ClinicID"     ) var DepartmentClinicID     : Int?    = null
){
    fun toSelectDepartment(): SelectDepartment {
        return SelectDepartment(
            DepartmentPKID = DepartmentPKID,
            DepartmentNameEN = DepartmentNameEN,
            DepartmentNameAR = DepartmentNameAR,
            DepartmentLocation = DepartmentLocation,
            DepartmentDescriptions = DepartmentDescriptions,
            DepartmentParentID = DepartmentParentID,
            DepartmentClinicID = DepartmentClinicID
        )
    }
}






package com.example.mzrtelpotest.data.remote.dto

import com.example.mzrtelpotest.domain.model.Branch
import com.google.gson.annotations.SerializedName

data class BranchDto(
    @SerializedName("Branch_PK_ID") var PKID: Int? = null,
    @SerializedName("Branch_Name_EN") var BranchNameEn: String? = null,
    @SerializedName("Branch_Name_AR") var BranchNameAr: String? = null,
    @SerializedName("Branch_IsActive") var IsBranchActive: Boolean? = null
){
    fun toBranch(): Branch {
        return Branch(
            PKID = PKID,
            BranchNameEn = BranchNameEn,
            BranchNameAr = BranchNameAr,
        )
    }
}

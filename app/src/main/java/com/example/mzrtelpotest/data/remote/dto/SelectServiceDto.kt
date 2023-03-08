package com.example.mzrtelpotest.data.remote.dto

import com.example.mzrtelpotest.domain.model.SelectService
import com.google.gson.annotations.SerializedName


data class SelectServiceDto (

    @SerializedName("Services_Name_EN"              ) var ServicesNameEN             : String?  = null,
    @SerializedName("Services_Name_AR"              ) var ServicesNameAR             : String?  = null,
    @SerializedName("Services_PK_ID"                ) var ServicesPKID               : Int?     = null,
    @SerializedName("Services_TicketDesigner_FK_ID" ) var ServicesTicketDesignerFKID : Int?     = null,
    @SerializedName("Services_Description"          ) var ServicesDescription        : String?  = null,
    @SerializedName("Services_Color"                ) var ServicesColor              : String?  = null,
    @SerializedName("Services_AllowSMS"             ) var ServicesAllowSMS           : Boolean? = null,
    @SerializedName("BranchServices_Service_FK_ID"  ) var BranchServicesServiceFKID  : Int?     = null,
    @SerializedName("BranchServices_Branch_FK_ID"   ) var BranchServicesBranchFKID   : Int?     = null,
    @SerializedName("Services_Parent_ID"            ) var ServicesParentID           : Int?     = null,
    @SerializedName("Services_Logo"                 ) var ServicesLogo               : String?  = null,
    @SerializedName("Services_Description_Ar"       ) var ServicesDescriptionAr      : String?  = null,
    @SerializedName("Services_BackGroundImage"      ) var ServicesBackGroundImage    : String?  = null,
    @SerializedName("Services_FontName"             ) var ServicesFontName           : String?  = null,
    @SerializedName("Services_FontSize"             ) var ServicesFontSize           : String?  = null,
    @SerializedName("Services_FontStyle"            ) var ServicesFontStyle          : String?  = null,
    @SerializedName("Services_FontColor"            ) var ServicesFontColor          : String?  = null,
    @SerializedName("TicketDesigner_FileName"       ) var TicketDesignerFileName     : String?  = null

){
    fun toSelectService(): SelectService {
        return SelectService(
            ServicesNameEN = ServicesNameEN,
            ServicesNameAR = ServicesNameAR,
            ServicesPKID = ServicesPKID,
            ServicesTicketDesignerFKID = ServicesTicketDesignerFKID,
            ServicesDescription = ServicesDescription,
            ServicesColor = ServicesColor,
            ServicesAllowSMS = ServicesAllowSMS,
            BranchServicesServiceFKID = BranchServicesServiceFKID,
            BranchServicesBranchFKID = BranchServicesBranchFKID,
            ServicesParentID = ServicesParentID,
            ServicesLogo = ServicesLogo,
            ServicesDescriptionAr = ServicesDescriptionAr,
            ServicesBackGroundImage = ServicesBackGroundImage,
            ServicesFontName = ServicesFontName,
            ServicesFontSize = ServicesFontSize,
            ServicesFontStyle = ServicesFontStyle,
            ServicesFontColor = ServicesFontColor,
            TicketDesignerFileName = TicketDesignerFileName,
        )
    }
}






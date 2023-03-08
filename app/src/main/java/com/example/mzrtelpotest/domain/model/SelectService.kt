package com.example.mzrtelpotest.domain.model

data class SelectService(
    var ServicesNameEN: String? = null, var ServicesNameAR: String? = null,
    var ServicesPKID: Int? = null,
    var ServicesTicketDesignerFKID: Int? = null, var ServicesDescription: String? = null,
    var ServicesColor: String? = null,
    var ServicesAllowSMS: Boolean? = null,
    var BranchServicesServiceFKID: Int? = null,
    var BranchServicesBranchFKID: Int? = null,
    var ServicesParentID: Int? = null,
    var ServicesLogo: String? = null,
    var ServicesDescriptionAr: String? = null,
    var ServicesBackGroundImage: String? = null,
    var ServicesFontName: String? = null,
    var ServicesFontSize: String? = null,
    var ServicesFontStyle: String? = null,
    var ServicesFontColor: String? = null,
    var TicketDesignerFileName: String? = null
)


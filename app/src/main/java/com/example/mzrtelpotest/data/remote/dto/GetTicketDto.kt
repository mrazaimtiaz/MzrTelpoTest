package com.example.mzrtelpotest.data.remote.dto

import com.example.mzrtelpotest.domain.model.GetTicket
import com.google.gson.annotations.SerializedName
data class GetTicketDto(

    @SerializedName("Ticket"       ) var Ticket       : String?    = null,
){
    fun toGetTicket(): GetTicket {
        return GetTicket(
            Ticket = Ticket,
        )
    }
}






package com.example.mzrtelpotest.data.remote.dto

import com.example.mzrtelpotest.domain.model.Doctor
import com.google.gson.annotations.SerializedName

data class DoctorDto(
    @SerializedName("id") var id: String? = null,
    @SerializedName("nameEn") var nameEn: String? = null,
    @SerializedName("departmentEn") var departmentEn: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("date") var date: String? = null,
){
    fun toDoctor(): Doctor {
        return Doctor(
            id = id,
            nameEn = nameEn,
            departmentEn = departmentEn,
            price = price,
            time = time,
            date = date
        )
    }
}

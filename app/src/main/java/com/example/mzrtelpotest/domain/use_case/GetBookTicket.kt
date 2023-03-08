package com.example.mzrtelpotest.domain.use_case


import com.example.mzrtelpotest.common.Resource
import com.example.mzrtelpotest.domain.model.BookTicket
import com.example.mzrtelpotest.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetBookTicket @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(
        serviceID: String,
        isHandicap: Boolean,
        isVip: Boolean,
        languageID: String,
        appointmentCode: String,
        isaapt: Boolean,
        refid: String,
        DoctorServiceID: String,
    ): Flow<Resource<BookTicket>> = flow {
        try {
            emit(Resource.Loading())

           var bookTicket = repository.getBookTicket(serviceID,isHandicap,isVip,languageID,appointmentCode,isaapt,refid,DoctorServiceID)
          //  var bookTicket = listOf(BookTicketDto(1,"Dr Emad",),BookTicketDto(1,"Dr Emad",), )


            if (!bookTicket.isNullOrEmpty()) {
                emit(Resource.Success(bookTicket[0].toBookTicket()))
            } else {
                emit(Resource.Error("Empty GetBookTicket List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
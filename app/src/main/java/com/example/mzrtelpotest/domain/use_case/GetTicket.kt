package com.example.mzrtelpotest.domain.use_case


import com.example.mzrtelpotest.common.Resource
import com.example.mzrtelpotest.domain.model.GetTicket
import com.example.mzrtelpotest.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetTicket @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(
        queueId: Int,
        languageId: Int,
    ): Flow<Resource<GetTicket>> = flow {
        try {
            emit(Resource.Loading())

           var getTicket = repository.getTicket(queueId,languageId,)
          //  var getTicket = listOf(GetTicketDto(""),GetTicketDto("Dr Emad",),)
            if (!getTicket.isNullOrEmpty()) {
                emit(Resource.Success(getTicket[0].toGetTicket()))
            } else {
                emit(Resource.Error("Empty GetTicket List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
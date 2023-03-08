package com.example.mzrtelpotest.domain.use_case


import com.example.mzrtelpotest.common.Resource
import com.example.mzrtelpotest.domain.model.Branch
import com.example.mzrtelpotest.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetBranches @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(): Flow<Resource<List<Branch>>> = flow {
        try {
            emit(Resource.Loading())

           var branches = repository.getBranches()
         //   var branches = listOf(BranchDto(1,"Surgery Department"), BranchDto(2,"Medical"))
            if (!branches.isNullOrEmpty()) {
                emit(Resource.Success(branches.map {
                    it.toBranch()
                }))
            } else {
                emit(Resource.Error("Empty Branch List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
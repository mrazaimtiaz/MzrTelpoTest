package com.example.mzrtelpotest.domain.use_case



import com.example.mzrtelpotest.common.Resource
import com.example.mzrtelpotest.domain.model.Department
import com.example.mzrtelpotest.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetDepartments @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(
    ): Flow<Resource<List<Department>>> = flow {
        try {
            emit(Resource.Loading())

            var departments = repository.getDepartments()
          //  var departments = listOf(DepartmentDto(1,"Surgery Department"), DepartmentDto(2,"Medical"))

            if (!departments.isNullOrEmpty()) {
                emit(Resource.Success(departments.map {
                    it.toDepartment()
                }))
            } else {
                emit(Resource.Error("Empty Department List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
package com.example.mzrtelpotest.domain.use_case


import com.example.mzrtelpotest.common.Resource
import com.example.mzrtelpotest.domain.model.SelectService
import com.example.mzrtelpotest.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetSelectServices @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(
        branchId: String,
        deptId: String): Flow<Resource<List<SelectService>>> = flow {
        try {
            emit(Resource.Loading())
          /*  var selectServices =  if(deptId == "1"){
               listOf(SelectServiceDto(ServicesPKID=1,
                    ServicesNameEN = "Dr.Mohammad Motaz Varabi",
                   ServicesNameAR = "د.محمد معتز فارابي",
                   ServicesDescription = "Description is here onte",
                   ServicesDescriptionAr = "د.محمد معتز فارابيddsd "
                ),SelectServiceDto(ServicesPKID=1,
                    ServicesNameEN = "Medical Department Two"
                ),SelectServiceDto(ServicesPKID=1,
                   ServicesNameEN = "Dr.Mohammad Motaz Varabi",
                   ServicesNameAR = "د.محمد معتز فارابي",
                   ServicesDescription = "Description is here onte",
                   ServicesDescriptionAr = "د.محمد معتز فارابيddsd "
               ),SelectServiceDto(ServicesPKID=1,
                   ServicesNameEN = "Dr.Mohammad Motaz Varabi",
                   ServicesNameAR = "د.محمد معتز فارابي",
                   ServicesDescription = "Description is here onte",
                   ServicesDescriptionAr = "د.محمد معتز فارابيddsd "
               ),SelectServiceDto(ServicesPKID=1,
                   ServicesNameEN = "Dr.Mohammad Motaz Varabi",
                   ServicesNameAR = "د.محمد معتز فارابي",
                   ServicesDescription = "Description is here onte",
                   ServicesDescriptionAr = "د.محمد معتز فارابيddsd "
               ),SelectServiceDto(ServicesPKID=1,
                   ServicesNameEN = "Dr.Mohammad Motaz Varabi",
                   ServicesNameAR = "د.محمد معتز فارابي",
                   ServicesDescription = "Description is here onte",
                   ServicesDescriptionAr = "د.محمد معتز فارابيddsd "
               ),SelectServiceDto(ServicesPKID=1,
                   ServicesNameEN = "Dr.Mohammad Motaz Varabi",
                   ServicesNameAR = "د.محمد معتز فارابي",
                   ServicesDescription = "Description is here onte",
                   ServicesDescriptionAr = "د.محمد معتز فارابيddsd "
               ),SelectServiceDto(ServicesPKID=1,
                   ServicesNameEN = "Dr.Mohammad Motaz Varabi",
                   ServicesNameAR = "د.محمد معتز فارابي",
                   ServicesDescription = "Description is here onte",
                   ServicesDescriptionAr = "د.محمد معتز فارابيddsd "
               ),SelectServiceDto(ServicesPKID=1,
                   ServicesNameEN = "Dr.Mohammad Motaz Varabi",
                   ServicesNameAR = "د.محمد معتز فارابي",
                   ServicesDescription = "Description is here onte",
                   ServicesDescriptionAr = "د.محمد معتز فارابيddsd "
               ))
            }else{
                listOf(SelectServiceDto(ServicesPKID=1,
                    ServicesNameEN = "Surgery "
                ),SelectServiceDto(ServicesPKID=1,
                    ServicesNameEN = "Surgery Medical"
                ))
            }*/
           var selectServices = repository.getSelectServices(branchId,deptId)
            if (!selectServices.isNullOrEmpty()) {
                emit(Resource.Success(selectServices.map {
                    it.toSelectService()
                }))
            } else {
                emit(Resource.Error("Empty Service List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}
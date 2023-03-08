package com.example.mzrtelpotest.di

import android.content.Context
import com.example.mzrtelpotest.data.remote.MyApi
import com.example.mzrtelpotest.common.Constants
import com.example.mzrtelpotest.data.repository.DataStoreRepositoryImpl
import com.example.mzrtelpotest.data.repository.MyRepositoryImpl
import com.example.mzrtelpotest.domain.repository.DataStoreRepository
import com.example.mzrtelpotest.domain.repository.MyRepository
import com.example.mzrtelpotest.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext app: Context
    ): DataStoreRepository = DataStoreRepositoryImpl(app)

    @Provides
    @Singleton
    fun provideMyApi(): MyApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMyRepository(api: MyApi): MyRepository {
        return MyRepositoryImpl(api = api)
    }

    @Provides
    @Singleton
    fun provideSurveyUseCases(
        repository: MyRepository,
        dataStoreRepository: DataStoreRepository
    ): MyUseCases {
        return MyUseCases(
            getDepartments = GetDepartments(repository = repository),
            getBranches = GetBranches(repository = repository),
            getCounters = GetCounters(repository = repository),
            getDoctors = GetDoctors(repository = repository),
            getPrintTicket = GetPrintTicket(repository = repository),
            getSelectDepartments = GetSelectDepartments(repository = repository),
            getSelectServices = GetSelectServices(repository = repository),
            getTicket = GetTicket(repository = repository),
            getBookTicket = GetBookTicket(repository = repository),
        )
    }
}
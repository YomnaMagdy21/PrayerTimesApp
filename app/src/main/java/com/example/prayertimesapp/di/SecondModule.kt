package com.example.prayertimesapp.di

import com.example.prayertimesapp.database.PrayerTimesLocalDataSource
import com.example.prayertimesapp.database.PrayerTimesLocalDataSourceImp
import com.example.prayertimesapp.model.PrayerTimesRepository
import com.example.prayertimesapp.model.PrayerTimesRepositoryImp
import com.example.prayertimesapp.network.PrayerTimesRemoteDataSource
import com.example.prayertimesapp.network.PrayerTimesRemoteDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SecondModule {

    @Binds
    abstract fun provideRemoteSource(impl: PrayerTimesRemoteDataSourceImp): PrayerTimesRemoteDataSource

    @Binds
    abstract fun provideLocalSource(impl: PrayerTimesLocalDataSourceImp): PrayerTimesLocalDataSource

    @Binds
    abstract fun provideRepository(impl: PrayerTimesRepositoryImp): PrayerTimesRepository


}
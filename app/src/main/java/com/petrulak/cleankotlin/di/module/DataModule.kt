package com.petrulak.cleankotlin.di.module

import android.arch.persistence.room.Room
import android.content.Context
import com.petrulak.cleankotlin.data.mapper.WeatherEntityMapper
import com.petrulak.cleankotlin.data.repository.WeatherRepository
import com.petrulak.cleankotlin.data.source.LocalSource
import com.petrulak.cleankotlin.data.source.RemoteSource
import com.petrulak.cleankotlin.data.source.local.WeatherDatabase
import com.petrulak.cleankotlin.data.source.local.WeatherLocalSource
import com.petrulak.cleankotlin.data.source.remote.WeatherRemoteSource
import com.petrulak.cleankotlin.domain.executor.SchedulerProviderI
import com.petrulak.cleankotlin.domain.repository.WeatherRepositoryI
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    internal fun weatherRepository(repository: WeatherRepository): WeatherRepositoryI {
        return repository
    }

    @Provides
    @Singleton
    internal fun provideRemoteSource(retrofit: Retrofit): RemoteSource {
        return WeatherRemoteSource(retrofit)
    }

    @Provides
    @Singleton
    internal fun provideLocalSource(db: WeatherDatabase, mapper: WeatherEntityMapper, schedulerProvider: SchedulerProviderI): LocalSource {
        return WeatherLocalSource(db, mapper, schedulerProvider)
    }

    @Provides
    @Singleton
    internal fun provideRoomDb(context: Context): WeatherDatabase {
        return Room.databaseBuilder(context, WeatherDatabase::class.java, "weather-db").build()
    }
}

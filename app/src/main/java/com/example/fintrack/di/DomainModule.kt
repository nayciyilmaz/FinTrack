package com.example.fintrack.di

import com.example.fintrack.domain.repository.AuthRepository
import com.example.fintrack.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase =
        RegisterUseCase(authRepository)
}

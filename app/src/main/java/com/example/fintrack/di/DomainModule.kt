package com.example.fintrack.di

import com.example.fintrack.domain.repository.AuthRepository
import com.example.fintrack.domain.repository.TransactionRepository
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.GoogleSignInUseCase
import com.example.fintrack.domain.usecase.LoginUseCase
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

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase =
        LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGoogleSignInUseCase(authRepository: AuthRepository): GoogleSignInUseCase =
        GoogleSignInUseCase(authRepository)

    @Provides
    @Singleton
    fun provideAddTransactionUseCase(transactionRepository: TransactionRepository): AddTransactionUseCase =
        AddTransactionUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetTransactionsUseCase(transactionRepository: TransactionRepository): GetTransactionsUseCase =
        GetTransactionsUseCase(transactionRepository)
}

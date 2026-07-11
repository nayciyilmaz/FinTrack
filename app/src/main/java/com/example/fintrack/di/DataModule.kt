package com.example.fintrack.di

import com.example.fintrack.data.repository.AuthRepositoryImpl
import com.example.fintrack.data.repository.BudgetRepositoryImpl
import com.example.fintrack.data.repository.TransactionRepositoryImpl
import com.example.fintrack.domain.repository.AuthRepository
import com.example.fintrack.domain.repository.BudgetRepository
import com.example.fintrack.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository
}

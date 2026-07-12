package com.example.fintrack.di

import com.example.fintrack.domain.repository.AuthRepository
import com.example.fintrack.domain.repository.BudgetRepository
import com.example.fintrack.domain.repository.SavingsGoalRepository
import com.example.fintrack.domain.repository.TransactionRepository
import com.example.fintrack.domain.usecase.AddSavingsGoalUseCase
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.DeleteSavingsGoalUseCase
import com.example.fintrack.domain.usecase.DeleteTransactionUseCase
import com.example.fintrack.domain.usecase.GetBudgetsUseCase
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.GoogleSignInUseCase
import com.example.fintrack.domain.usecase.SaveBudgetsUseCase
import com.example.fintrack.domain.usecase.UpdateSavingsGoalUseCase
import com.example.fintrack.domain.usecase.UpdateTransactionUseCase
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

    @Provides
    @Singleton
    fun provideUpdateTransactionUseCase(transactionRepository: TransactionRepository): UpdateTransactionUseCase =
        UpdateTransactionUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideDeleteTransactionUseCase(transactionRepository: TransactionRepository): DeleteTransactionUseCase =
        DeleteTransactionUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetBudgetsUseCase(budgetRepository: BudgetRepository): GetBudgetsUseCase =
        GetBudgetsUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideSaveBudgetsUseCase(budgetRepository: BudgetRepository): SaveBudgetsUseCase =
        SaveBudgetsUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideGetSavingsGoalsUseCase(savingsGoalRepository: SavingsGoalRepository): GetSavingsGoalsUseCase =
        GetSavingsGoalsUseCase(savingsGoalRepository)

    @Provides
    @Singleton
    fun provideAddSavingsGoalUseCase(savingsGoalRepository: SavingsGoalRepository): AddSavingsGoalUseCase =
        AddSavingsGoalUseCase(savingsGoalRepository)

    @Provides
    @Singleton
    fun provideUpdateSavingsGoalUseCase(savingsGoalRepository: SavingsGoalRepository): UpdateSavingsGoalUseCase =
        UpdateSavingsGoalUseCase(savingsGoalRepository)

    @Provides
    @Singleton
    fun provideDeleteSavingsGoalUseCase(savingsGoalRepository: SavingsGoalRepository): DeleteSavingsGoalUseCase =
        DeleteSavingsGoalUseCase(savingsGoalRepository)
}

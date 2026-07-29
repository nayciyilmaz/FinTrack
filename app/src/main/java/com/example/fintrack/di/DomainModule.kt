package com.example.fintrack.di

import com.example.fintrack.domain.repository.AdvisorRepository
import com.example.fintrack.domain.repository.AuthRepository
import com.example.fintrack.domain.repository.BudgetRepository
import com.example.fintrack.domain.repository.RecurringItemRepository
import com.example.fintrack.domain.repository.SavingsGoalRepository
import com.example.fintrack.domain.repository.TransactionRepository
import com.example.fintrack.domain.repository.UserProfileRepository
import com.example.fintrack.domain.usecase.AddSavingsGoalUseCase
import com.example.fintrack.domain.usecase.AddTransactionUseCase
import com.example.fintrack.domain.usecase.AskAdvisorQuestionUseCase
import com.example.fintrack.domain.usecase.DeleteRecurringItemUseCase
import com.example.fintrack.domain.usecase.DeleteSavingsGoalUseCase
import com.example.fintrack.domain.usecase.DeleteTransactionUseCase
import com.example.fintrack.domain.usecase.GetAdvisorInsightsUseCase
import com.example.fintrack.domain.usecase.GetAdvisorSummaryUseCase
import com.example.fintrack.domain.usecase.GetBudgetsUseCase
import com.example.fintrack.domain.usecase.GetRecurringItemsUseCase
import com.example.fintrack.domain.usecase.GetReminderTransactionsUseCase
import com.example.fintrack.domain.usecase.GetSavingsGoalsUseCase
import com.example.fintrack.domain.usecase.GetTransactionsUseCase
import com.example.fintrack.domain.usecase.GetUserProfileUseCase
import com.example.fintrack.domain.usecase.GoogleSignInUseCase
import com.example.fintrack.domain.usecase.LogoutUseCase
import com.example.fintrack.domain.usecase.SaveBudgetsUseCase
import com.example.fintrack.domain.usecase.UpdateSavingsGoalUseCase
import com.example.fintrack.domain.usecase.UpdateTransactionUseCase
import com.example.fintrack.domain.usecase.UpdateUserEmailUseCase
import com.example.fintrack.domain.usecase.UpdateUserNameUseCase
import com.example.fintrack.domain.usecase.UpdateUserPasswordUseCase
import com.example.fintrack.domain.usecase.LoginUseCase
import com.example.fintrack.domain.usecase.RefreshAdvisorInsightUseCase
import com.example.fintrack.domain.usecase.RegisterUseCase
import com.example.fintrack.domain.usecase.ResetPasswordUseCase
import com.example.fintrack.domain.usecase.SendPasswordResetCodeUseCase
import com.example.fintrack.domain.usecase.UpdateRecurringItemUseCase
import com.example.fintrack.domain.usecase.VerifyPasswordResetCodeUseCase
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

    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(userProfileRepository: UserProfileRepository): GetUserProfileUseCase =
        GetUserProfileUseCase(userProfileRepository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(authRepository: AuthRepository): LogoutUseCase =
        LogoutUseCase(authRepository)

    @Provides
    @Singleton
    fun provideUpdateUserNameUseCase(userProfileRepository: UserProfileRepository): UpdateUserNameUseCase =
        UpdateUserNameUseCase(userProfileRepository)

    @Provides
    @Singleton
    fun provideUpdateUserEmailUseCase(userProfileRepository: UserProfileRepository): UpdateUserEmailUseCase =
        UpdateUserEmailUseCase(userProfileRepository)

    @Provides
    @Singleton
    fun provideUpdateUserPasswordUseCase(userProfileRepository: UserProfileRepository): UpdateUserPasswordUseCase =
        UpdateUserPasswordUseCase(userProfileRepository)

    @Provides
    @Singleton
    fun provideSendPasswordResetCodeUseCase(authRepository: AuthRepository): SendPasswordResetCodeUseCase =
        SendPasswordResetCodeUseCase(authRepository)

    @Provides
    @Singleton
    fun provideVerifyPasswordResetCodeUseCase(authRepository: AuthRepository): VerifyPasswordResetCodeUseCase =
        VerifyPasswordResetCodeUseCase(authRepository)

    @Provides
    @Singleton
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase =
        ResetPasswordUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGetAdvisorSummaryUseCase(advisorRepository: AdvisorRepository): GetAdvisorSummaryUseCase =
        GetAdvisorSummaryUseCase(advisorRepository)

    @Provides
    @Singleton
    fun provideGetAdvisorInsightsUseCase(advisorRepository: AdvisorRepository): GetAdvisorInsightsUseCase =
        GetAdvisorInsightsUseCase(advisorRepository)

    @Provides
    @Singleton
    fun provideAskAdvisorQuestionUseCase(advisorRepository: AdvisorRepository): AskAdvisorQuestionUseCase =
        AskAdvisorQuestionUseCase(advisorRepository)

    @Provides
    @Singleton
    fun provideRefreshAdvisorInsightUseCase(advisorRepository: AdvisorRepository): RefreshAdvisorInsightUseCase =
        RefreshAdvisorInsightUseCase(advisorRepository)

    @Provides
    @Singleton
    fun provideGetRecurringItemsUseCase(recurringItemRepository: RecurringItemRepository): GetRecurringItemsUseCase =
        GetRecurringItemsUseCase(recurringItemRepository)

    @Provides
    @Singleton
    fun provideUpdateRecurringItemUseCase(recurringItemRepository: RecurringItemRepository): UpdateRecurringItemUseCase =
        UpdateRecurringItemUseCase(recurringItemRepository)

    @Provides
    @Singleton
    fun provideDeleteRecurringItemUseCase(recurringItemRepository: RecurringItemRepository): DeleteRecurringItemUseCase =
        DeleteRecurringItemUseCase(recurringItemRepository)

    @Provides
    @Singleton
    fun provideGetReminderTransactionsUseCase(transactionRepository: TransactionRepository): GetReminderTransactionsUseCase =
        GetReminderTransactionsUseCase(transactionRepository)
}

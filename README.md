![Logo](https://github.com/nayciyilmaz/FinTrack/blob/main/FinTrack_Aydinlik.png?raw=true)

![CI](https://github.com/nayciyilmaz/FinTrack/actions/workflows/ci.yml/badge.svg)

👉 **[Click here to view Backend code](https://github.com/nayciyilmaz/FinTrackBackend)**

---

A modern Android personal finance application built with Jetpack Compose. Users can track income and expenses, set budget limits and savings goals, automate recurring transactions, and receive AI-generated financial advice.

## Features

- **AI-Powered Advice:** Personalized financial advice and a weighted health score based on real spending data
- **Transaction Tracking:** Log income and expense entries with category, amount, note, and date/time
- **Quick-Add Templates:** Frequently used transactions suggested from recent history
- **Budget Limits:** Category-based spending limits with usage tracking
- **Savings Goals:** Track goals with progress and an estimated completion date
- **Recurring Transactions:** Auto-created recurring income/expense entries per pay period
- **Payment Reminders:** Daily notifications for upcoming reminder entries
- **Spending Analysis:** Category distribution and adaptive spending trend charts
- **PDF Reports:** Export financial reports with selectable sections
- **Google Sign-In:** Sign in with Google via Credential Manager
- **Profile & Settings:** Update name/email/password, switch language, theme, font size, and currency
- **Multi-Language:** Turkish, English, and German
- **Material 3 Design:** Modern UI built entirely with Jetpack Compose

## Tech Stack

- **Jetpack Compose** - Declarative UI toolkit, no XML layouts
- **Material 3** - Latest Material Design components
- **Material Icons Extended** - Extended Material icon set for categories
- **Navigation Compose** - 14 screens with argument passing
- **Dagger Hilt** - Dependency injection
- **Retrofit + OkHttp** - Network layer with auth interceptor and token authenticator
- **kotlinx.serialization** - JSON serialization
- **DataStore Preferences** - Token and preference persistence
- **Android Keystore** - AES/GCM encryption for stored auth tokens
- **Credential Manager** - Google Sign-In integration
- **WorkManager** - Scheduled reminder notifications
- **Timber** - Structured logging

## Architecture

- **Clean Architecture** - Domain/Data/Presentation layer separation with dependency inversion
- **MVVM Pattern** - ViewModel with StateFlow for reactive UI
- **Use Cases** - Domain-layer interactors encapsulating business logic
- **Repository Pattern** - Auth, Transaction, Budget, SavingsGoal, Advisor, RecurringItem, and UserProfile repositories
- **Resource Sealed Class** - Unified success/error/loading across all layers
- **UI/Action State Split** - Separate form (UiState) and data (ActionState) flows per screen
- **Coroutines & Flow** - Asynchronous programming and reactive streams

## Security

- Auth tokens encrypted with Android Keystore (AES/GCM) before storage
- Automatic access-token refresh via OkHttp Authenticator
- Single-flight token refresh with session-expiry handling
- JWT access and refresh token flow with the backend

## Design & UI

- **Modern Compose UI:** Fully declarative, no XML
- **Light & Dark Theme:** Runtime theme switching
- **Adjustable Font Size:** Small, medium, and large scaling
- **Custom Wave Background:** Canvas-drawn wave shapes on auth screens
- **Reusable Components:** EditScaffold, EditDatePicker, EditTimePicker, TransactionRow, ProgressBar, etc.
- **Edge-to-Edge Display:** Theme-aware status bar appearance
- **Portrait Locked:** Consistent mobile experience

## Notification System

- 2 daily reminder notifications via WorkManager
- Morning (10:00) and afternoon (14:00) reminders
- Self-rescheduling OneTimeWorkRequest chain for exact-time delivery
- Android 13+ POST_NOTIFICATIONS permission handling
- Notification tap opens the reminders screen
- HiltWorker integration for dependency injection

## Testing

- **JUnit 4** - Unit testing framework
- **MockK** - Mocking library for repositories and ViewModels
- **Turbine** - Testing Kotlin Flow emissions
- **Kotlinx Coroutines Test** - Coroutine testing with test dispatchers
- **Espresso + Compose UI Test** - Instrumented UI tests with fake repositories
- **174 Unit Tests** covering repositories, viewmodels, and the network layer
- **14 Instrumented UI Tests** across authentication and transaction screens

## CI/CD

- **GitHub Actions** - Automated pipeline on every push and pull request
- **Unit Tests** - All unit tests run on each build
- **Instrumented Tests** - UI tests run on an Android emulator (API 30)
- **Jacoco** - Merged unit and instrumented coverage report uploaded as artifact
- **Debug APK** - Built on every run

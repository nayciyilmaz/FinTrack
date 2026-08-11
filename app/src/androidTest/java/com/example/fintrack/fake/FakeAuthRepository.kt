package com.example.fintrack.fake

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.User
import com.example.fintrack.domain.repository.AuthRepository

class FakeAuthRepository(
    private val loginResult: Resource<User>? = null,
    private val loginWithGoogleResult: Resource<User>? = null,
    private val registerResult: Resource<User>? = null
) : AuthRepository {

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        payday: Int,
        salary: Double
    ): Resource<User> {
        return registerResult ?: Resource.Success(
            User(id = 1L, firstName = firstName, lastName = lastName, email = email, payday = payday, token = "fake_token")
        )
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return loginResult ?: Resource.Success(
            User(id = 1L, firstName = "Test", lastName = "User", email = email, payday = 1, token = "fake_token")
        )
    }

    override suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return loginWithGoogleResult ?: Resource.Success(
            User(id = 1L, firstName = "Test", lastName = "User", email = "test@fintrack.com", payday = 1, token = "fake_token")
        )
    }

    override suspend fun logout(): Resource<Unit> = Resource.Success(Unit)

    override suspend fun sendPasswordResetCode(email: String): Resource<Unit> = Resource.Success(Unit)

    override suspend fun verifyPasswordResetCode(email: String, code: String): Resource<String> =
        Resource.Success("fake_reset_token")

    override suspend fun resetPassword(resetToken: String, newPassword: String): Resource<Unit> =
        Resource.Success(Unit)
}

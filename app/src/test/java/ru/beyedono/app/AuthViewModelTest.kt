// file: app/src/test/java/ru/beyedono/app/AuthViewModelTest.kt
package ru.beyedono.app

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ru.beyedono.app.data.repository.AuthRepository
import ru.beyedono.app.ui.screen.auth.LoginViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val authRepository = mockk<AuthRepository>()
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login sets success state`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.success(Unit)
        
        viewModel.onEmailChange("test@test.com")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        
        advanceUntilIdle()
        
        assert(viewModel.uiState.value.isSuccess)
    }
}
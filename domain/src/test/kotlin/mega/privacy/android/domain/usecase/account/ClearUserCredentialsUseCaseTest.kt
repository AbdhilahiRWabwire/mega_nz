package mega.privacy.android.domain.usecase.account

import kotlinx.coroutines.test.runTest
import mega.privacy.android.domain.entity.user.UserCredentials
import mega.privacy.android.domain.repository.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClearUserCredentialsUseCaseTest {
    private val repository = mock<AccountRepository>()
    private val underTest = ClearUserCredentialsUseCase(repository)

    @BeforeEach
    fun reset() {
        reset(repository)
    }

    @Test
    fun `test that invoke calls repository`() = runTest {
        underTest()
        verify(repository).clearCredentials()
    }
}
package mega.privacy.android.domain.usecase.login

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import mega.privacy.android.domain.exception.SessionNotRetrievedException
import mega.privacy.android.domain.repository.LoginRepository
import mega.privacy.android.domain.usecase.GetSession
import mega.privacy.android.domain.usecase.RootNodeExists
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultBackgroundFastLoginTest {

    private lateinit var underTest: BackgroundFastLogin

    private val loginRepository = mock<LoginRepository>()
    private val initialiseMegaChat = mock<InitialiseMegaChat>()
    private val getSession = mock<GetSession>()
    private val getRootNodeExists = mock<RootNodeExists>()
    private val session = "User session"

    @Before
    fun setUp() {
        underTest = DefaultBackgroundFastLogin(
            loginRepository = loginRepository,
            initialiseMegaChat = initialiseMegaChat,
            getSession = getSession,
            getRootNodeExists = getRootNodeExists,
            loginMutex = mock()
        )
    }

    @Test
    fun `test that login is failed if get session null`() = runTest {
        whenever(getSession()).thenReturn(null)
        assertThrows(SessionNotRetrievedException::class.java) {
            runBlocking { underTest() }
        }
    }

    @Test
    fun `test that login is success if get session differ null and root node exist`() = runTest {
        whenever(getSession()).thenReturn(session)
        whenever(getRootNodeExists()).thenReturn(true)
        val result = underTest()
        assertEquals(session, result)
    }

    @Test
    fun `test that login is failed if root node not exist and init mega chat throw exception`() =
        runTest {
            whenever(getSession()).thenReturn(session)
            whenever(getRootNodeExists()).thenReturn(false)
            whenever(initialiseMegaChat(session)).thenThrow(RuntimeException())
            assertThrows(RuntimeException::class.java) {
                runBlocking { underTest() }
            }
        }

    @Test
    fun `test that login is failed if root node not exist and fastLogin throw exception`() =
        runTest {
            whenever(getSession()).thenReturn(session)
            whenever(getRootNodeExists()).thenReturn(false)
            whenever(initialiseMegaChat(session)).thenReturn(Unit)
            whenever(loginRepository.fastLogin(session)).thenThrow(RuntimeException())
            assertThrows(RuntimeException::class.java) {
                runBlocking { underTest() }
            }
        }

    @Test
    fun `test that login is failed if root node not exist and fetchNodes throw exception`() =
        runTest {
            whenever(getSession()).thenReturn(session)
            whenever(getRootNodeExists()).thenReturn(false)
            whenever(initialiseMegaChat(session)).thenReturn(Unit)
            whenever(loginRepository.fastLogin(session)).thenReturn(Unit)
            whenever(loginRepository.fetchNodes()).thenThrow(RuntimeException())
            assertThrows(RuntimeException::class.java) {
                runBlocking { underTest() }
            }
        }

    @Test
    fun `test that login is success if root node not exist and init mega chat and fast login and fetch nodes success`() =
        runTest {
            whenever(getSession()).thenReturn(session)
            whenever(getRootNodeExists()).thenReturn(false)
            whenever(initialiseMegaChat(session)).thenReturn(Unit)
            whenever(loginRepository.fastLogin(session)).thenReturn(Unit)
            whenever(loginRepository.fetchNodes()).thenReturn(Unit)
            val result = underTest()
            assertEquals(session, result)
        }
}
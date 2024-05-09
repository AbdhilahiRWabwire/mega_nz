package mega.privacy.android.app.presentation.bottomsheet

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import de.palm.composestateevents.StateEventWithContentTriggered
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mega.privacy.android.app.presentation.transfers.starttransfer.StartDownloadViewModel
import mega.privacy.android.app.presentation.transfers.starttransfer.model.TransferTriggerEvent
import mega.privacy.android.core.test.extension.CoroutineMainDispatcherExtension
import mega.privacy.android.domain.entity.node.NodeId
import mega.privacy.android.domain.entity.node.TypedFileNode
import mega.privacy.android.domain.entity.node.TypedNode
import mega.privacy.android.domain.entity.node.chat.ChatDefaultFile
import mega.privacy.android.domain.entity.node.publiclink.PublicLinkFile
import mega.privacy.android.domain.usecase.GetNodeByIdUseCase
import mega.privacy.android.domain.usecase.featureflag.GetFeatureFlagValueUseCase
import mega.privacy.android.domain.usecase.filelink.GetPublicNodeFromSerializedDataUseCase
import mega.privacy.android.domain.usecase.folderlink.GetPublicChildNodeFromIdUseCase
import mega.privacy.android.domain.usecase.node.chat.GetChatFileUseCase
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@ExtendWith(CoroutineMainDispatcherExtension::class)
@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StartDownloadViewModelTest {

    private lateinit var underTest: StartDownloadViewModel

    private val getFeatureFlagValueUseCase = mock<GetFeatureFlagValueUseCase>()
    private val getChatFileUseCase = mock<GetChatFileUseCase>()
    private val getNodeByIdUseCase = mock<GetNodeByIdUseCase>()
    private val getPublicNodeFromSerializedDataUseCase =
        mock<GetPublicNodeFromSerializedDataUseCase>()
    val getPublicChildNodeFromIdUseCase = mock<GetPublicChildNodeFromIdUseCase>()

    @BeforeAll
    fun setup() {
        initViewModel()
    }

    @BeforeEach
    fun resetMocks() {
        reset(
            getFeatureFlagValueUseCase,
            getChatFileUseCase,
            getNodeByIdUseCase,
            getPublicNodeFromSerializedDataUseCase,
            getPublicChildNodeFromIdUseCase,
        )
    }

    private fun initViewModel() {
        underTest = StartDownloadViewModel(
            getFeatureFlagValueUseCase,
            getChatFileUseCase,
            getNodeByIdUseCase,
            getPublicNodeFromSerializedDataUseCase,
            getPublicChildNodeFromIdUseCase,
        )
    }

    @Test
    fun `test that onDownloadClicked launches the correct event`() = runTest {
        val node = mock<TypedFileNode>()
        underTest.onDownloadClicked(node)
        assertStartDownloadNode(node)
    }

    @Test
    fun `test that onSaveOfflineClicked launches the correct event`() = runTest {
        val node = mock<TypedFileNode>()
        underTest.onSaveOfflineClicked(node)
        assertStartDownloadForOffline(node)
    }

    @Test
    fun `test that onDownloadClicked with id launches the correct event`() = runTest {
        val nodeId = NodeId(1L)
        val node = mock<TypedFileNode>()
        whenever(getNodeByIdUseCase(nodeId)).thenReturn(node)
        underTest.onDownloadClicked(nodeId)
        assertStartDownloadNode(node)
    }

    @Test
    fun `test that onDownloadClicked with serialized data launches the correct event`() = runTest {
        val node = mock<PublicLinkFile>()
        val serializedData = "serialized data"
        whenever(getPublicNodeFromSerializedDataUseCase(serializedData)).thenReturn(node)
        underTest.onDownloadClicked(serializedData)
        assertStartDownloadNode(node)
    }

    @Test
    fun `test that onFolderLinkChildNodeDownloadClicked  launches the correct event`() = runTest {
        val node = mock<PublicLinkFile>()
        val id = NodeId(1L)
        whenever(getPublicChildNodeFromIdUseCase(id)).thenReturn(node)
        underTest.onFolderLinkChildNodeDownloadClicked(id)
        assertStartDownloadNode(node)
    }

    @Test
    fun `test that onSaveOfflineClicked with id launches the correct event`() = runTest {
        val nodeId = NodeId(1L)
        val node = mock<TypedFileNode>()
        whenever(getNodeByIdUseCase(nodeId)).thenReturn(node)
        underTest.onSaveOfflineClicked(nodeId)
        assertStartDownloadForOffline(node)
    }

    @Test
    fun `test that onDownloadClicked for chat file launches the correct event`() = runTest {
        val chatId = 11L
        val messageId = 22L
        val chatFile = mock<ChatDefaultFile>()
        whenever(getChatFileUseCase(chatId, messageId)).thenReturn(chatFile)
        underTest.onDownloadClicked(chatId, messageId)
        assertStartDownloadNode(chatFile)
    }

    @Test
    fun `test that onDownloadClicked for chat files launches the correct event`() = runTest {
        val chatId = 11L
        val messageId1 = 22L
        val messageId2 = 33L
        val chatFile1 = mock<ChatDefaultFile>()
        val chatFile2 = mock<ChatDefaultFile>()
        whenever(getChatFileUseCase(chatId, messageId1)).thenReturn(chatFile1)
        whenever(getChatFileUseCase(chatId, messageId2)).thenReturn(chatFile2)
        underTest.onDownloadClicked(chatId, listOf(messageId1, messageId2))
        assertStartDownloadNode(chatFile1, chatFile2)
    }

    @Test
    fun `test that downloadChatNodesOnlyIfFeatureFlagIsTrue executes the lambda if and only if feature is false`() =
        runTest {
            val toDoIfFeatureFlagIsFalse = mock<() -> Unit>()
            underTest.downloadChatNodesOnlyIfFeatureFlagIsTrue(
                1L,
                listOf(11L),
                toDoIfFeatureFlagIsFalse
            )
            verifyNoInteractions(toDoIfFeatureFlagIsFalse)
        }

    @Test
    fun `test that onSaveOfflineClicked for chat file launches the correct event`() = runTest {
        val chatId = 11L
        val messageId = 22L
        val chatFile = mock<ChatDefaultFile>()
        whenever(getChatFileUseCase(chatId, messageId)).thenReturn(chatFile)
        underTest.onSaveOfflineClicked(chatId, messageId)
        assertStartDownloadForOffline(chatFile)
    }

    @Test
    fun `test that onSaveOfflineClicked with serialized data launches the correct event`() =
        runTest {
            val node = mock<PublicLinkFile>()
            val serializedData = "serialized data"
            whenever(getPublicNodeFromSerializedDataUseCase(serializedData)).thenReturn(node)
            underTest.onSaveOfflineClicked(serializedData)
            assertStartDownloadForOffline(node)
        }

    private suspend fun assertStartDownloadNode(vararg node: TypedNode) {
        underTest.state.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(StateEventWithContentTriggered::class.java)
            val content = (event as StateEventWithContentTriggered).content
            assertThat(content).isInstanceOf(TransferTriggerEvent.StartDownloadNode::class.java)
            assertThat((content as TransferTriggerEvent.StartDownloadNode).nodes)
                .containsExactly(*node)
        }
    }

    private suspend fun assertStartDownloadForOffline(node: TypedNode) {
        underTest.state.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(StateEventWithContentTriggered::class.java)
            val content = (event as StateEventWithContentTriggered).content
            assertThat(content).isInstanceOf(TransferTriggerEvent.StartDownloadForOffline::class.java)
            assertThat((content as TransferTriggerEvent.StartDownloadForOffline).node)
                .isEqualTo(node)
        }
    }
}
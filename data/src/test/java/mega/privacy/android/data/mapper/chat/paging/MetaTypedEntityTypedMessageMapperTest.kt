package mega.privacy.android.data.mapper.chat.paging

import kotlinx.coroutines.test.runTest
import mega.privacy.android.data.database.entity.chat.ChatGeolocationEntity
import mega.privacy.android.data.database.entity.chat.ChatNodeEntity
import mega.privacy.android.data.database.entity.chat.GiphyEntity
import mega.privacy.android.data.database.entity.chat.MetaTypedMessageEntity
import mega.privacy.android.data.database.entity.chat.RichPreviewEntity
import mega.privacy.android.data.database.entity.chat.TypedMessageEntity
import mega.privacy.android.domain.entity.chat.ChatMessageType
import mega.privacy.android.domain.entity.chat.messages.TypedMessage
import mega.privacy.android.domain.usecase.chat.message.CreateTypedMessageUseCase
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.argForWhich
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MetaTypedEntityTypedMessageMapperTest {
    private lateinit var underTest: MetaTypedEntityTypedMessageMapper
    private val messageType = ChatMessageType.NORMAL
    private val createTypedMessageUseCase = mock<CreateTypedMessageUseCase>()

    @BeforeAll
    internal fun setUp() {
        underTest = MetaTypedEntityTypedMessageMapper(
            createTypedMessageUseCases = mapOf(Pair(messageType, createTypedMessageUseCase)),
            createInvalidMessageUseCase = mock()
        )
    }

    @ParameterizedTest(name = "Test isMine {0} ")
    @ValueSource(booleans = [true, false])
    fun `test that request object is created with the correct parameters`(
        isMineParam: Boolean,
    ) = runTest {
        val mock = mock<TypedMessage>()
        whenever(createTypedMessageUseCase.invoke(any())).thenReturn(mock)
        val expectedNodeList = listOf(mock<ChatNodeEntity>())
        val expectedRichPreviewEntity = mock<RichPreviewEntity>()
        val expectedChatGeolocationEntity = mock<ChatGeolocationEntity>()
        val expectedGiphyEntity = mock<GiphyEntity>()

        val expectedTextMessage = "expectedTextMessage"
        val expectedTypedMessageEntity = mock<TypedMessageEntity> {
            on { type } doReturn messageType
            on { isMine } doReturn isMineParam
            on { textMessage } doReturn expectedTextMessage
        }

        val entity = mock<MetaTypedMessageEntity> {
            on { typedMessageEntity } doReturn expectedTypedMessageEntity
            on { nodeList } doReturn expectedNodeList
            on { richPreviewEntity } doReturn expectedRichPreviewEntity
            on { geolocationEntity } doReturn expectedChatGeolocationEntity
            on { giphyEntity } doReturn expectedGiphyEntity
        }
        underTest(entity)

        verify(createTypedMessageUseCase).invoke(argForWhich {
            nodeList == expectedNodeList
                    && chatRichPreviewInfo == expectedRichPreviewEntity
                    && chatGeolocationInfo == expectedChatGeolocationEntity
                    && chatGifInfo == expectedGiphyEntity
                    && textMessage == expectedTextMessage
                    && isMine == isMineParam
        })
    }
}
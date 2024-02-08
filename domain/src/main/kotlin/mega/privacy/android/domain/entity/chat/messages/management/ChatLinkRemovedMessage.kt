package mega.privacy.android.domain.entity.chat.messages.management

/**
 * Chat link created message
 */
data class ChatLinkRemovedMessage(
    override val msgId: Long,
    override val time: Long,
    override val isMine: Boolean,
    override val userHandle: Long,
    override val shouldShowAvatar: Boolean,
    override val shouldShowTime: Boolean,
    override val shouldShowDate: Boolean,
) : ManagementMessage
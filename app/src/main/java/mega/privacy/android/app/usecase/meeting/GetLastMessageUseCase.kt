package mega.privacy.android.app.usecase.meeting

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import mega.privacy.android.app.MegaApplication
import mega.privacy.android.app.R
import mega.privacy.android.app.components.ChatManagement
import mega.privacy.android.app.main.controllers.ChatController
import mega.privacy.android.app.utils.CallUtil
import mega.privacy.android.app.utils.ChatUtil
import mega.privacy.android.app.utils.ChatUtil.converterShortCodes
import mega.privacy.android.app.utils.StringUtils.isTextEmpty
import mega.privacy.android.app.utils.StringUtils.toSpannedHtmlText
import mega.privacy.android.app.utils.Util
import nz.mega.sdk.MegaChatApiAndroid
import nz.mega.sdk.MegaChatCall
import nz.mega.sdk.MegaChatCall.CALL_STATUS_TERMINATING_USER_PARTICIPATION
import nz.mega.sdk.MegaChatCall.CALL_STATUS_USER_NO_PRESENT
import nz.mega.sdk.MegaChatListItem
import nz.mega.sdk.MegaChatMessage.TYPE_ALTER_PARTICIPANTS
import nz.mega.sdk.MegaChatMessage.TYPE_CALL_ENDED
import nz.mega.sdk.MegaChatMessage.TYPE_CALL_STARTED
import nz.mega.sdk.MegaChatMessage.TYPE_CHAT_TITLE
import nz.mega.sdk.MegaChatMessage.TYPE_CONTACT_ATTACHMENT
import nz.mega.sdk.MegaChatMessage.TYPE_CONTAINS_META
import nz.mega.sdk.MegaChatMessage.TYPE_INVALID
import nz.mega.sdk.MegaChatMessage.TYPE_NODE_ATTACHMENT
import nz.mega.sdk.MegaChatMessage.TYPE_NORMAL
import nz.mega.sdk.MegaChatMessage.TYPE_PRIV_CHANGE
import nz.mega.sdk.MegaChatMessage.TYPE_PUBLIC_HANDLE_CREATE
import nz.mega.sdk.MegaChatMessage.TYPE_PUBLIC_HANDLE_DELETE
import nz.mega.sdk.MegaChatMessage.TYPE_SCHED_MEETING
import nz.mega.sdk.MegaChatMessage.TYPE_SET_PRIVATE_MODE
import nz.mega.sdk.MegaChatMessage.TYPE_SET_RETENTION_TIME
import nz.mega.sdk.MegaChatMessage.TYPE_TRUNCATE
import nz.mega.sdk.MegaChatMessage.TYPE_VOICE_CLIP
import javax.inject.Inject

/**
 * Get last message use case to retrieve formatted last message from specific chat
 *
 * @property context        Needed to get strings
 * @property megaChatApi    Needed to retrieve chat messages
 */
class GetLastMessageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val megaChatApi: MegaChatApiAndroid,
) {

    companion object {
        private const val LAST_MSG_LOADING = 255
    }

    private val chatController: ChatController by lazy { ChatController(context) }
    private val chatManagement: ChatManagement by lazy { MegaApplication.getChatManagement() }

    /**
     * Get a formatted String with the last message given the chatId
     *
     * @param chatId    Chat Id to retrieve chat message
     * @return          Single
     */
    fun get(chatId: Long): Single<String> =
        Single.fromCallable {
            val chatListItem = requireNotNull(megaChatApi.getChatListItem(chatId))
            val chatRoom by lazy { megaChatApi.getChatRoom(chatId) }
            val chatMessage by lazy { megaChatApi.getMessage(chatId, chatListItem.lastMessageId) }
            val isMeeting by lazy { megaChatApi.getChatRoom(chatId)?.isMeeting == true }

            if (megaChatApi.hasCallInChatRoom(chatId)) {
                megaChatApi.getChatCall(chatId)?.let { chatCall ->
                    return@fromCallable chatCall.getChatCallStatusMessage(isMeeting)
                }
            }

            return@fromCallable when (chatListItem.lastMessageType) {
                TYPE_INVALID, TYPE_SCHED_MEETING ->
                    context.getString(R.string.no_conversation_history)
                LAST_MSG_LOADING ->
                    context.getString(R.string.general_loading)
                TYPE_NORMAL, TYPE_CHAT_TITLE, TYPE_CALL_ENDED, TYPE_TRUNCATE, TYPE_ALTER_PARTICIPANTS, TYPE_PRIV_CHANGE, TYPE_CONTAINS_META ->
                    chatController.createManagementString(chatMessage, chatRoom)
                TYPE_CALL_STARTED ->
                    if (isMeeting) {
                        context.getString(R.string.meetings_list_ongoing_call_message)
                    } else {
                        context.getString(R.string.ongoing_call_messages)
                    }
                TYPE_SET_RETENTION_TIME -> {
                    val timeFormatted = ChatUtil.transformSecondsInString(chatRoom.retentionTime)
                    if (timeFormatted.isTextEmpty()) {
                        String.format(
                            context.getString(R.string.retention_history_disabled),
                            chatListItem.getSenderName(true)
                        ).cleanHtmlText()
                    } else {
                        String.format(
                            context.getString(R.string.retention_history_changed_by),
                            chatListItem.getSenderName(true),
                            timeFormatted
                        ).cleanHtmlText()
                    }
                }
                TYPE_PUBLIC_HANDLE_CREATE ->
                    String.format(
                        context.getString(R.string.message_created_chat_link),
                        chatListItem.getSenderName(true)
                    ).cleanHtmlText()
                TYPE_PUBLIC_HANDLE_DELETE ->
                    String.format(
                        context.getString(R.string.message_deleted_chat_link),
                        chatListItem.getSenderName(true)
                    ).cleanHtmlText()
                TYPE_SET_PRIVATE_MODE ->
                    String.format(
                        context.getString(R.string.message_set_chat_private),
                        chatListItem.getSenderName(true)
                    ).cleanHtmlText()
                TYPE_NODE_ATTACHMENT -> {
                    val nodeList = chatMessage.megaNodeList
                    val message = if ((nodeList?.size() ?: 0) > 0) {
                        nodeList.get(0).name
                    } else {
                        converterShortCodes(chatListItem.lastMessage)
                    }
                    "${chatListItem.getSenderName()}: $message"
                }
                TYPE_CONTACT_ATTACHMENT -> {
                    val message =
                        context.getString(R.string.contacts_sent, chatMessage.usersCount.toString())
                    "${chatListItem.getSenderName()}: ${converterShortCodes(message)}"
                }
                TYPE_VOICE_CLIP -> {
                    val nodeList = chatMessage.megaNodeList
                    val message = if ((nodeList?.size()
                            ?: 0) > 0 && ChatUtil.isVoiceClip(nodeList.get(0).name)
                    ) {
                        val duration = ChatUtil.getVoiceClipDuration(nodeList.get(0))
                        CallUtil.milliSecondsToTimer(duration)
                    } else {
                        "--:--"
                    }
                    "${chatListItem.getSenderName()}: $message"
                }
                else -> {
                    if (chatListItem.lastMessage.isNullOrBlank()) {
                        context.getString(R.string.error_message_unrecognizable)
                    } else {
                        "${chatListItem.getSenderName()}: ${converterShortCodes(chatListItem.lastMessage)}"
                    }
                }
            }
        }

    private fun MegaChatListItem.getSenderName(includeMyName: Boolean = false): String =
        if (isMine()) {
            if (includeMyName) {
                megaChatApi.myFullname
                    ?: megaChatApi.myEmail
                    ?: context.getString(R.string.chat_last_message_sender_me)
            } else {
                context.getString(R.string.chat_last_message_sender_me)
            }
        } else {
            megaChatApi.getUserFullnameFromCache(lastMessageSender)
                ?: megaChatApi.getUserEmailFromCache(lastMessageSender)
                ?: context.getString(R.string.unknown_name_label)
        }

    private fun MegaChatListItem.isMine(): Boolean =
        lastMessageSender == megaChatApi.myUserHandle

    private fun MegaChatCall.getChatCallStatusMessage(isMeeting: Boolean): String =
        when (status) {
            CALL_STATUS_TERMINATING_USER_PARTICIPATION, CALL_STATUS_USER_NO_PRESENT -> {
                if (isRinging) {
                    context.getString(R.string.notification_subtitle_incoming)
                } else if (isMeeting) {
                    context.getString(R.string.meetings_list_ongoing_call_message)
                } else {
                    context.getString(R.string.ongoing_call_messages)
                }
            }
            else -> {
                val requestSent = chatManagement.isRequestSent(callId)
                if (requestSent) {
                    context.getString(R.string.outgoing_call_starting)
                } else if (isMeeting) {
                    context.getString(R.string.meetings_list_ongoing_call_message)
                } else {
                    context.getString(R.string.call_started_messages)
                }
            }
        }

    private fun String.cleanHtmlText(): String =
        Util.toCDATA(this)
            .replace("[A]", "")
            .replace("[/A]", "")
            .replace("[B]", "")
            .replace("[/B]", "")
            .replace("[C]", "")
            .replace("[/C]", "")
            .toSpannedHtmlText()
            .toString()
}

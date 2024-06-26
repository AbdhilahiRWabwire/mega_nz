package mega.privacy.android.app.listeners

import android.content.Context
import mega.privacy.android.app.MegaApplication
import mega.privacy.android.app.R
import mega.privacy.android.app.main.megachat.GroupChatInfoActivity
import nz.mega.sdk.MegaChatApiJava
import nz.mega.sdk.MegaChatApiJava.MEGACHAT_INVALID_HANDLE
import nz.mega.sdk.MegaChatError
import nz.mega.sdk.MegaChatPeerList
import nz.mega.sdk.MegaChatRequest
import nz.mega.sdk.MegaUser

class InviteToChatRoomListener(context: Context) : ChatBaseListener(context) {

    private var numberOfRequests = 0
    private var success = 0
    private var error = 0

    override fun onRequestFinish(
        api: MegaChatApiJava,
        request: MegaChatRequest,
        e: MegaChatError
    ) {
        if (request.type != MegaChatRequest.TYPE_INVITE_TO_CHATROOM) {
            return
        }

        if (e.errorCode == MegaChatError.ERROR_OK) success++ else error++

        numberOfRequests--

        if (numberOfRequests == 0) {
            val message = when {
                error == 1 -> if (e.errorCode == MegaChatError.ERROR_EXIST) context.getString(
                    R.string.add_participant_error_already_exists
                ) else context.getString(R.string.add_participant_error)

                error > 1 -> context.getString(
                    R.string.number_no_add_participant_request,
                    success,
                    error
                )

                success == 1 -> context.getString(R.string.add_participant_success)

                else -> context.getString(
                    R.string.number_correctly_add_participant,
                    success
                )
            }

            if (context is GroupChatInfoActivity) {
                (context as GroupChatInfoActivity).updateParticipants()
                (context as GroupChatInfoActivity).showSnackbar(message)
            }
        }
    }

    /**
     * Launches the requests to invite new participants to a chat conversation.
     *
     * @param chatId       Identifier of the chat where the participants should be added.
     * @param contactsData List of contacts that should be added as new participants.
     */
    fun inviteToChat(chatId: Long, contactsData: List<String>) {
        numberOfRequests = contactsData.size
        val megaApi = MegaApplication.getInstance().megaApi
        val megaChatApi = MegaApplication.getInstance().megaChatApi

        for (contact in contactsData.indices) {
            val user: MegaUser? = megaApi.getContact(contactsData[contact])

            megaChatApi.inviteToChat(
                chatId,
                user?.handle ?: MEGACHAT_INVALID_HANDLE,
                MegaChatPeerList.PRIV_STANDARD,
                this
            )
        }
    }
}
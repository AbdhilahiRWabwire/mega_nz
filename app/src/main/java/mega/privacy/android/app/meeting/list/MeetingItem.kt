package mega.privacy.android.app.meeting.list

import android.icu.text.SimpleDateFormat
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import mega.privacy.android.app.R
import mega.privacy.android.app.contacts.group.data.ContactGroupUser
import mega.privacy.android.app.utils.StringResourcesUtils
import java.util.Calendar

sealed class MeetingItem(val id: Long) {

    data class Header constructor(val title: String) : MeetingItem(title.hashCode().toLong())

    /**
     * Meeting item
     *
     * @property chatId             Chat identifier
     * @property title              Chat title
     * @property lastMessage        Last chat message
     * @property lastMessageIcon    Last chat icon
     * @property isPublic           Check if chat is public
     * @property isMuted            Check if chat is muted
     * @property hasPermissions     Check if has permissions to clear history
     * @property firstUser          First user of the chat
     * @property lastUser           Last user of the chat
     * @property unreadCount        Unread messages count
     * @property highlight          Check if message should be highlighted
     * @property timeStamp          Last timestamp of the chat
     * @property formattedTimestamp Last timestamp of the chat formatted
     * @property formattedScheduledTimestamp Timestamp of the scheduled chat formatted
     * @property startTimestamp     Schedule meeting start time
     * @property endTimestamp       Schedule meeting start time
     * @property isRecurring        Check if is recurring meeting
     */
    data class Data constructor(
        val chatId: Long,
        val title: String,
        val lastMessage: String?,
        @DrawableRes val lastMessageIcon: Int?,
        val isPublic: Boolean,
        val isMuted: Boolean,
        val hasPermissions: Boolean,
        val firstUser: ContactGroupUser,
        val lastUser: ContactGroupUser?,
        val unreadCount: Int,
        val highlight: Boolean,
        val timeStamp: Long,
        val formattedTimestamp: String,
        val formattedScheduledTimestamp: String?,
        val startTimestamp: Long?,
        val endTimestamp: Long?,
        val isRecurring: Boolean,
    ) : MeetingItem(chatId) {

        /**
         * Check if is a Scheduled Meeting
         */
        fun isScheduled(): Boolean =
            startTimestamp != null || endTimestamp != null

        /**
         * Check if meeting contains only 1 user and myself
         *
         * @return  true if its single false otherwise
         */
        fun isSingleMeeting(): Boolean =
            lastUser == null

        fun getStartDay(): String =
            when {
                startTimestamp == null -> ""
                DateUtils.isToday(startTimestamp) -> StringResourcesUtils.getString(R.string.label_today)
                else -> {
                    val calendar = Calendar.getInstance().apply { timeInMillis = startTimestamp }
                    SimpleDateFormat("EEEE, d MMM").format(calendar.time)
                }
            }
    }
}

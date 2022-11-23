package mega.privacy.android.app.meeting.list.adapter

import android.content.res.ColorStateList
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.drawable.ScalingUtils
import mega.privacy.android.app.R
import mega.privacy.android.app.databinding.ItemMeetingDataBinding
import mega.privacy.android.app.meeting.list.MeetingItem
import mega.privacy.android.app.utils.ColorUtils.getThemeColor
import mega.privacy.android.app.utils.setImageRequestFromUri

class MeetingDataViewHolder(
    private val binding: ItemMeetingDataBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private val selectAnimation by lazy {
        AnimationUtils.loadAnimation(itemView.context, R.anim.multiselect_flip)
    }

    fun bind(item: MeetingItem.Data, isSelected: Boolean) {
        val lastMessageColor = when {
            item.isScheduled() -> ContextCompat.getColor(itemView.context, R.color.red_600_red_300)
            item.highlight -> ContextCompat.getColor(itemView.context, R.color.teal_300_teal_200)
            else -> getThemeColor(itemView.context, android.R.attr.textColorSecondary)
        }

        binding.txtTitle.text = item.title
        if (item.isScheduled()) {
            binding.txtLastMessage.text = item.formattedScheduledTimestamp
            binding.txtLastMessage.isVisible = !item.formattedScheduledTimestamp.isNullOrBlank()
            binding.imgRecurring.isVisible = item.isRecurring
            binding.txtTimestamp.setText(if (item.isRecurring) {
                R.string.meetings_list_recurring_meeting_label
            } else {
                R.string.meetings_list_upcoming_meeting_label
            })
        } else {
            binding.txtLastMessage.text = item.lastMessage
            binding.txtLastMessage.isVisible = !item.lastMessage.isNullOrBlank()
            binding.txtTimestamp.text = item.formattedTimestamp
        }
        binding.txtLastMessage.setTextColor(lastMessageColor)
        if (item.lastMessageIcon != null) {
            binding.imgLastMessage.setImageResource(item.lastMessageIcon)
        } else {
            binding.imgLastMessage.setImageDrawable(null)
        }
        binding.imgLastMessage.imageTintList = ColorStateList.valueOf(lastMessageColor)
        binding.imgLastMessage.isVisible = item.lastMessageIcon != null
        binding.imgMute.isVisible = item.isMuted
        binding.imgPrivate.isVisible = !item.isPublic
        binding.txtUnreadCount.text = item.unreadCount.toString()
        binding.txtUnreadCount.isVisible = item.unreadCount > 0

        val firstUserPlaceholder = item.firstUser.getImagePlaceholder(itemView.context)
        if (item.isSingleMeeting() || item.lastUser == null) {
            binding.imgThumbnail.hierarchy.setPlaceholderImage(
                firstUserPlaceholder,
                ScalingUtils.ScaleType.FIT_CENTER
            )
            binding.imgThumbnail.setImageRequestFromUri(item.firstUser.avatar)
            binding.groupThumbnails.isVisible = false
            binding.imgThumbnail.isVisible = !isSelected
        } else {
            val lastUserPlaceholder = item.lastUser.getImagePlaceholder(itemView.context)
            binding.imgThumbnailGroupFirst.hierarchy.setPlaceholderImage(
                firstUserPlaceholder,
                ScalingUtils.ScaleType.FIT_CENTER
            )
            binding.imgThumbnailGroupLast.hierarchy.setPlaceholderImage(
                lastUserPlaceholder,
                ScalingUtils.ScaleType.FIT_CENTER
            )
            binding.imgThumbnailGroupFirst.setImageRequestFromUri(item.firstUser.avatar)
            binding.imgThumbnailGroupLast.setImageRequestFromUri(item.lastUser.avatar)
            binding.groupThumbnails.isVisible = !isSelected
            binding.imgThumbnail.isVisible = false
        }

        binding.imgSelectState.apply {
            if ((isSelected && !isVisible) || (!isSelected && isVisible)) {
                isVisible = true
                startAnimation(selectAnimation.apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationEnd(animation: Animation?) {
                            isVisible = isSelected
                        }

                        override fun onAnimationStart(animation: Animation?) {
                        }

                        override fun onAnimationRepeat(animation: Animation?) {
                        }
                    })
                })
            } else {
                isVisible = isSelected
            }
        }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
        object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): Long = itemId
        }
}
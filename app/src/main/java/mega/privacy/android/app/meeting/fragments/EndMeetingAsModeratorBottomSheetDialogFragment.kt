package mega.privacy.android.app.meeting.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mega.privacy.android.app.R
import mega.privacy.android.app.databinding.BottomSheetEndMeetingAsModeratorBinding
import mega.privacy.android.app.utils.LogUtil
import nz.mega.sdk.MegaChatApiJava

/**
 * The fragment shows two options for moderator when the moderator leave the meeting:
 * LEAVE CALL or END CALL FOR ALL
 */
class EndMeetingAsModeratorBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetEndMeetingAsModeratorBinding
    private val viewModel by viewModels<InMeetingViewModel>({ requireParentFragment() })

    private var chatId: Long? = MegaChatApiJava.MEGACHAT_INVALID_HANDLE

    private var callBackEndForAll: (() -> Unit)? = null
    private var callBackLeaveMeeting: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatId = viewModel.getChatId()

        if (chatId == MegaChatApiJava.MEGACHAT_INVALID_HANDLE) {
            LogUtil.logError("Error. Chat doesn't exist")
            return
        }

        if (viewModel.getCall() == null) {
            LogUtil.logError("Error. Call doesn't exist")
            return
        }
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        binding =
            BottomSheetEndMeetingAsModeratorBinding.inflate(LayoutInflater.from(context),
                null,
                false).apply {
                lifecycleOwner = this@EndMeetingAsModeratorBottomSheetDialogFragment
            }

        binding.endForAll.setOnClickListener { endForAll() }
        binding.leaveMeeting.setOnClickListener { leaveMeeting() }

        dialog.setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog ?: return
        BottomSheetBehavior.from(dialog.findViewById(R.id.design_bottom_sheet)).state =
            BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * Assign moderator listener will close the page and open assign moderator activity
     */
    private fun endForAll() {
        dismiss()
        callBackEndForAll?.invoke()
    }

    /**
     * Leave anyway listener, will leave meeting directly
     */
    private fun leaveMeeting() {
        dismiss()
        callBackLeaveMeeting?.invoke()
    }

    /**
     * Set the call back for clicking leave meeting option
     *
     * @param leaveMeetingModerator call back
     */
    fun setLeaveMeetingCallBack(leaveMeetingModerator: () -> Unit) {
        callBackLeaveMeeting = leaveMeetingModerator
    }

    /**
     * Set the call back for clicking end for all option
     *
     * @param endMeetingForAll call back
     */
    fun setEndForAllCallBack(endMeetingForAll: () -> Unit) {
        callBackEndForAll = endMeetingForAll
    }
}
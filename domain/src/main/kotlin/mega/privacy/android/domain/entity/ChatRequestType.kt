package mega.privacy.android.domain.entity

/**
 * Chat request type.
 */
enum class ChatRequestType {
    /**
     * Initialize request
     */
    Initialize,

    /**
     * Connect request
     */
    Connect,

    /**
     * Delete request
     */
    Delete,

    /**
     * Logout request
     */
    Logout,

    /**
     * Set online status request
     */
    SetOnlineStatus,

    /**
     * Start chat call request
     */
    StartChatCall,

    /**
     * Answer chat call request
     */
    AnswerChatCall,

    /**
     * Disable audio video call request
     */
    DisableAudioVideoCall,

    /**
     * Hang chat call request
     */
    HangChatCall,

    /**
     * Create chat room request
     */
    CreateChatRoom,

    /**
     * Remove from chat room request
     */
    RemoveFromChatRoom,

    /**
     * Update peer permissions request
     */
    UpdatePeerPermissions,

    /**
     * Invite to chat request
     */
    InviteToChatRoom,

    /**
     * Edit chat room name request
     */
    EditChatRoomName,

    /**
     * Edit chat room pic request
     */
    EditChatRoomPic,

    /**
     * Truncate history request
     */
    TruncateHistory,

    /**
     * Share contact request
     */
    ShareContact,

    /**
     * Get first name request
     */
    GetFirstName,

    /**
     * Get last name request
     */
    GetLastName,

    /**
     * Disconnect request
     */
    Disconnect,

    /**
     * Get email request
     */
    GetEmail,

    /**
     * Attach node message request
     */
    AttachNodeMessage,

    /**
     * Revoke node message request
     */
    RevokeNodeMessage,

    /**
     * Set background status request
     */
    SetBackgroundStatus,

    /**
     * Retry pending connections request
     */
    RetryPendingConnections,

    /**
     * Send typing notification request
     */
    SendTypingNotification,

    /**
     * Signal activity request
     */
    SignalActivity,

    /**
     * Set presence persist request
     */
    SetPresencePersist,

    /**
     * Set presence auto away request
     */
    SetPresenceAutoAway,

    /**
     * Load audio video devices request
     */
    LoadAudioVideoDevices,

    /**
     * Archive chat room request
     */
    ArchiveChatRoom,

    /**
     * Push received request
     */
    PushReceived,

    /**
     * Set last green visible request
     */
    SetLastGreenVisible,

    /**
     * Last green request
     */
    LastGreen,

    /**
     * Load preview request
     */
    LoadPreview,

    /**
     * Chat link handle request
     */
    ChatLinkHandle,

    /**
     * Set private mode request
     */
    SetPrivateMode,

    /**
     * Auto join public chat request
     */
    AutoJoinPublicChat,

    /**
     * Change video stream request
     */
    ChangeVideoStream,

    /**
     * Import messages request
     */
    ImportMessages,

    /**
     * Set retention time request
     */
    SetRetentionTime,

    /**
     * Set call on hold request
     */
    SetCallOnHold,

    /**
     * Enable audio level monitor request
     */
    EnableAudioLevelMonitor,

    /**
     * Manage reaction request
     */
    ManageReaction,

    /**
     * Get peer attributes request
     */
    GetPeerAttributes,

    /**
     * Request speak request
     */
    RequestSpeak,

    /**
     * Approve speak request
     */
    ApproveSpeak,

    /**
     * Request high resolution video request
     */
    RequestHighResVideo,

    /**
     * Request low resolution video request
     */
    RequestLowResVideo,

    /**
     * Open video device request
     */
    OpenVideoDevice,

    /**
     * Request hires quality request
     */
    RequestHiresQuality,

    /**
     * Delete speaker request
     */
    DeleteSpeaker,

    /**
     * Request SVC layers request
     */
    RequestSVCLayers,

    /**
     * Set chat room options request
     */
    SetChatRoomOptions,

    /**
     * Create scheduled meeting request
     */
    CreateScheduledMeeting,

    /**
     * Delete scheduled meeting request
     */
    DeleteScheduledMeeting,

    /**
     * Fetch scheduled meeting occurrences request
     */
    FetchScheduledMeetingOccurrences,

    /**
     * Update scheduled meeting occurrence request
     */
    UpdateScheduledMeetingOccurrence,

    /**
     * Update scheduled meeting request
     */
    UpdateScheduledMeeting,

    /**
     * Waiting room push request
     */
    WaitingRoomPush,

    /**
     * Waiting room allow request
     */
    WaitingRoomAllow,

    /**
     * Waiting room kick request
     */
    WaitingRoomKick,

    /**
     * Ring individual in call request
     */
    RingIndividualInCall,

    /**
     * Mute request
     */
    Mute,

    /**
     * Add/del speaker permissions request
     */
    SpeakerAddDel,

    /**
     * Add/del Speak request
     */
    SpeakRequestAddDel,

    /**
     * Reject call request
     */
    RejectCall,

    /**
     * Raise hand to speak request
     */
    RaiseHandToSpeak,

    /**
     * Set limit call request
     */
    SetLimitCall,

    /**
     * Invalid request
     */
    InvalidRequest,
}
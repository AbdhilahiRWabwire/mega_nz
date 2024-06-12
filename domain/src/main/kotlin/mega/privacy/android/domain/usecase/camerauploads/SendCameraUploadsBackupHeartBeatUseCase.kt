package mega.privacy.android.domain.usecase.camerauploads

import mega.privacy.android.domain.entity.camerauploads.HeartbeatStatus
import mega.privacy.android.domain.repository.CameraUploadsRepository
import javax.inject.Inject

/**
 * Send Camera Uploads Backup Heart Beat Use Case
 * @param cameraUploadsRepository [CameraUploadsRepository]
 */
class SendCameraUploadsBackupHeartBeatUseCase @Inject constructor(private val cameraUploadsRepository: CameraUploadsRepository) {

    /**
     * Invocation function
     * @param heartbeatStatus   Heartbeat status
     * @param lastNodeHandle    Last node handle to be synced
     */
    suspend operator fun invoke(
        heartbeatStatus: HeartbeatStatus,
        lastNodeHandle: Long,
    ) {
        if (cameraUploadsRepository.isCameraUploadsEnabled() == true) {
            cameraUploadsRepository.getCuBackUpId()?.let { backupId ->
                cameraUploadsRepository.sendBackupHeartbeat(
                    backupId = backupId,
                    heartbeatStatus = heartbeatStatus,
                    ups = 0,
                    downs = 0,
                    ts = 0,
                    lastNode = lastNodeHandle,
                )
            }
        }
    }
}

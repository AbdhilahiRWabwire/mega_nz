package mega.privacy.android.domain.usecase.camerauploads

import mega.privacy.android.domain.repository.CameraUploadsRepository
import javax.inject.Inject

/**
 * Use Case that checks whether Camera Uploads should only run through Wi-Fi / Wi-Fi or Mobile Data
 *
 * @property cameraUploadsRepository [CameraUploadsRepository]
 */
class IsCameraUploadsByWifiUseCase @Inject constructor(
    private val cameraUploadsRepository: CameraUploadsRepository,
) {

    /**
     * Invocation function
     *
     * @return true if Camera Uploads will only run through Wi-Fi
     * false if Camera Uploads can run through either Wi-Fi or Mobile Data
     */
    suspend operator fun invoke(): Boolean = cameraUploadsRepository.isCameraUploadsByWifi() ?: true
}

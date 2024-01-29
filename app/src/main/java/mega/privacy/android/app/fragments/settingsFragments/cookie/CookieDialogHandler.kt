package mega.privacy.android.app.fragments.settingsFragments.cookie

import android.content.Context
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mega.privacy.android.app.R
import mega.privacy.android.app.activities.settingsActivities.CookiePreferencesActivity
import mega.privacy.android.app.featuretoggle.ABTestFeatures
import mega.privacy.android.app.featuretoggle.AppFeatures
import mega.privacy.android.app.utils.ContextUtils.isValid
import mega.privacy.android.app.utils.StringUtils.toSpannedHtmlText
import mega.privacy.android.domain.entity.settings.cookie.CookieDialogType
import mega.privacy.android.domain.entity.settings.cookie.CookieType
import mega.privacy.android.domain.qualifier.ApplicationScope
import mega.privacy.android.domain.qualifier.IoDispatcher
import mega.privacy.android.domain.qualifier.MainDispatcher
import mega.privacy.android.domain.usecase.setting.ShouldShowCookieDialogWithAdsUseCase
import mega.privacy.android.domain.usecase.setting.ShouldShowGenericCookieDialogUseCase
import mega.privacy.android.domain.usecase.setting.UpdateCookieSettingsUseCase
import mega.privacy.android.domain.usecase.setting.UpdateCrashAndPerformanceReportersUseCase
import timber.log.Timber
import javax.inject.Inject

/**
 * Class to handle cookie dialog.
 *
 * @property updateCookieSettingsUseCase                Use Case to update cookie settings.
 * @property updateCrashAndPerformanceReportersUseCase  Use Case to update crash and performance reporters.
 * @property shouldShowCookieDialogWithAdsUseCase           Use Case to check if the cookie dialog with Ads should be shown.
 * @property shouldShowGenericCookieDialogUseCase           Use Case to check if the generic cookie dialog should be shown.
 * @property applicationScope                           Scope for the Coroutine launched by the Use Case.
 * @property ioDispatcher                              Dispatcher for the Coroutine launched by the Use Case to perform background operations.
 * @property mainDispatcher                            Dispatcher for the Coroutine launched by the Use Case to perform operations on the main thread.
 */
class CookieDialogHandler @Inject constructor(
    private val updateCookieSettingsUseCase: UpdateCookieSettingsUseCase,
    private val updateCrashAndPerformanceReportersUseCase: UpdateCrashAndPerformanceReportersUseCase,
    private val shouldShowCookieDialogWithAdsUseCase: ShouldShowCookieDialogWithAdsUseCase,
    private val shouldShowGenericCookieDialogUseCase: ShouldShowGenericCookieDialogUseCase,
    @ApplicationScope private val applicationScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) {

    private var dialog: AlertDialog? = null
    private var isCookieDialogWithAds = false

    /**
     * Show cookie dialog if needed.
     *
     * @param context   View context for the Dialog to be shown.
     * @param recreate  Dismiss current dialog and create a new instance.
     */
    @JvmOverloads
    fun showDialogIfNeeded(context: Context, recreate: Boolean = false) {
        if (recreate) dialog?.dismiss()

        checkDialogSettings { state ->
            when (state) {
                CookieDialogType.GenericCookieDialog -> createGenericCookieDialog(context)
                CookieDialogType.CookieDialogWithAds -> createCookieDialogWithAds(context)
                else -> dialog?.dismiss()
            }
        }
    }

    private fun checkDialogSettings(action: (CookieDialogType) -> Unit) {
        applicationScope.launch(ioDispatcher) {
            runCatching {
                val shouldShowCookieDialogWithAds = shouldShowCookieDialogWithAdsUseCase(
                    AppFeatures.InAppAdvertisement,
                    ABTestFeatures.ads,
                    ABTestFeatures.adse
                )
                if (shouldShowCookieDialogWithAds) {
                    withContext(mainDispatcher) {
                        action.invoke(CookieDialogType.CookieDialogWithAds)
                    }
                } else {
                    val shouldShowGenericCookieDialog = shouldShowGenericCookieDialogUseCase()
                    if (shouldShowGenericCookieDialog) {
                        withContext(mainDispatcher) {
                            action.invoke(CookieDialogType.GenericCookieDialog)
                        }
                    } else {
                        withContext(mainDispatcher) {
                            action.invoke(CookieDialogType.None)
                        }
                    }
                }
            }.onFailure {
                Timber.e("failed to check cookie dialog settings: $it")
            }
        }
    }

    private fun createGenericCookieDialog(context: Context) {
        if (dialog?.isShowing == true || !context.isValid()) return

        isCookieDialogWithAds = false
        dialog = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(R.layout.dialog_cookie_alert)
            .setPositiveButton(context.getString(R.string.preference_cookies_accept)) { _, _ ->
                acceptAllCookies()
            }
            .setNegativeButton(context.getString(R.string.settings_about_cookie_settings)) { _, _ ->
                context.startActivity(Intent(context, CookiePreferencesActivity::class.java))
            }
            .create()
            .apply {
                setOnShowListener {
                    val message =
                        context.getString(R.string.dialog_cookie_alert_message)
                            .replace("[A]", "<a href='https://mega.nz/cookie'>")
                            .replace("[/A]", "</a>")
                            .toSpannedHtmlText()

                    findViewById<TextView>(R.id.message)?.apply {
                        movementMethod = LinkMovementMethod.getInstance()
                        text = message
                    }
                }
            }.also { it.show() }
    }

    /**
     * function to create a Cookie dialog where Ads cookies will be mentioned specifically
     * this dialog will be shown when the user will be part of Advertisement experiment and will see the external Ads
     */
    private fun createCookieDialogWithAds(context: Context) {
        if (dialog?.isShowing == true || !context.isValid()) return

        isCookieDialogWithAds = true
        dialog = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(R.layout.dialog_cookie_alert)
            .setPositiveButton(context.getString(R.string.preference_cookies_accept)) { _, _ ->
                acceptAllCookies()
            }
            .setNegativeButton(context.getString(R.string.settings_about_cookie_settings)) { _, _ ->
                context.startActivity(Intent(context, CookiePreferencesActivity::class.java))
            }
            .create()
            .apply {
                setOnShowListener {
                    val message =
                        context.getString(R.string.dialog_ads_cookie_alert_message)
                            .replace("[A]", "<a href='https://mega.nz/cookie'>")
                            .replace("[/A]", "</a>")
                            .toSpannedHtmlText()

                    findViewById<TextView>(R.id.message)?.apply {
                        movementMethod = LinkMovementMethod.getInstance()
                        text = message
                    }
                }
            }.also { it.show() }
    }

    private fun acceptAllCookies() {
        applicationScope.launch {
            runCatching {
                // If the user accepts all cookies, we will enable all the cookies,
                // including the Ads cookies else, enable all the cookies except the Ads cookies
                if (isCookieDialogWithAds) {
                    updateCookieSettingsUseCase(CookieType.entries.toSet())
                } else {
                    updateCookieSettingsUseCase(CookieType.entries.filter { it != CookieType.ADS_CHECK }
                        .toSet())
                }
                updateCrashAndPerformanceReportersUseCase()
            }.onFailure { Timber.e("failed to accept all cookies: $it") }
        }
    }

    /**
     * Show dialog when view is resumed.
     */
    fun onResume() {
        if (dialog?.isShowing == true) {
            checkDialogSettings { state ->
                if (state == CookieDialogType.None) {
                    dialog?.dismiss()
                }
            }
        }
    }

    /**
     * Dismiss dialog when view is destroyed.
     */
    fun onDestroy() {
        dialog?.dismiss()
        dialog = null
    }
}

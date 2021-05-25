package mega.privacy.android.app.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import mega.privacy.android.app.R
import mega.privacy.android.app.activities.PasscodeActivity
import mega.privacy.android.app.contacts.requests.ContactRequestsFragment
import mega.privacy.android.app.databinding.ActivityContactsBinding
import mega.privacy.android.app.utils.ExtraUtils.extraNotNull

@AndroidEntryPoint
class ContactsActivity : PasscodeActivity() {

    companion object {
        private const val EXTRA_SHOW_GROUPS = "EXTRA_SHOW_GROUPS"
        private const val EXTRA_SHOW_SENT_REQUESTS = "EXTRA_SHOW_SENT_REQUESTS"
        private const val EXTRA_SHOW_RECEIVED_REQUESTS = "EXTRA_SHOW_RECEIVED_REQUESTS"

        /**
         * Show Contact list screen
         */
        @JvmStatic
        fun getListIntent(context: Context): Intent =
            Intent(context, ContactsActivity::class.java)

        /**
         * Show Contact group list screen
         */
        @JvmStatic
        fun getGroupsIntent(context: Context): Intent =
            Intent(context, ContactsActivity::class.java).apply {
                putExtra(EXTRA_SHOW_GROUPS, true)
            }

        /**
         * Show Contact sent requests screen
         */
        @JvmStatic
        fun getSentRequestsIntent(context: Context): Intent =
            Intent(context, ContactsActivity::class.java).apply {
                putExtra(EXTRA_SHOW_SENT_REQUESTS, true)
                putExtra(ContactRequestsFragment.EXTRA_IS_OUTGOING, true)
            }

        /**
         * Show Contact received requests screen
         */
        @JvmStatic
        fun getReceivedRequestsIntent(context: Context): Intent =
            Intent(context, ContactsActivity::class.java).apply {
                putExtra(EXTRA_SHOW_RECEIVED_REQUESTS, true)
                putExtra(ContactRequestsFragment.EXTRA_IS_OUTGOING, false)
            }
    }

    private val showGroups by extraNotNull(EXTRA_SHOW_GROUPS, false)
    private val showSentRequests by extraNotNull(EXTRA_SHOW_SENT_REQUESTS, false)
    private val showReceivedRequests by extraNotNull(EXTRA_SHOW_RECEIVED_REQUESTS, false)
    private lateinit var binding: ActivityContactsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupNavigation(savedInstanceState == null)
    }

    private fun setupNavigation(overrideNavGraph: Boolean) {
        val navController = getNavController()

        if (overrideNavGraph) {
            val defaultDestination = when {
                showGroups -> R.id.contact_groups
                showSentRequests || showReceivedRequests -> R.id.contact_requests
                else -> R.id.contact_list
            }
            navController.setGraph(
                navController.graph.apply { startDestination = defaultDestination },
                intent.extras
            )
        }

        NavigationUI.setupActionBarWithNavController(
            this,
            navController,
            AppBarConfiguration(
                topLevelDestinationIds = setOf(),
                fallbackOnNavigateUpListener = {
                    onBackPressed()
                    true
                }
            )
        )
    }

    private fun getNavController(): NavController =
        (supportFragmentManager.findFragmentById(R.id.contacts_nav_host_fragment) as NavHostFragment).navController
}

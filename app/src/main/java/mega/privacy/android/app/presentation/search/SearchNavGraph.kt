package mega.privacy.android.app.presentation.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import mega.privacy.android.app.presentation.node.NodeBottomSheetActionHandler
import mega.privacy.android.app.presentation.node.NodeOptionsBottomSheetViewModel
import mega.privacy.android.app.presentation.search.model.SearchFilter
import mega.privacy.android.app.presentation.search.model.navigation.removeNodeLinkDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.cannotOpenFileDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.cannotVerifyUserNavigation
import mega.privacy.android.app.presentation.search.navigation.changeLabelBottomSheetNavigation
import mega.privacy.android.app.presentation.search.navigation.changeNodeExtensionDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.foreignNodeDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.leaveFolderShareDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.moveToRubbishOrDeleteNavigation
import mega.privacy.android.app.presentation.search.navigation.nodeBottomSheetNavigation
import mega.privacy.android.app.presentation.search.navigation.overQuotaDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.removeShareFolderDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.renameDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.shareFolderAccessDialogNavigation
import mega.privacy.android.app.presentation.search.navigation.shareFolderDialogNavigation
import mega.privacy.android.domain.entity.node.TypedNode


/**
 * Navigation graph for Search
 *
 * @param trackAnalytics Function to track analytics
 * @param showSortOrderBottomSheet Function to show sort order bottom sheet
 * @param navigateToLink Function to navigate to link
 * @param handleClick Function to handle click
 * @param navHostController Navigation controller
 * @param nodeBottomSheetActionHandler Node bottom sheet action handler
 * @param searchActivityViewModel Search activity view model
 * @param onBackPressed OnBackPressed
 */
internal fun NavGraphBuilder.searchNavGraph(
    trackAnalytics: (SearchFilter?) -> Unit,
    showSortOrderBottomSheet: () -> Unit,
    navigateToLink: (String) -> Unit,
    handleClick: (TypedNode?) -> Unit,
    navHostController: NavHostController,
    nodeBottomSheetActionHandler: NodeBottomSheetActionHandler,
    searchActivityViewModel: SearchActivityViewModel,
    nodeOptionsBottomSheetViewModel: NodeOptionsBottomSheetViewModel,
    onBackPressed: () -> Unit,
) {
    composable(searchRoute) {
        SearchScreen(
            trackAnalytics = trackAnalytics,
            handleClick = handleClick,
            navigateToLink = navigateToLink,
            showSortOrderBottomSheet = showSortOrderBottomSheet,
            navHostController = navHostController,
            searchActivityViewModel = searchActivityViewModel,
            onBackPressed = onBackPressed
        )
    }
    moveToRubbishOrDeleteNavigation(
        navHostController = navHostController,
        searchActivityViewModel = searchActivityViewModel,
        nodeOptionsBottomSheetViewModel = nodeOptionsBottomSheetViewModel
    )
    renameDialogNavigation(navHostController, nodeOptionsBottomSheetViewModel)
    nodeBottomSheetNavigation(
        nodeBottomSheetActionHandler,
        navHostController,
        nodeOptionsBottomSheetViewModel
    )
    changeLabelBottomSheetNavigation(navHostController, nodeOptionsBottomSheetViewModel)
    changeNodeExtensionDialogNavigation(
        navHostController = navHostController,
        nodeOptionsBottomSheetViewModel = nodeOptionsBottomSheetViewModel
    )
    cannotVerifyUserNavigation(navHostController)
    removeNodeLinkDialogNavigation(
        navHostController = navHostController,
        searchActivityViewModel = searchActivityViewModel
    )
    shareFolderDialogNavigation(
        navHostController = navHostController,
        searchActivityViewModel = searchActivityViewModel,
        nodeOptionsBottomSheetViewModel = nodeOptionsBottomSheetViewModel,
        nodeBottomSheetActionHandler = nodeBottomSheetActionHandler
    )
    removeShareFolderDialogNavigation(
        navHostController = navHostController,
        searchActivityViewModel = searchActivityViewModel,
        nodeOptionsBottomSheetViewModel = nodeOptionsBottomSheetViewModel
    )
    leaveFolderShareDialogNavigation(
        navHostController = navHostController,
        searchActivityViewModel = searchActivityViewModel,
        nodeOptionsBottomSheetViewModel = nodeOptionsBottomSheetViewModel
    )
    overQuotaDialogNavigation(navHostController = navHostController)
    foreignNodeDialogNavigation(navHostController = navHostController)
    shareFolderAccessDialogNavigation(
        navHostController = navHostController,
        searchActivityViewModel = searchActivityViewModel,
        nodeOptionsBottomSheetViewModel = nodeOptionsBottomSheetViewModel
    )
    cannotOpenFileDialogNavigation(
        navHostController = navHostController,
        nodeOptionsBottomSheetViewModel = nodeOptionsBottomSheetViewModel,
    )
}

/**
 * Route for Search
 */
internal const val searchRoute = "search/main"
internal const val isFromToolbar = "isFromToolbar"
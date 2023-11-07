package mega.privacy.android.core.ui.controls.menus

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import mega.privacy.android.core.R
import mega.privacy.android.core.ui.controls.appbar.LocalMegaAppBarColors
import mega.privacy.android.core.ui.controls.tooltips.Tooltip
import mega.privacy.android.core.ui.model.MenuAction
import mega.privacy.android.core.ui.model.MenuActionWithIcon
import mega.privacy.android.core.ui.theme.MegaTheme


/**
 * Utility function to generate actions for [TopAppBar]
 * @param actions the actions to be added
 * @param maxActionsToShow if there are more actions than that the extra actions will be shown in a drop down menu
 * @param enabled if false, all actions will be disabled, if true each [MenuAction.enabled] will be used to check if the action is enabled or not
 * @param onActionClick event to receive clicks on actions, both in the toolbar or in the drop down menu
 */
@Composable
fun MenuActions(
    actions: List<MenuAction>,
    maxActionsToShow: Int,
    onActionClick: (MenuAction) -> Unit,
    enabled: Boolean = true,
) {
    val sortedActions = actions.sortedBy { it.orderInCategory }
    val visible = sortedActions
        .filterIsInstance(MenuActionWithIcon::class.java)
        .take(maxActionsToShow)
    visible.forEach {
        IconButtonForAction(
            menuAction = it,
            onActionClick = onActionClick,
            enabled = enabled && it.enabled,
        )
    }
    sortedActions.filterNot { visible.contains(it) }.takeIf { it.isNotEmpty() }?.let { notVisible ->
        DropDown(
            actions = notVisible,
            onActionClick = onActionClick,
            enabled = enabled,
        )
    }

}

@Composable
private fun IconButtonForAction(
    menuAction: MenuActionWithIcon,
    onActionClick: (MenuAction) -> Unit,
    enabled: Boolean = true,
) {
    IconButtonWithTooltip(
        iconPainter = menuAction.getIconPainter(),
        description = menuAction.getDescription(),
        onClick = { onActionClick(menuAction) },
        modifier = Modifier.testTag(menuAction.testTag),
        enabled = enabled,
    )
}

@Composable
private fun DropDown(
    actions: List<MenuAction>,
    onActionClick: (MenuAction) -> Unit,
    enabled: Boolean = true,
) {
    var showMoreMenu by remember {
        mutableStateOf(false)
    }
    Box(contentAlignment = Alignment.BottomEnd) {
        IconButtonWithTooltip(
            iconPainter = painterResource(id = R.drawable.ic_more),
            description = stringResource(id = com.google.android.material.R.string.abc_action_menu_overflow_description),
            onClick = { showMoreMenu = !showMoreMenu },
            modifier = Modifier.testTag(TAG_MENU_ACTIONS_SHOW_MORE),
            enabled = enabled,
        )

        DropdownMenu(
            expanded = showMoreMenu,
            onDismissRequest = {
                showMoreMenu = false
            }
        ) {
            actions.forEach {
                DropdownMenuItem(
                    onClick = {
                        onActionClick(it)
                        showMoreMenu = false
                    },
                    modifier = Modifier.testTag(it.testTag)
                ) {
                    Text(text = it.getDescription())
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IconButtonWithTooltip(
    iconPainter: Painter,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(modifier = modifier) {
        val showTooltip = remember { mutableStateOf(false) }
        val haptic = LocalHapticFeedback.current
        Box(
            modifier = Modifier
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(radius = 24.dp),
                    role = Role.Button,
                    onClick = onClick,
                    enabled = enabled,
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showTooltip.value = true
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = description,
                tint = if (enabled) LocalMegaAppBarColors.current.iconsTintColor else MegaTheme.colors.icon.disabled,
            )
        }
        Tooltip(showTooltip, description)
    }
}

/**
 * test tag for the "Show more" menu actions button
 */
const val TAG_MENU_ACTIONS_SHOW_MORE = "menuActionsShowMore"
package mega.privacy.android.app.presentation.audiosection

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mega.privacy.android.app.presentation.audiosection.model.AudioUIEntity

/**
 * The compose view for displaying audios
 */
@Composable
fun AudiosView(
    items: List<AudioUIEntity>,
    isListView: Boolean,
    sortOrder: String,
    modifier: Modifier,
    onChangeViewTypeClick: () -> Unit,
    onClick: (item: AudioUIEntity, index: Int) -> Unit,
    onMenuClick: (AudioUIEntity) -> Unit,
    onSortOrderClick: () -> Unit,
    listState: LazyListState = LazyListState(),
    gridState: LazyGridState = LazyGridState(),
    onLongClick: ((item: AudioUIEntity, index: Int) -> Unit) = { _, _ -> },
) {
    if (isListView) {
        AudioListView(
            items = items,
            lazyListState = listState,
            sortOrder = sortOrder,
            modifier = modifier,
            onChangeViewTypeClick = onChangeViewTypeClick,
            onClick = onClick,
            onMenuClick = onMenuClick,
            onSortOrderClick = onSortOrderClick,
            onLongClick = onLongClick,
        )
    } else {
        AudioGridView(
            items = items,
            lazyGridState = gridState,
            sortOrder = sortOrder,
            modifier = modifier,
            onChangeViewTypeClick = onChangeViewTypeClick,
            onClick = onClick,
            onMenuClick = onMenuClick,
            onSortOrderClick = onSortOrderClick,
            onLongClick = onLongClick,
        )
    }
}
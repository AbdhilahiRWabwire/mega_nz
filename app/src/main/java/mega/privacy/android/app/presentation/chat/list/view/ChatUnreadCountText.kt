package mega.privacy.android.app.presentation.chat.list.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mega.privacy.android.shared.original.core.ui.preview.CombinedThemePreviews
import kotlin.random.Random

/**
 * Chat unread count icon text view
 *
 * @param count     Number of unread items
 * @param modifier  [Modifier]
 */
@Composable
fun ChatUnreadCountText(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colors.secondary),
    ) {
        Text(
            modifier = Modifier
                .testTag("chat_unread_count:text")
                .padding(horizontal = 6.dp, vertical = 1.dp),
            text = count.takeIf { it <= MAX_COUNT }?.toString() ?: "$MAX_COUNT+",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.surface,
            maxLines = 1,
        )
    }
}

private const val MAX_COUNT = 99

@CombinedThemePreviews
@Composable
private fun PreviewChatUnreadCountView() {
    ChatUnreadCountText(
        Random.nextInt(110),
        Modifier
    )
}

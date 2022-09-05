import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Toolkit

val notifications = mutableStateListOf<Notification>()
private val hidingNotificationsJobs = mutableStateMapOf<Any, Job>()


data class Notification(
    val id: Any,
    val title: String,
    val message: AnnotatedString,
    val image: Painter? = null,
    val progress: Float? = null,
    val duration: Long = 10000L,
    val isVisible: Boolean = true,
    val actions: List<String>? = null,
    val onActionClick: ((String) -> Unit)? = null,
    val backgroundColor: Color = Color(0xff1b1f25),
    val headerTextColor: Color = Color(0xffffbc00),
)

@Composable
fun NotificationsArea(
    notificationWidth: Dp = 350.dp,
    notificationHeight: Dp = 250.dp,
    mainWindow: @Composable () -> Unit
) {
    val notificationsList = remember { notifications }
    val density = LocalDensity.current
    val screenHeight = remember {
        with(density) {
            Toolkit.getDefaultToolkit().screenSize.height.toDp()
        }
    }
    val calculatedHeight = (notificationsList.filter { it.isVisible }.size * (notificationHeight.value + 10)).dp
    val dialogState = rememberDialogState(
        position = WindowPosition(30.dp, 30.dp),
        size = DpSize(notificationWidth, 0.dp)
    )

    rememberCoroutineScope().launch {
        notificationsList.filter { it.isVisible }.forEach {
            if (!hidingNotificationsJobs.contains(it.id)) {
                hidingNotificationsJobs[it.id] = launch {
                    delay(it.duration)
                    hideNotification(it.id)
                }
            }
        }
    }

    Dialog(
        state = dialogState,
        visible = true,
        onCloseRequest = {},
        resizable = false,
        transparent = true,
        undecorated = true,
        focusable = false,
    ) {
        window.isAlwaysOnTop = true
        window.isAutoRequestFocus = false
        window.isFocusable = false
        dialogState.size = DpSize(
            width = notificationWidth,
            height = animateDpAsState(min(calculatedHeight, screenHeight - 10.dp)).value
        )
        Box(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                notificationsList.forEach { notification ->
                    val transitionState = remember { MutableTransitionState(!notification.isVisible) }
                    transitionState.targetState = notification.isVisible
                    AnimatedVisibility(transitionState) {
                        Surface(
                            color = notification.backgroundColor,
                            shape = RoundedCornerShape(5.dp),
                            elevation = 3.dp,
                            modifier = Modifier.padding(bottom = 10.dp, start = 3.dp, end = 3.dp)
                        ) {
                            NotificationBox(
                                modifier = Modifier.height(notificationHeight)
                                    .fillMaxWidth(),
                                title = notification.title,
                                message = notification.message,
                                headerTextColor = notification.headerTextColor,
                                onDismissClick = {
                                    hideNotification(notification.id)
                                },
                                actions = notification.actions,
                                onActionClick = notification.onActionClick,
                                image = notification.image,
                                progress = notification.progress
                            )
                        }
                    }
                }
            }
        }
    }
    mainWindow()
}

@Composable
fun NotificationBox(
    modifier: Modifier,
    title: String,
    message: AnnotatedString,
    image: Painter? = null,
    progress: Float? = null,
    headerTextColor: Color,
    onDismissClick: (() -> Unit)? = null,
    actions: List<String>? = null,
    onActionClick: ((String) -> Unit)? = null
) {
    val headerHeight by remember { mutableStateOf(64.dp) }
    val actionsHeight by remember { mutableStateOf(48.dp) }
    var messageHeight by remember { mutableStateOf(0.dp) }
    BoxWithConstraints(
        modifier = modifier,
    ) {
        messageHeight = maxHeight - headerHeight - actionsHeight
        Column {
            Column(
                modifier = Modifier.background(
                    Color.Gray.copy(alpha = 0.1f),
                    RoundedCornerShape(5.dp)
                )
            ) {
                NotificationHeader(
                    title = title,
                    height = headerHeight,
                    color = headerTextColor,
                    progress
                )
                // message
                Row(
                    modifier = Modifier.height(messageHeight)
                ) {
                    if (image != null) {
                        Image(
                            painter = image,
                            contentDescription = "Notification Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.weight(0.5f)
                                .padding(top = 8.dp, bottom = 5.dp, start = 5.dp, end = 5.dp)
                                .clip(RoundedCornerShape(5.dp))
                        )
                    }
                    if (message.isNotEmpty()) {
                        ColumnWithScrollbar(
                            modifier = Modifier.weight(1f)
                                .animateContentSize()
                                .padding(5.dp)
                        ) {
                            Text(
                                text = message,
                                fontSize = 16.sp,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            NotificationActions(
                actions = actions,
                height = actionsHeight,
                onDismissClick = onDismissClick,
                onActionClick = onActionClick
            )
        }
    }
}

@Composable
private fun NotificationHeader(
    title: String,
    height: Dp,
    color: Color,
    progress: Float?
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.height(height)
            .fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.2f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                title,
                modifier = Modifier.padding(horizontal = 15.dp)
                    .weight(1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (progress != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val p = (progress * 100).toInt().toString() + "%"
                    Text(
                        text = p,
                        color = color
                    )
                    Spacer(Modifier.width(5.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp)
                            .padding(5.dp),
                        progress = progress,
                        strokeWidth = 6.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationActions(
    actions: List<String>?,
    height: Dp,
    onDismissClick: (() -> Unit)?,
    onActionClick: ((String) -> Unit)?
) {
    RowWithScrollbar(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.height(height)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        if (onDismissClick != null) {
            TextButton({
                onDismissClick()
            }) {
                Text("Dismiss")
            }
        }
        actions?.forEach { action ->
            TextButton({
                onActionClick?.invoke(action)
            }) {
                Text(action)
            }
        }
    }
}

fun showNotification(notification: Notification) {
    var index: Int
    if (notifications.indexOfFirst { it.id == notification.id }.also { index = it } != -1) {
        // there is a textNotification with the same id so update it with new data
        notifications[index] = notification
    } else {
        notifications.add(notification)
    }
}

fun hideNotification(id: Any) {
    notifications.indexOfFirst { it.id == id }.let { index ->
        if (index != -1) {
            if (notifications[index].isVisible) {
                notifications[index] = notifications[index].copy(isVisible = false)
                // remove from the map and cancel the coroutine job
                hidingNotificationsJobs.remove(id)?.cancel()
            }
        }
    }
}
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    var title by remember { mutableStateOf("Lorem Ipsum") }
    var message by remember {
        mutableStateOf(
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry." +
                    " Lorem Ipsum has been the industry's standard dummy text ever since the 1500s"
        )
    }
    var duration by remember { mutableStateOf(10000L) }
    var actions by remember { mutableStateOf("") }
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(5.dp),
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.weight(1f)
            ) {
                ColumnWithScrollbar(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        title,
                        onValueChange = { title = it },
                        label = {
                            Text("Notification Title", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    TextField(
                        message,
                        onValueChange = { message = it },
                        label = {
                            Text("Notification Message", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    TextField(
                        duration.toString(),
                        onValueChange = {
                            it.toLongOrNull()?.let { durLong ->
                                duration = durLong
                            }
                        },
                        label = {
                            Text("Duration Millis", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    TextField(
                        actions,
                        onValueChange = { actions = it },
                        label = {
                            Text("Actions (Separated With ',')", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    var isImageEnabled by remember { mutableStateOf(false) }
                    var isProgressEnabled by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isImageEnabled,
                            onCheckedChange = { isImageEnabled = it }
                        )
                        Text(text = "Use Image", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isProgressEnabled,
                            onCheckedChange = { isProgressEnabled = it }
                        )
                        Text(text = "Use Progress", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    val imagePainter = if (isImageEnabled) painterResource("images/colors.jpg") else null
                    val scope = rememberCoroutineScope()
                    Button(
                        onClick = {
                            if (!isProgressEnabled) {
                                showNotification(
                                    Notification(
                                        id = System.currentTimeMillis(),
                                        title = title,
                                        message = AnnotatedString(message),
                                        actions = actions.split(",")
                                            .filter { it.isNotEmpty() },
                                        duration = duration,
                                        image = imagePainter,
                                    )
                                )
                            } else {
                                scope.launch {
                                    var currentProgress = 0f
                                    val notificationWithProgress = Notification(
                                        id = System.currentTimeMillis(),
                                        title = title,
                                        message = AnnotatedString(message),
                                        actions = actions.split(",")
                                            .filter { it.isNotEmpty() },
                                        duration = duration,
                                        image = imagePainter,
                                        progress = 0f
                                    )
                                    if (notificationWithProgress.progress != null) {
                                        while (currentProgress < 1f) {
                                            delay(200)
                                            val newProgress = currentProgress + 0.01f
                                            currentProgress = newProgress
                                            showNotification(notificationWithProgress.copy(progress = newProgress))
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Show Notification")
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            Surface(
                shape = RoundedCornerShape(5.dp),
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.weight(1f)
            ) {
                NotificationsHistory(
                    Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationsHistory(
    modifier: Modifier = Modifier
) {
    val notificationsList = remember { notifications }
    val keyStyle = remember {
        SpanStyle(
            color = Color(0xffffbc00),
            fontWeight = FontWeight.Bold
        )
    }
    val valueStyle = remember {
        SpanStyle(
            color = Color(0xffbaf0ff),
            fontWeight = FontWeight.Bold
        )
    }
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Notifications History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xffffbc00)
            )
        }
        Spacer(Modifier.height(10.dp))
        ColumnWithScrollbar {
            notificationsList.forEach {
                Surface(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    elevation = 3.dp,
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(keyStyle) { append("ID: ") }
                            withStyle(valueStyle) { append(it.id.toString()) }
                            append("\n")
                            withStyle(keyStyle) { append("Title: ") }
                            withStyle(valueStyle) { append(it.title) }
                            append("\n")
                            withStyle(keyStyle) { append("Message: ") }
                            withStyle(valueStyle) { append(it.message.take(100).toString()) }
                            append("\n")
                            withStyle(keyStyle) { append("Duration: ") }
                            withStyle(valueStyle) { append("${it.duration} Millis") }
                            append("\n")
                            withStyle(keyStyle) { append("Is Visible: ") }
                            withStyle(valueStyle) { append(it.isVisible.toString()) }
                        },
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

fun main() = application {
    MyAppTheme {
        NotificationsArea(
            notificationWidth = 500.dp
        ) {
            val windowState = rememberWindowState(size = DpSize(800.dp, 600.dp))
            Window(
                onCloseRequest = ::exitApplication,
                state = windowState,
                resizable = false
            ) {
                App()
            }
        }
    }
}

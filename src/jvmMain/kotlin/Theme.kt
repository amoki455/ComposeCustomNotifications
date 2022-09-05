import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Primary = Color(0xff367aff)
val SecondaryColor = Color(0xff8589ff)

private val LightColorPalette = lightColors(
    primary = Primary,
    secondary = SecondaryColor,

    background = Color(0xff1b1f25),
    surface =  Color(0xff374950),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xffffbc00),
    onSurface = Color(0xffbaf0ff),
)

@Composable
fun MyAppTheme(
    content: @Composable () -> Unit
) {
    val colors = LightColorPalette
    MaterialTheme(
        colors = colors,
        content = content
    )
}
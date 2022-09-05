import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ColumnWithScrollbar(
    modifier: Modifier = Modifier,
    isScrollEnabled: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Box(modifier) {
        var columnModifier = Modifier.fillMaxWidth()
        if (isScrollEnabled) {
            columnModifier = columnModifier.then(Modifier.verticalScroll(scrollState))
        }
        Column(
            columnModifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            content()
        }
        if (isScrollEnabled) {
            VerticalScrollbar(
                rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.TopEnd)
                    .width(5.dp)
                    .padding(vertical = 3.dp),
                style = LocalScrollbarStyle.current.copy(
                    unhoverColor = MaterialTheme.colors.onSurface,
                    hoverColor = MaterialTheme.colors.secondary
                )
            )
        }
    }
}

@Composable
fun RowWithScrollbar(
    modifier: Modifier = Modifier,
    isScrollEnabled: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Box(modifier) {
        var rowModifier = Modifier.fillMaxWidth()
        if (isScrollEnabled) {
            rowModifier = rowModifier.then(Modifier.horizontalScroll(scrollState))
        }
        Row(
            rowModifier,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment
        ) {
            content()
        }
        if (isScrollEnabled) {
            HorizontalScrollbar(
                rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(5.dp)
                    .padding(horizontal = 3.dp),
                style = LocalScrollbarStyle.current.copy(
                    unhoverColor = MaterialTheme.colors.onSurface,
                    hoverColor = MaterialTheme.colors.secondary
                )
            )
        }
    }
}
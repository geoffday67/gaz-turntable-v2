import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import kotlin.math.max
import kotlin.math.min

@Composable
fun Chart(
    modifier: Modifier = Modifier,
    maxValue: Int,
    bars: List<MainViewModel.Bar>,
    selected: Int? = null,
    neighbour: Int? = null,
    position: Float? = null,
) {
    Canvas(
        modifier = modifier,
    ) {
        if (maxValue <= 0) {
            return@Canvas
        }

        val barWidth = size.width / bars.size
        var x = 0f
        bars.forEachIndexed { index, bar ->
            val barHeight = min(size.height * bar.value / maxValue, size.height)
            drawRect(
                color = when (index) {
                    selected ?: -1 -> Color.Red
                    neighbour ?: -1 -> Color.Green
                    else -> Color.Blue
                },
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight),
                style = Fill
            )
            x += barWidth
        }

        position?.let {
            println("Position = $it")
            drawRect(
                color = Color.Gray,
                topLeft = Offset(it * size.width, 0f),
                size = Size(4f, size.height),
            )
        }
    }
}

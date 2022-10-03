import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = viewModel::onOpen,
                enabled = !viewModel.portOpen,
            ) {
                Text("Open")
            }
            Button(
                onClick = viewModel::onClose,
                enabled = viewModel.portOpen,
            ) {
                Text("Close")
            }
        }
        Chart(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .border(2.dp, Color.Red),
            bars = viewModel.levels,
            maxValue = viewModel.maxLevel,
            selected = viewModel.selected,
            neighbour = viewModel.neighbour,
            position = viewModel.position,
        )
    }
    if (viewModel.choosingPort) {
        PortScreen(
            viewModel.ports,
            viewModel::onOpenPort,
            viewModel::onCancelOpen,
        )
    }
}
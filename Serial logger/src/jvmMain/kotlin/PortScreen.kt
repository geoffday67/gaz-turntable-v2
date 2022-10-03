import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fazecast.jSerialComm.SerialPort

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PortScreen(
    ports: List<SerialPort>,
    onOpen: (SerialPort) -> Unit,
    onCancel: () -> Unit,
) {
    var selected: SerialPort? by remember { mutableStateOf(null) }

    AlertDialog(
        title = { Text("Choose port") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                ports.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = it == selected,
                            onClick = {
                                selected = it
                            },
                        )
                        Text(it.systemPortPath)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selected?.let { onOpen(it) } },
                enabled = selected != null,
            ) {
                Text("Open")
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel,
            ) {
                Text("Cancel")
            }
        },
        onDismissRequest = {},
        modifier = Modifier.fillMaxWidth(0.5f),
    )
}

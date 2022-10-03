import androidx.compose.runtime.*
import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortMessageListener

class MainViewModel {
    var port: SerialPort? by mutableStateOf(null)
    var ports = mutableStateListOf<SerialPort>()
    var choosingPort by mutableStateOf(false)
    val portOpen by derivedStateOf { port?.isOpen == true }
    val levels = mutableStateListOf<Bar>()
    var maxLevel by mutableStateOf(700)
    var selected: Int? by mutableStateOf(null)
    var neighbour: Int? by mutableStateOf(null)
    var position: Float? by mutableStateOf(null)

    private val dataListener: SerialPortMessageListener = object : SerialPortMessageListener {
        override fun getListeningEvents() = SerialPort.LISTENING_EVENT_DATA_RECEIVED
        override fun getMessageDelimiter() = "\r\n".toByteArray()
        override fun delimiterIndicatesEndOfMessage() = true

        override fun serialEvent(event: SerialPortEvent) {
            val line = event.receivedData.dropLast(2).fold("") { acc, value -> acc + Char(value.toInt()) }
            println("Received: $line")
            val parts = line.split(':', ',')

            when (parts[0]) {
                "Level" -> {
                    levels.clear()
                    levels.addAll(
                        parts.drop(1).mapIndexed { index, part ->
                            Bar(
                                caption = (index + 1).toString(),
                                value = try {
                                    part.toInt()
                                } catch (ignore: Exception) {
                                    0
                                }
                            )
                        },
                    )
                }
                "Position" -> {
                    position = parts[1].toFloat()
                }
                /*"Largest" -> {
                    selected = parts[1].toInt()
                }
                "Neighbour" -> {
                    neighbour = parts[1].toInt()
                }*/
            }
        }
    }

    fun onOpen() {
        ports.clear()
        ports.addAll(SerialPort.getCommPorts().filterNot { it.systemPortPath.contains("tty") })
        choosingPort = true
    }

    fun onCancelOpen() {
        choosingPort = false
    }

    fun onClose() {
        port?.closePort()
        port = null
    }

    fun onOpenPort(target: SerialPort) {
        println("Opening port ${target.systemPortPath}")
        target.baudRate = 115200
        target.addDataListener(dataListener)
        target.openPort()
        println("Port is ${if (target.isOpen) "open" else "closed"}")
        port = target
        choosingPort = false
    }

    data class Bar(
        val caption: String,
        val value: Int,
    )
}

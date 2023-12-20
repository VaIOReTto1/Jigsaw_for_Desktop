import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import config.*

fun main() = application {
    initializeDatabase()
    //clearDatabase()
    Window(
        onCloseRequest = ::exitApplication,
        title = "拼图游戏",
        state = rememberWindowState(width = 900.dp, height = 506.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xff50665d))
        ) {
            App()
        }

    }
}

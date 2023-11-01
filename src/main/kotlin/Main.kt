import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

@Composable
@Preview
fun App() {
    MaterialTheme {
        PuzzleGame()
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication,state = rememberWindowState(width = 500.dp, height = 650.dp), title = "拼图游戏") {
        App()
    }
}

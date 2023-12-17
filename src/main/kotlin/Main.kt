import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun App() {
    var isGameRunning by remember { mutableStateOf(false) }
    var imagePath by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),

            ) {
            if (isGameRunning) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    isGameRunning = false
                    println(imagePath)
                }, colors = ButtonDefaults.buttonColors(Color(0xff708090))) {
                    Text("切换图片")
                }
                Spacer(modifier = Modifier.height(16.dp))
                PuzzleGame(imagePath)
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val selectedImagePath = openImageFileChooser()
                        if (selectedImagePath.isNotEmpty()) {
                            imagePath = selectedImagePath
                            isGameRunning = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xff708090)),
                    modifier = Modifier.padding(start = 190.dp, end = 100.dp)) {
                        Text("开始游戏")
                    }
            }
        }
    }
}

fun openImageFileChooser(): String {
    val fileChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        fileFilter = FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "bmp")
    }
    val result = fileChooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.absolutePath
    } else ""
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "拼图游戏",
        state = rememberWindowState(width = 550.dp, height = 785.dp)
    ) {
        App()
    }
}
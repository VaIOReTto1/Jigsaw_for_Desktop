import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

@Composable
fun App() {
    var isGameRunning by remember { mutableStateOf(false) }
    var imagePath by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            if (isGameRunning) {
                Button(onClick = { isGameRunning = false }) {
                    Text("切换图片")
                }
                PuzzleGame(imagePath)
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                TextEntry(onTextEntered = { imagePath = it })
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { if (imagePath.isNotEmpty()) isGameRunning = true }) {
                    Text("开始游戏")
                }
            }
        }
    }
}

@Composable
fun TextEntry(onTextEntered: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    BasicTextField(
        value = text,
        onValueChange = {
            text = it
            onTextEntered(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(8.dp),
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 18.sp,
            textAlign = TextAlign.Start
        ),
        singleLine = true
    )
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "拼图游戏",
        state = rememberWindowState(width = 500.dp, height = 650.dp)
    ) {
        App()
    }
}
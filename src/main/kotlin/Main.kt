import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

@Composable
@Preview
fun App() {
    MaterialTheme {
        var isOpen by remember { mutableStateOf(false) }
        var textValue by remember { mutableStateOf("") }
        if (isOpen) Column {
            Button(
                onClick = { isOpen = false }
            ) {
                Text("切换图片")
            }
            PuzzleGame(textValue)
        } else Column {
            BasicTextField(
                modifier = Modifier.height(50.dp).padding(8.dp).fillMaxWidth(),
                value = textValue,
                onValueChange = { textValue = if (it == "") "0" else it },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.Gray,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.End
                ),
                visualTransformation = VisualTransformation.None,
                singleLine = true
            )
            Button(
                onClick = { isOpen = true }
            ) {
                Text("输入成功")

            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 500.dp, height = 650.dp),
        title = "拼图游戏"
    ) {
        App()
    }
}

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


@Composable
fun App() {
    var difficulty by remember { mutableStateOf("Medium") } // 难度
    var isGameRunning by remember { mutableStateOf(false) }
    var selectedImage by remember {
        mutableStateOf<BufferedImage>(
            ImageIO.read(File("D:\\Program\\jigsaw for desktop\\src\\main\\resources\\image\\1.jpg"))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xffb9c6cd), shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Row {
                Column {
                    Spacer(modifier = Modifier.height(30.dp))

                    // 开始游戏按钮和排行榜按钮
                    GameButton("开始游戏") {
                        isGameRunning = true
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Spacer(Modifier.width(8.dp))
                    GameButton("排行榜") {
                        // TODO: 排行榜逻辑
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 规则按钮
                    GameButton("规则") {
                        // TODO: 规则逻辑
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 切换图片按钮
                    GameButton("切换图片") {
                        val newImagePath = openImageFileChooser()
                        if (newImagePath.isNotEmpty()) {
                            selectedImage = ImageIO.read(File(newImagePath))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                InsetShadowBox(
                    modifier = Modifier
                        .background(
                            color = Color(0xff50665d),
                            shape = RoundedCornerShape(50)
                        ).width(20.dp).fillMaxHeight()
                )

                Spacer(modifier = Modifier.width(10.dp))

                // 显示用户选择的图片或占位符
                if (isGameRunning) {
                    PuzzleGame(selectedImage)
                } else {
                    Image(bitmap = selectedImage.toComposeImageBitmap(), contentDescription = "Selected Puzzle Image")
                }
                Spacer(modifier = Modifier.width(10.dp))

                InsetShadowBox(
                    modifier = Modifier
                        .background(
                            color = Color(0xff50665d),
                            shape = RoundedCornerShape(50)
                        ).width(20.dp).fillMaxHeight()
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    // 难度选择按钮
                    DifficultyButtons(difficulty) { newDifficulty ->
                        difficulty = newDifficulty
                    }
                }
            }

        }
    }
}

@Composable
fun InsetShadowBox(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.clip(RoundedCornerShape(50))) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val shadowColor = Color.Black
        val shadowAlpha = 0.5f
        val cornerRadius = CornerRadius(if (width < height) width / 2 else height / 2)

        Canvas(modifier = Modifier.matchParentSize()) {// Draw the main rounded rectangle
            drawRoundRect(
                color = Color(0xff50665d),
                size = Size(width, height),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(shadowColor.copy(alpha = shadowAlpha), Color.Transparent),
                    startY = 0f,
                    endY = 10f
                ),
                size = Size(width, 10f),
                topLeft = Offset(0f, 0f),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, shadowColor.copy(alpha = shadowAlpha)),
                    startY = height - 10f, // Adjust for the desired spread of the shadow
                    endY = height
                ),
                size = Size(width, 10f),
                topLeft = Offset(0f, height - 10f),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(shadowColor.copy(alpha = shadowAlpha), Color.Transparent),
                    startX = 0f,
                    endX = 10f
                ),
                size = Size(10f, height),
                topLeft = Offset(0f, 0f),
                cornerRadius = cornerRadius
            )

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, shadowColor.copy(alpha = shadowAlpha)),
                    startX = width - 10f, // Adjust for the desired spread of the shadow
                    endX = width
                ),
                size = Size(10f, height),
                topLeft = Offset(width - 10f, 0f),
                cornerRadius = cornerRadius
            )
        }
    }
}

@Composable
fun DifficultyButtons(selectedDifficulty: String, onDifficultySelected: (String) -> Unit) {
    Column {
        val difficulties = listOf("简单", "中等", "困难")
        difficulties.forEach { difficulty ->
            GameButton(
                label = difficulty,
                isSelected = difficulty == selectedDifficulty,
                onClick = { onDifficultySelected(difficulty) }
            )
            Spacer(Modifier.width(8.dp))
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun GameButton(label: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = if (isSelected) Color(0xff737c83) else Color(0xffbfcfda)),
        modifier = Modifier
            .height(48.dp)
            .width(120.dp)
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color(0xff36261b)
            ), // Green shadow
        shape = RoundedCornerShape(8.dp), // Rounded corners for the button
        elevation = ButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 6.dp)
    ) {
        Text(label, fontSize = 18.sp, color = Color.Black)
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
        state = rememberWindowState(width = 900.dp, height = 506.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xff50665d))
        ) {
            App()
        }

    }
}

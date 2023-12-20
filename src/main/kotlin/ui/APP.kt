import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import config.*
import kotlinx.coroutines.delay
import ui.PuzzleGame
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun App() {
    var remainingTime by remember { mutableStateOf(60) }
    var difficulty by remember { mutableStateOf("简单") } // 难度
    var isGameRunning by remember { mutableStateOf(false) }
    var selectedImage by remember {
        mutableStateOf<BufferedImage>(
            ImageIO.read(File("D:\\Program\\jigsaw for desktop\\src\\main\\resources\\image\\1.jpg"))
        )
    }
    var showRulesDialog by remember { mutableStateOf(false) }

    var showLeaderboardDialog by remember { mutableStateOf(false) }
    val selectedLeaderboardDifficulty by remember { mutableStateOf("简单") }

    LaunchedEffect(isGameRunning) {
        if (isGameRunning) {
            while (remainingTime > 0) {
                delay(1000) // 等待一秒
                remainingTime-- // 减少剩余时间
            }
            isGameRunning = false // 当时间耗尽时停止游戏
        }
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
                    if (showRulesDialog) {
                        RulesDialog(onDismiss = { showRulesDialog = false })
                    }

                    if (showLeaderboardDialog) {
                        LeaderboardDialog(selectedDifficulty = mutableStateOf(selectedLeaderboardDifficulty)) {
                            showLeaderboardDialog = false
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // 开始游戏按钮和排行榜按钮
                    GameButton(if (!isGameRunning) "开始游戏" else "结束游戏") {
                        isGameRunning = !isGameRunning
                        if (isGameRunning) {
                            remainingTime = 59 // 重置计时器
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // 切换图片按钮
                    GameButton("切换图片") {
                        val newImagePath = openImageFileChooser()
                        if (newImagePath.isNotEmpty()) {
                            selectedImage = ImageIO.read(File(newImagePath))
                        }
                    }

                    Column(
                        modifier = Modifier.padding(top = 14.dp, bottom = 14.dp).height(55.dp)
                            .width(120.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        InsetShadowBox(
                            modifier = Modifier
                                .background(
                                    color = Color(0xff50665d),
                                    shape = RoundedCornerShape(50)
                                ).width(90.dp).height(5.dp).padding(4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // 计时器显示
                        Text(text = if (isGameRunning) formatTime(remainingTime) else "01:00", fontSize = 25.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        InsetShadowBox(
                            modifier = Modifier
                                .background(
                                    color = Color(0xff50665d),
                                    shape = RoundedCornerShape(50)
                                ).width(90.dp).height(5.dp).padding(4.dp)
                        )
                    }

                    GameButton("排行榜") {
                        showLeaderboardDialog = true
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 规则按钮
                    GameButton("规则") {
                        showRulesDialog = true
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
                    PuzzleGame(
                        selectedImage,
                        parseDifficulty(difficulty),
                        isGameRunning,
                        formatTime(remainingTime).substring(3)
                    ) {
                        isGameRunning = false
                    }
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
                    Image(
                        bitmap = selectedImage.toComposeImageBitmap(),
                        contentDescription = "Selected Puzzle Image",
                        modifier = Modifier.width(100.dp).height(100.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // 难度选择按钮
                    DifficultyButtons(difficulty) { newDifficulty ->
                        difficulty = newDifficulty
                    }
                }
            }

        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
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
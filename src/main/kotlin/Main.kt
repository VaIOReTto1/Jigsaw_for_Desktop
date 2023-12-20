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
import kotlinx.coroutines.delay
import java.awt.image.BufferedImage
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
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
                    Image(bitmap = selectedImage.toComposeImageBitmap(), contentDescription = "Selected Puzzle Image",modifier = Modifier.width(100.dp).height(100.dp))
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

fun parseDifficulty(difficulty: String): Int {
    return when (difficulty) {
        "简单" -> 9
        "普通" -> 16
        "困难" -> 25
        else -> 9
    }
}

data class GameResult(val rank: Int, val time: Int, val completionTime: String)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LeaderboardDialog(selectedDifficulty: MutableState<String>, onDismiss: () -> Unit) {
    // 当前选择的难度级别
    val currentDifficulty = selectedDifficulty.value

    // 从数据库获取排行榜数据
    val leaderboardData by remember(currentDifficulty) {
        mutableStateOf(getLeaderboardData(currentDifficulty))
    }

    AlertDialog(
        modifier = Modifier
            .clip(RoundedCornerShape(8)),
        backgroundColor = Color(0xffeff1f1),
        onDismissRequest = onDismiss,
        title = { Text("排行榜 - $currentDifficulty") },
        text = {
            Column(modifier = Modifier.background(color = Color(0xffeff1f1))) {
                // 难度选择按钮
                DifficultySelectionButtons(selectedDifficulty = selectedDifficulty)

                Spacer(modifier = Modifier.height(16.dp))

                // 排行榜数据
                leaderboardData.forEach { result ->
                    Text("${result.rank}   时间: ${result.time}秒, 完成时间: ${result.completionTime}")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xffcde0ea)),
                modifier = Modifier
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(8.dp),
                        spotColor = Color(0xff36261b)
                    ), // Green shadow
                shape = RoundedCornerShape(8.dp), // Rounded corners for the button
                elevation = ButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 6.dp)
            ) {
                Text("关闭")
            }
        }
    )
}

@Composable
fun DifficultySelectionButtons(selectedDifficulty: MutableState<String>) {
    Row {
        listOf("简单", "普通", "困难").forEach { difficulty ->
            Button(
                onClick = { selectedDifficulty.value = difficulty },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (selectedDifficulty.value != difficulty) Color.LightGray else Color(
                        0xffcde0ea
                    )
                ),
                modifier = Modifier
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(8.dp),
                        spotColor = Color(0xff36261b)
                    ), // Green shadow
                shape = RoundedCornerShape(8.dp), // Rounded corners for the button
                elevation = ButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 6.dp)
            ) {
                Text(difficulty)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}


fun getLeaderboardData(difficulty: String): List<GameResult> {
    val url = "jdbc:sqlite:game_data.db"
    val sql = """
        SELECT time, completion_time 
        FROM game_results 
        WHERE difficulty = ? 
        ORDER BY time, completion_time
    """.trimIndent()

    val leaderboardData = mutableListOf<GameResult>()

    DriverManager.getConnection(url).use { conn ->
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, difficulty)
            val rs = pstmt.executeQuery()
            var rank = 1
            while (rs.next()) {
                val time = rs.getInt("time")
                val completionTime = rs.getString("completion_time")
                leaderboardData.add(GameResult(rank++, time, completionTime))
            }
        }
    }

    return leaderboardData
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RulesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier
            .clip(RoundedCornerShape(8)),
        backgroundColor = Color(0xffeff1f1),
        onDismissRequest = onDismiss,
        title = { Text("游戏规则") },
        text = {
            Text(
                "拼图只能左右上下滑动。\n" +
                        "简单难度为 3x3 拼图。\n" +
                        "普通难度为 4x4 拼图。\n" +
                        "困难难度为 5x5 拼图。\n" +
                        "规定时间内完成即获胜。\n" +
                        "建议选择960x960的照片", modifier = Modifier.background(color = Color(0xffeff1f1))
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xffcde0ea)),
                modifier = Modifier
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(8.dp),
                        spotColor = Color(0xff36261b)
                    ), // Green shadow
                shape = RoundedCornerShape(8.dp), // Rounded corners for the button
                elevation = ButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 6.dp)
            ) {
                Text("关闭")
            }
        }
    )
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

fun initializeDatabase() {
    val url = "jdbc:sqlite:game_data.db"
    val sql = """
        CREATE TABLE IF NOT EXISTS game_results (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            difficulty TEXT,
            time INTEGER,
            completion_time TEXT
        )
    """.trimIndent()

    DriverManager.getConnection(url).use { conn ->
        conn.createStatement().use { stmt ->
            stmt.execute(sql)
        }
    }
}

fun clearDatabase() {
    val url = "jdbc:sqlite:game_data.db"
    val sqlDelete = "DELETE FROM game_results" // 删除所有行
    val sqlVacuum = "VACUUM" // 重建数据库文件，重置自增主键

    DriverManager.getConnection(url).use { conn ->
        conn.createStatement().use { stmt ->
            stmt.execute(sqlDelete) // 执行删除操作
            stmt.execute(sqlVacuum) // 执行 VACUUM 操作
        }
    }
}

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

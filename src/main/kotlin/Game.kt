import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.skiko.toBitmap
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PuzzleGame(filePath: String="src/main/resources/1.jpg") {
    var difficulty by remember { mutableStateOf(9) }
    val image: BufferedImage = ImageIO.read(File(filePath))
    var isTimerRunning by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf(60) }
    var isPuzzleComplete by remember { mutableStateOf(false) }
    var isPuzzleEnd by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 选择难度
        Row {
            DifficultyButton("普通", 9, difficulty) { difficulty = 9 }
            Spacer(modifier = Modifier.width(16.dp))
            DifficultyButton("中级", 16, difficulty) { difficulty = 16 }
            Spacer(modifier = Modifier.width(16.dp))
            DifficultyButton("高级", 25, difficulty) { difficulty = 25 }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isTimerRunning) {
            PuzzleBoard(image, difficulty, onPuzzleCompleted = { isPuzzleComplete = true })
        } else {
            Image(
                bitmap = ImageIO.read(File(filePath)).toComposeImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(350.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        //计时器
        if (isTimerRunning) {
            Timer(remainingTime) {
                remainingTime = it
                if (it == 0) isPuzzleEnd = true
            }
        }

        Button(onClick = { isTimerRunning = !isTimerRunning }) {
            Text(if (isTimerRunning) "结束游戏" else "开始游戏")
        }

        if (isPuzzleComplete) {
            AlertDialog(
                modifier = Modifier.width(300.dp).height(150.dp),
                onDismissRequest = {},
                title = {
                    Text("恭喜！")
                },
                text = {
                    Text("游戏胜利！")
                },
                confirmButton = {
                    Button(onClick = {
                        isPuzzleComplete = false // 关闭对话框
                        isTimerRunning = false // 停止计时器
                        remainingTime = 60 // 重置计时器
                    }) {
                        Text("确定")
                    }
                }
            )
        }

        if (isPuzzleEnd) {
            AlertDialog(
                modifier = Modifier.width(300.dp).height(150.dp),
                onDismissRequest = {},
                title = {
                    Text("很遗憾")
                },
                text = {
                    Text("挑战失败")
                },
                confirmButton = {
                    Button(onClick = {
                        isPuzzleEnd = false // 关闭对话框
                        isTimerRunning = false // 停止计时器
                        remainingTime = 60 // 重置计时器
                    }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

@Composable
fun DifficultyButton(label: String, value: Int, current: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (current == value) Color.LightGray else Color.Gray
        )
    ) {
        Text(label)
    }
}

@Composable
fun PuzzleBoard(image: BufferedImage, difficulty: Int, onPuzzleCompleted: () -> Unit) {
    val piecesInRow = kotlin.math.sqrt(difficulty.toDouble()).toInt()
    val pieceWidth = image.width / piecesInRow
    val pieceHeight = image.height / piecesInRow

    val puzzleState by remember { mutableStateOf(PuzzleState(difficulty)) }
    var selectedPieceIndex by remember { mutableStateOf(-1) }

    // 创建拼图片段
    val pieces = List(difficulty) { index ->
        val x = (index % piecesInRow) * pieceWidth
        val y = (index / piecesInRow) * pieceHeight
        image.getSubimage(x, y, pieceWidth, pieceHeight).toBitmap()
    }

    Box(modifier = Modifier.background(Color.LightGray)) {
        Column {
            for (y in 0 until piecesInRow) {
                Row {
                    for (x in 0 until piecesInRow) {
                        val currentIndex = y * piecesInRow + x // 计算全局索引
                        val pieceIndex = puzzleState.getPieceIndexAt(x, y)
                        Image(
                            bitmap = pieces[pieceIndex].asComposeImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp, 90.dp)
                                .background(if (selectedPieceIndex == currentIndex) Color.Gray else Color.Transparent)
                                .clickable {
                                    selectedPieceIndex = if (selectedPieceIndex == -1) {
                                        currentIndex
                                    } else {
                                        puzzleState.swapPieces(selectedPieceIndex, currentIndex)
                                        -1
                                    }
                                }
                        )
                    }
                }
            }
        }
        if (checkPuzzleComplete(puzzleState, difficulty)) {
            onPuzzleCompleted()
        }
    }
}

@Composable
fun Timer(time: Int, onTimeUpdated: (Int) -> Unit) {
    var newTime = time
    val updatedTime = rememberUpdatedState(newTime)

    LaunchedEffect(key1 = time) {
        while (updatedTime.value > 0) {
            delay(1000)
            newTime--
            onTimeUpdated(newTime)
        }
    }

    Text(text = "剩余时间：${updatedTime.value}秒")
}

class PuzzleState(difficulty: Int) {
    private val piecesInRow = kotlin.math.sqrt(difficulty.toDouble()).toInt()
    private val pieces = List(difficulty) { it }.shuffled().toMutableList()

    fun getPieceIndexAt(x: Int, y: Int): Int {
        return pieces[y * piecesInRow + x]
    }

    fun swapPieces(firstIndex: Int, secondIndex: Int) {
        val temp = pieces[firstIndex]
        pieces[firstIndex] = pieces[secondIndex]
        pieces[secondIndex] = temp
    }
}

fun checkPuzzleComplete(puzzleState: PuzzleState, difficulty: Int): Boolean {
    for (i in 0 until difficulty) {
        println(puzzleState.getPieceIndexAt(i % difficulty, i / difficulty))
        if (puzzleState.getPieceIndexAt(i % difficulty, i / difficulty) != i) {
            return false
        }
    }
    return true
}
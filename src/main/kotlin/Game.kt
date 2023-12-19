import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skiko.toBitmap
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

@Composable
fun PuzzleGame(image: BufferedImage) {
    var difficulty by remember { mutableStateOf(9) } //难度选择
    var isTimerRunning by remember { mutableStateOf(false) } //计时开始
    var remainingTime by remember { mutableStateOf(60) } //计时时间
    var isPuzzleComplete by remember { mutableStateOf(false) } //游戏成功
    var isPuzzleEnd by remember { mutableStateOf(false) } //游戏失败

    Column(
        modifier = Modifier.width(350.dp).padding(16.dp),
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

        //拼图游戏
        if (isTimerRunning) {
            PuzzleBoard(image, difficulty, onPuzzleCompleted = { isPuzzleComplete = true })
        } else {
            //预览图片
            Image(
                bitmap = image.toComposeImageBitmap(),
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

        Button(
            onClick = { isTimerRunning = !isTimerRunning },
            colors = ButtonDefaults.buttonColors(Color(0xff708090))
        ) {
            Text(if (isTimerRunning) "结束游戏" else "开始游戏")
        }

        //游戏成功
        if (isPuzzleComplete) {
            GameAlertDialog(title = "恭喜！", text = "游戏胜利！") {
                isPuzzleComplete = false
                isTimerRunning = false
                remainingTime = 60
            }
        }

        //游戏失败
        if (isPuzzleEnd) {
            GameAlertDialog(title = "很遗憾", text = "挑战失败") {
                isPuzzleEnd = false
                isTimerRunning = false
                remainingTime = 60
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameAlertDialog(title: String, text: String, onConfirm: () -> Unit) {
    AlertDialog(
        modifier = Modifier.width(280.dp).height(180.dp),
        onDismissRequest = {},
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(Color(0xff708090))) {
                Text("确定")
            }
        }
    )
}

//难度选择按钮
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

//拼图游戏生成
@Composable
fun PuzzleBoard(image: BufferedImage, difficulty: Int, onPuzzleCompleted: () -> Unit) {
    val piecesInRow = kotlin.math.sqrt(difficulty.toDouble()).toInt()
    val pieceWidth = image.width / piecesInRow
    val pieceHeight = image.height / piecesInRow

    val puzzleState by remember { mutableStateOf(PuzzleState(difficulty)) } //拼图片段
    var draggingPieceIndex by remember { mutableStateOf(-1) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    // 创建拼图片段
    val pieces = List(difficulty) { index ->
        val x = (index % piecesInRow) * pieceWidth
        val y = (index / piecesInRow) * pieceHeight
        image.getSubimage(x, y, pieceWidth, pieceHeight).toBitmap()
    }

    Box() {
        Column {
            for (y in 0 until piecesInRow) {
                Row {
                    for (x in 0 until piecesInRow) {
                        val currentIndex = y * piecesInRow + x
                        val pieceIndex = puzzleState.getPieceIndexAt(x, y)

                        Image(
                            bitmap = pieces[pieceIndex].asComposeImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .offset {
                                    if (draggingPieceIndex == currentIndex) {
                                        IntOffset(
                                            dragOffset.x.roundToInt(),
                                            dragOffset.y.roundToInt()
                                        )
                                    } else IntOffset.Zero
                                }
                                .pointerInput(pieces[pieceIndex]) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            draggingPieceIndex = currentIndex
                                        },
                                        onDrag = { change, dragAmount ->
                                            dragOffset += dragAmount
                                            change.consume()
                                        },
                                        onDragEnd = {
                                            puzzleState.updatePiecePosition(
                                                draggedIndex = draggingPieceIndex,
                                                dragOffset = dragOffset,
                                                pieceWidth = pieceWidth,
                                                pieceHeight = pieceHeight,
                                                piecesInRow = piecesInRow
                                            )
                                            draggingPieceIndex = -1
                                            dragOffset = Offset.Zero
                                        }
                                    )
                                }
                                .background(if (draggingPieceIndex == currentIndex) Color.Gray else Color.Transparent)
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

//计时器
@Composable
fun Timer(time: Int, onTimeUpdated: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val countDownTime = produceState(initialValue = time, producer = {
        while (value > 0) {
            delay(1000)
            value--
        }
    })
    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.launch {
                onTimeUpdated(countDownTime.value)
            }
        }
    }
    Text(text = "剩余时间：${countDownTime.value}秒")
}

//分割图片
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

    fun updatePiecePosition(draggedIndex: Int, dragOffset: Offset, pieceWidth: Int, pieceHeight: Int, piecesInRow: Int) {
        val draggedX = draggedIndex % piecesInRow
        val draggedY = draggedIndex / piecesInRow

        val deltaX = dragOffset.x / pieceWidth
        val deltaY = dragOffset.y / pieceHeight

        val targetX = when {
            deltaX > 0.05f -> (draggedX + 1).coerceIn(0, piecesInRow )
            deltaX < -0.05f -> (draggedX - 1).coerceIn(0, piecesInRow )
            else -> draggedX
        }

        val targetY = when {
            deltaY > 0.05f -> (draggedY + 1).coerceIn(0, piecesInRow )
            deltaY < -0.05f -> (draggedY - 1).coerceIn(0, piecesInRow )
            else -> draggedY
        }

        val targetIndex = targetY * piecesInRow + targetX

        // Swap only if the target index is different from the dragged index
        if (targetIndex != draggedIndex && targetIndex in pieces.indices) {
            swapPieces(draggedIndex, targetIndex)
        }
    }
}


//判断游戏是否成功
fun checkPuzzleComplete(puzzleState: PuzzleState, difficulty: Int): Boolean {
    for (i in 0 until difficulty) {
        //println(puzzleState.getPieceIndexAt(i % difficulty, i / difficulty))
        if (puzzleState.getPieceIndexAt(i % difficulty, i / difficulty) != i) {
            return false
        }
    }
    return true
}
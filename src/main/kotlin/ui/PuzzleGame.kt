package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import config.GameAlertDialog
import config.saveGameResult
import org.jetbrains.skiko.toBitmap
import java.awt.image.BufferedImage
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Composable
fun PuzzleGame(
    image: BufferedImage,
    difficulty: Int,
    isGameRunning: Boolean,
    remainingTime: String,
    onPuzzleCompleted: () -> Unit
) {
    var isPuzzleComplete by remember { mutableStateOf(false) } //游戏成功
    var isPuzzleEnd by remember { mutableStateOf(false) } //游戏失败

    Column(
        modifier = Modifier.width(350.dp).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //拼图游戏
        if (isGameRunning) {
            PuzzleBoard(image, difficulty, onPuzzleCompleted = { isPuzzleComplete = true })
        } else {
            //预览图片
            Image(
                bitmap = image.toComposeImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(350.dp)
            )
        }

        //游戏成功
        if (isPuzzleComplete) {
            val difficultyText = when (difficulty) {
                9 -> "简单"
                16 -> "普通"
                25 -> "高级"
                else -> "苦难"
            }

            // 获取当前时间
            val completionTime = LocalDateTime.now()

            // 保存游戏结果
            saveGameResult(difficultyText, 60 - remainingTime.toInt(), completionTime)

            GameAlertDialog(title = "恭喜！", text = "游戏胜利！") {
                isPuzzleComplete = false
                onPuzzleCompleted()
            }
        }

        //游戏失败
        if (isPuzzleEnd) {
            GameAlertDialog(title = "很遗憾", text = "挑战失败") {
                isPuzzleEnd = false
                onPuzzleCompleted()
            }
        }
    }
}

//拼图游戏生成
@Composable
fun PuzzleBoard(image: BufferedImage, difficulty: Int, onPuzzleCompleted: () -> Unit) {
    val piecesInRow = kotlin.math.sqrt(difficulty.toDouble()).toInt()
    val pieceWidth = image.width / piecesInRow
    val pieceHeight = image.height / piecesInRow

    val puzzleState by remember { mutableStateOf(PuzzleState(difficulty)) }
    var draggingPieceIndex by remember { mutableStateOf(-1) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

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
                                }.width((300 / piecesInRow).dp).height((300 / piecesInRow).dp)
                                .pointerInput(pieces[pieceIndex]) {
                                    detectDragGestures(
                                        onDragStart = {
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

//分割图片
class PuzzleState(difficulty: Int) {
    private val piecesInRow = kotlin.math.sqrt(difficulty.toDouble()).toInt()
    private val pieces = List(difficulty) { it }.shuffled().toMutableList()

    fun getPieceIndexAt(x: Int, y: Int): Int {
        return pieces[y * piecesInRow + x]
    }

    private fun swapPieces(firstIndex: Int, secondIndex: Int) {
        val temp = pieces[firstIndex]
        pieces[firstIndex] = pieces[secondIndex]
        pieces[secondIndex] = temp
    }

    fun updatePiecePosition(
        draggedIndex: Int,
        dragOffset: Offset,
        pieceWidth: Int,
        pieceHeight: Int,
        piecesInRow: Int
    ) {
        val draggedX = draggedIndex % piecesInRow
        val draggedY = draggedIndex / piecesInRow

        val deltaX = dragOffset.x / pieceWidth
        val deltaY = dragOffset.y / pieceHeight

        val targetX = when {
            deltaX > 0.05f -> (draggedX + 1).coerceIn(0, piecesInRow)
            deltaX < -0.05f -> (draggedX - 1).coerceIn(0, piecesInRow)
            else -> draggedX
        }

        val targetY = when {
            deltaY > 0.05f -> (draggedY + 1).coerceIn(0, piecesInRow)
            deltaY < -0.05f -> (draggedY - 1).coerceIn(0, piecesInRow)
            else -> draggedY
        }

        val targetIndex = targetY * piecesInRow + targetX

        if (targetIndex != draggedIndex && targetIndex in pieces.indices) {
            swapPieces(draggedIndex, targetIndex)
        }
    }
}

//判断游戏是否成功
fun checkPuzzleComplete(puzzleState: PuzzleState, difficulty: Int): Boolean {
    for (i in 0 until difficulty) {
        if (puzzleState.getPieceIndexAt(i % difficulty, i / difficulty) != i) {
            return false
        }
    }
    return true
}
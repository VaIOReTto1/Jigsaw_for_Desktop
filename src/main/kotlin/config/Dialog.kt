package config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 排行榜对话框
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
                    ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 6.dp)
            ) {
                Text("关闭")
            }
        }
    )
}

// 游戏规则对话框
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
                    ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 3.dp, pressedElevation = 6.dp)
            ) {
                Text("关闭")
            }
        }
    )
}

// 游戏弹窗
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
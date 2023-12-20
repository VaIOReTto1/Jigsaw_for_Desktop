package config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

fun parseDifficulty(difficulty: String): Int {
    return when (difficulty) {
        "简单" -> 9
        "普通" -> 16
        "困难" -> 25
        else -> 9
    }
}
package config

import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

fun saveGameResult(difficulty: String, time: Int, completionTime: LocalDateTime) {
    val url = "jdbc:sqlite:game_data.db"
    val sql = """
        INSERT INTO game_results (difficulty, time, completion_time) 
        VALUES (?, ?, ?)
    """.trimIndent()

    DriverManager.getConnection(url).use { conn ->
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, difficulty)
            pstmt.setInt(2, time)
            pstmt.setString(3, completionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            pstmt.executeUpdate()
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
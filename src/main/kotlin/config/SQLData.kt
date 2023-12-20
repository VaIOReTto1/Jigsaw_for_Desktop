package config

import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 连接数据库
fun initializeDatabase() {
    val url = "jdbc:sqlite:game_data.db" // 数据库路径
    val sql = """
        CREATE TABLE IF NOT EXISTS game_results (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            difficulty TEXT,
            time INTEGER,
            completion_time TEXT
        )
    """.trimIndent() // 创建表格

    // 创建数据库
    DriverManager.getConnection(url).use { conn ->
        conn.createStatement().use { stmt ->
            stmt.execute(sql) // 执行创建表格操作
        }
    }
}

// 清空数据库
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

// 保存游戏结果
fun saveGameResult(difficulty: String, time: Int, completionTime: LocalDateTime) {
    val url = "jdbc:sqlite:game_data.db"
    val sql = """
        INSERT INTO game_results (difficulty, time, completion_time) 
        VALUES (?, ?, ?)
    """.trimIndent()

    DriverManager.getConnection(url).use { conn ->
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, difficulty) // 难度
            pstmt.setInt(2, time) // 时间
            pstmt.setString(3, completionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) //完成时间
            pstmt.executeUpdate() // 执行插入操作
        }
    }
}

// 获取排行榜数据
fun getLeaderboardData(difficulty: String): List<GameResult> {
    val url = "jdbc:sqlite:game_data.db"
    val sql = """
        SELECT time, completion_time 
        FROM game_results 
        WHERE difficulty = ? 
        ORDER BY time, completion_time
    """.trimIndent()

    val leaderboardData = mutableListOf<GameResult>() // 排行榜数据

    DriverManager.getConnection(url).use { conn ->
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, difficulty) // 难度
            val rs = pstmt.executeQuery() // 执行查询操作
            var rank = 1 // 排名
            while (rs.next()) {
                val time = rs.getInt("time") // 时间
                val completionTime = rs.getString("completion_time") // 完成时间
                leaderboardData.add(GameResult(rank++, time, completionTime)) // 添加排行榜数据
            }
        }
    }

    return leaderboardData
}
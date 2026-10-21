package com.BigBon.bugs.database.dao

import androidx.room.*
import com.BigBon.bugs.database.entities.GameRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {
    @Query("""
        SELECT gr.*, u.username FROM game_records gr
        INNER JOIN users u ON gr.userId = u.id
        WHERE gr.difficulty = :difficulty
        ORDER BY gr.score DESC
        LIMIT :limit
    """)
    fun getTopRecordsByDifficulty(difficulty: String, limit: Int = 10): Flow<List<RecordWithUsername>>

    @Query("""
        SELECT gr.*, u.username FROM game_records gr
        INNER JOIN users u ON gr.userId = u.id
        WHERE gr.userId = :userId
        ORDER BY gr.score DESC
        LIMIT :limit
    """)
    fun getUserTopRecords(userId: Long, limit: Int = 10): Flow<List<RecordWithUsername>>

    @Query("""
        SELECT gr.*, u.username FROM game_records gr
        INNER JOIN users u ON gr.userId = u.id
        WHERE gr.userId = :userId AND gr.difficulty = :difficulty
        ORDER BY gr.score DESC
        LIMIT 1
    """)
    suspend fun getUserBestRecordForDifficulty(userId: Long, difficulty: String): RecordWithUsername?

    @Insert
    suspend fun insertRecord(record: GameRecord): Long

    @Query("DELETE FROM game_records WHERE userId = :userId")
    suspend fun deleteUserRecords(userId: Long)
}

data class RecordWithUsername(
    val id: Long,
    val userId: Long,
    val score: Int,
    val hits: Int,
    val misses: Int,
    val difficulty: String,
    val timestamp: Long,
    val username: String
)

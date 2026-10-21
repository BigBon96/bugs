package com.BigBon.bugs.repository

import com.BigBon.bugs.database.dao.GameRecordDao
import com.BigBon.bugs.database.dao.RecordWithUsername
import com.BigBon.bugs.database.dao.UserDao
import com.BigBon.bugs.database.entities.GameRecord
import com.BigBon.bugs.database.entities.User
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val userDao: UserDao,
    private val gameRecordDao: GameRecordDao
) {
    // User operations
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)

    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)

    suspend fun createUser(username: String): Long {
        val user = User(username = username)
        return userDao.insertUser(user)
    }

    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun getUserCount(): Int = userDao.getUserCount()

    // Game record operations
    fun getTopRecordsByDifficulty(difficulty: String, limit: Int = 10): Flow<List<RecordWithUsername>> =
        gameRecordDao.getTopRecordsByDifficulty(difficulty, limit)

    fun getUserTopRecords(userId: Long, limit: Int = 10): Flow<List<RecordWithUsername>> =
        gameRecordDao.getUserTopRecords(userId, limit)

    suspend fun getUserBestRecordForDifficulty(userId: Long, difficulty: String): RecordWithUsername? =
        gameRecordDao.getUserBestRecordForDifficulty(userId, difficulty)

    suspend fun saveGameRecord(
        userId: Long,
        score: Int,
        hits: Int,
        misses: Int,
        difficulty: String
    ): Long {
        val record = GameRecord(
            userId = userId,
            score = score,
            hits = hits,
            misses = misses,
            difficulty = difficulty
        )
        return gameRecordDao.insertRecord(record)
    }

    suspend fun deleteUserRecords(userId: Long) = gameRecordDao.deleteUserRecords(userId)
}

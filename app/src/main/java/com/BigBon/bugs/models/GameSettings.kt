package com.BigBon.bugs.models

import android.content.Context
import android.content.SharedPreferences

/**
 * Настройки игры
 */
data class GameSettings(
    val speedMultiplier: Float = 1.0f,      // Множитель скорости (0.5 - 2.0)
    val maxBugs: Int = 5,                    // Количество жуков на экране (3 - 15)
    val roundDuration: Long = 60000L,        // Длительность раунда в мс (30000 - 120000)
    val missClickPenalty: Int = 5            // Штраф за промах
) {
    /**
     * Определяет уровень сложности на основе настроек
     */
    fun getDifficulty(): String {
        val difficultyScore = (speedMultiplier * 2) + (maxBugs / 5.0f) + (missClickPenalty / 10.0f)

        return when {
            difficultyScore < 3.0f -> "easy"
            difficultyScore < 5.0f -> "medium"
            else -> "hard"
        }
    }

    fun getDifficultyName(): String {
        return when (getDifficulty()) {
            "easy" -> "Легко"
            "medium" -> "Средне"
            "hard" -> "Сложно"
            else -> "Неизвестно"
        }
    }

    companion object {
        private const val PREFS_NAME = "game_settings"
        private const val KEY_SPEED_MULTIPLIER = "speed_multiplier"
        private const val KEY_MAX_BUGS = "max_bugs"
        private const val KEY_ROUND_DURATION = "round_duration"
        private const val KEY_MISS_PENALTY = "miss_penalty"

        /**
         * Загрузить настройки из SharedPreferences
         */
        fun load(context: Context): GameSettings {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return GameSettings(
                speedMultiplier = prefs.getFloat(KEY_SPEED_MULTIPLIER, 1.0f),
                maxBugs = prefs.getInt(KEY_MAX_BUGS, 5),
                roundDuration = prefs.getLong(KEY_ROUND_DURATION, 60000L),
                missClickPenalty = prefs.getInt(KEY_MISS_PENALTY, 5)
            )
        }

        /**
         * Сохранить настройки в SharedPreferences
         */
        fun save(context: Context, settings: GameSettings) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putFloat(KEY_SPEED_MULTIPLIER, settings.speedMultiplier)
                putInt(KEY_MAX_BUGS, settings.maxBugs)
                putLong(KEY_ROUND_DURATION, settings.roundDuration)
                putInt(KEY_MISS_PENALTY, settings.missClickPenalty)
                apply()
            }
        }
    }
}

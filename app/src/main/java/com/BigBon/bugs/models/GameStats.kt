package com.BigBon.bugs.models

/**
 * Статистика игры
 * @param score общий счет
 * @param hits количество попаданий
 * @param misses количество промахов
 * @param accuracy точность (%)
 */
data class GameStats(
    val score: Int = 0,
    val hits: Int = 0,
    val misses: Int = 0
) {
    val accuracy: Float
        get() = if (hits + misses > 0) {
            (hits.toFloat() / (hits + misses)) * 100
        } else {
            0f
        }
}

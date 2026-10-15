package com.BigBon.bugs.game

import com.BigBon.bugs.models.Bug
import com.BigBon.bugs.models.BugType
import kotlin.random.Random

/**
 * Фабрика для создания жуков разных типов
 */
object BugFactory {

    // Настройки для обычного жука
    private const val NORMAL_SIZE = 50f
    private const val NORMAL_SPEED = 200f
    private const val NORMAL_POINTS = 10

    // Настройки для быстрого жука
    private const val FAST_SIZE = 35f
    private const val FAST_SPEED = 400f
    private const val FAST_POINTS = 20

    // Настройки для редкого жука
    private const val RARE_SIZE = 70f
    private const val RARE_SPEED = 150f
    private const val RARE_POINTS = 50

    /**
     * Создать жука указанного типа
     */
    fun createBug(type: BugType, screenWidth: Float, screenHeight: Float): Bug {
        val x = Random.nextFloat() * screenWidth
        val y = Random.nextFloat() * screenHeight

        val speedX = (Random.nextFloat() - 0.5f) * 2
        val speedY = (Random.nextFloat() - 0.5f) * 2

        return when (type) {
            BugType.NORMAL -> Bug(
                x = x,
                y = y,
                speedX = speedX * NORMAL_SPEED,
                speedY = speedY * NORMAL_SPEED,
                size = NORMAL_SIZE,
                type = BugType.NORMAL,
                points = NORMAL_POINTS
            )
            BugType.FAST -> Bug(
                x = x,
                y = y,
                speedX = speedX * FAST_SPEED,
                speedY = speedY * FAST_SPEED,
                size = FAST_SIZE,
                type = BugType.FAST,
                points = FAST_POINTS
            )
            BugType.RARE -> Bug(
                x = x,
                y = y,
                speedX = speedX * RARE_SPEED,
                speedY = speedY * RARE_SPEED,
                size = RARE_SIZE,
                type = BugType.RARE,
                points = RARE_POINTS
            )
        }
    }

    /**
     * Создать случайного жука (с учетом вероятности появления редких)
     */
    fun createRandomBug(screenWidth: Float, screenHeight: Float): Bug {
        val random = Random.nextFloat()
        val type = when {
            random < 0.1f -> BugType.RARE     // 10% шанс редкого жука
            random < 0.4f -> BugType.FAST     // 30% шанс быстрого жука
            else -> BugType.NORMAL             // 60% шанс обычного жука
        }
        return createBug(type, screenWidth, screenHeight)
    }
}

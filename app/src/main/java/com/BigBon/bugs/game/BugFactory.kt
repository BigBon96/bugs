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
    fun createBug(type: BugType, screenWidth: Float, screenHeight: Float, speedMultiplier: Float = 1.0f): Bug {
        val size = when (type) {
            BugType.NORMAL -> NORMAL_SIZE
            BugType.FAST -> FAST_SIZE
            BugType.RARE -> RARE_SIZE
        }

        // Безопасная зона от краев с учетом размера жука
        val margin = size * 2
        val safeWidth = screenWidth - 2 * margin
        val safeHeight = screenHeight - 2 * margin

        // Позиция с учетом безопасной зоны
        val x = if (safeWidth > 0) Random.nextFloat() * safeWidth + margin else screenWidth / 2
        val y = if (safeHeight > 0) Random.nextFloat() * safeHeight + margin else screenHeight / 2

        // Генерируем случайное направление (угол от 0 до 360 градусов)
        val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
        val speedX = Math.cos(angle.toDouble()).toFloat()
        val speedY = Math.sin(angle.toDouble()).toFloat()

        return when (type) {
            BugType.NORMAL -> Bug(
                x = x,
                y = y,
                speedX = speedX * NORMAL_SPEED * speedMultiplier,
                speedY = speedY * NORMAL_SPEED * speedMultiplier,
                size = NORMAL_SIZE,
                type = BugType.NORMAL,
                points = NORMAL_POINTS
            )
            BugType.FAST -> Bug(
                x = x,
                y = y,
                speedX = speedX * FAST_SPEED * speedMultiplier,
                speedY = speedY * FAST_SPEED * speedMultiplier,
                size = FAST_SIZE,
                type = BugType.FAST,
                points = FAST_POINTS
            )
            BugType.RARE -> Bug(
                x = x,
                y = y,
                speedX = speedX * RARE_SPEED * speedMultiplier,
                speedY = speedY * RARE_SPEED * speedMultiplier,
                size = RARE_SIZE,
                type = BugType.RARE,
                points = RARE_POINTS
            )
        }
    }

    /**
     * Создать случайного жука (с учетом вероятности появления редких)
     */
    fun createRandomBug(screenWidth: Float, screenHeight: Float, speedMultiplier: Float = 1.0f): Bug {
        val random = Random.nextFloat()
        val type = when {
            random < 0.1f -> BugType.RARE     // 10% шанс редкого жука
            random < 0.4f -> BugType.FAST     // 30% шанс быстрого жука
            else -> BugType.NORMAL             // 60% шанс обычного жука
        }
        return createBug(type, screenWidth, screenHeight, speedMultiplier)
    }
}

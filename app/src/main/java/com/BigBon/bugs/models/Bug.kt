package com.BigBon.bugs.models

import java.util.UUID

/**
 * Игровая сущность - Жук
 * @param id уникальный идентификатор
 * @param x позиция по горизонтали
 * @param y позиция по вертикали
 * @param speedX скорость по горизонтали (пикселей в секунду)
 * @param speedY скорость по вертикали (пикселей в секунду)
 * @param size размер жука (радиус)
 * @param type тип жука (NORMAL, FAST, RARE)
 * @param points стоимость в очках
 */
data class Bug(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    var speedX: Float,
    var speedY: Float,
    val size: Float,
    val type: BugType,
    val points: Int
)

enum class BugType {
    NORMAL,  // Обычный жук
    FAST,    // Быстрый жук
    RARE     // Редкий жук
}

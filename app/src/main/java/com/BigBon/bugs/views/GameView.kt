package com.BigBon.bugs.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.BigBon.bugs.models.Bug
import com.BigBon.bugs.models.BugType

/**
 * View для отрисовки игрового поля с жуками
 */
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bugs = mutableListOf<Bug>()
    private val bugsLock = Any()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var onBugClickListener: ((Bug) -> Unit)? = null
    var onMissClickListener: (() -> Unit)? = null

    init {
        paint.style = Paint.Style.FILL
    }

    fun setBugs(newBugs: List<Bug>) {
        synchronized(bugsLock) {
            bugs.clear()
            bugs.addAll(newBugs)
        }
        invalidate()
    }

    fun addBug(bug: Bug) {
        synchronized(bugsLock) {
            bugs.add(bug)
        }
        invalidate()
    }

    fun removeBug(bug: Bug) {
        synchronized(bugsLock) {
            bugs.remove(bug)
        }
        invalidate()
    }

    fun clearBugs() {
        synchronized(bugsLock) {
            bugs.clear()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Создаем копию списка для безопасной отрисовки
        val bugsCopy = synchronized(bugsLock) { bugs.toList() }

        bugsCopy.forEach { bug ->
            try {
                // Цвет жука зависит от типа
                paint.color = when (bug.type) {
                    BugType.NORMAL -> Color.rgb(139, 69, 19) // Коричневый
                    BugType.FAST -> Color.rgb(255, 165, 0)    // Оранжевый
                    BugType.RARE -> Color.rgb(255, 215, 0)    // Золотой
                }
                paint.style = Paint.Style.FILL

                // Рисуем тело жука
                canvas.drawCircle(bug.x, bug.y, bug.size, paint)

                // Рисуем усики
                paint.color = Color.BLACK
                paint.strokeWidth = 3f
                canvas.drawLine(
                    bug.x - bug.size * 0.3f,
                    bug.y - bug.size * 0.3f,
                    bug.x - bug.size * 0.7f,
                    bug.y - bug.size * 0.9f,
                    paint
                )
                canvas.drawLine(
                    bug.x + bug.size * 0.3f,
                    bug.y - bug.size * 0.3f,
                    bug.x + bug.size * 0.7f,
                    bug.y - bug.size * 0.9f,
                    paint
                )

                // Рисуем глаза
                paint.color = Color.WHITE
                paint.style = Paint.Style.FILL
                canvas.drawCircle(
                    bug.x - bug.size * 0.25f,
                    bug.y,
                    bug.size * 0.15f,
                    paint
                )
                canvas.drawCircle(
                    bug.x + bug.size * 0.25f,
                    bug.y,
                    bug.size * 0.15f,
                    paint
                )

                // Зрачки
                paint.color = Color.BLACK
                canvas.drawCircle(
                    bug.x - bug.size * 0.25f,
                    bug.y,
                    bug.size * 0.08f,
                    paint
                )
                canvas.drawCircle(
                    bug.x + bug.size * 0.25f,
                    bug.y,
                    bug.size * 0.08f,
                    paint
                )
            } catch (e: Exception) {
                // Игнорируем ошибки отрисовки отдельных жуков
                e.printStackTrace()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y

            // Проверяем, попали ли мы в жука
            // Создаем копию списка для безопасной итерации
            var bugClicked = false
            var clickedBug: Bug? = null

            synchronized(bugsLock) {
                bugs.toList().forEach { bug ->
                    val distance = Math.sqrt(
                        ((touchX - bug.x) * (touchX - bug.x) + (touchY - bug.y) * (touchY - bug.y)).toDouble()
                    ).toFloat()

                    if (distance <= bug.size && !bugClicked) {
                        clickedBug = bug
                        bugClicked = true
                    }
                }
            }

            // Вызываем колбэк после завершения итерации
            if (bugClicked && clickedBug != null) {
                onBugClickListener?.invoke(clickedBug!!)
            } else {
                onMissClickListener?.invoke()
            }

            return true
        }
        return super.onTouchEvent(event)
    }
}

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
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var onBugClickListener: ((Bug) -> Unit)? = null
    var onMissClickListener: (() -> Unit)? = null

    init {
        paint.style = Paint.Style.FILL
    }

    fun setBugs(newBugs: List<Bug>) {
        bugs.clear()
        bugs.addAll(newBugs)
        invalidate()
    }

    fun addBug(bug: Bug) {
        bugs.add(bug)
        invalidate()
    }

    fun removeBug(bug: Bug) {
        bugs.remove(bug)
        invalidate()
    }

    fun clearBugs() {
        bugs.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bugs.forEach { bug ->
            // Цвет жука зависит от типа
            paint.color = when (bug.type) {
                BugType.NORMAL -> Color.rgb(139, 69, 19) // Коричневый
                BugType.FAST -> Color.rgb(255, 165, 0)    // Оранжевый
                BugType.RARE -> Color.rgb(255, 215, 0)    // Золотой
            }

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

            paint.style = Paint.Style.FILL
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y

            // Проверяем, попали ли мы в жука
            var bugClicked = false
            bugs.forEach { bug ->
                val distance = Math.sqrt(
                    ((touchX - bug.x) * (touchX - bug.x) + (touchY - bug.y) * (touchY - bug.y)).toDouble()
                ).toFloat()

                if (distance <= bug.size) {
                    onBugClickListener?.invoke(bug)
                    bugClicked = true
                    return@forEach
                }
            }

            // Если не попали ни в одного жука, это промах
            if (!bugClicked) {
                onMissClickListener?.invoke()
            }

            return true
        }
        return super.onTouchEvent(event)
    }
}

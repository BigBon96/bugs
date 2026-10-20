package com.BigBon.bugs.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.BigBon.bugs.R
import com.BigBon.bugs.game.BugFactory
import com.BigBon.bugs.models.Bug
import com.BigBon.bugs.models.GameSettings
import com.BigBon.bugs.models.GameStats
import com.BigBon.bugs.views.GameView
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Фрагмент с игровым процессом
 */
class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private lateinit var tvScore: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvHits: TextView
    private lateinit var tvMisses: TextView

    private val bugs = CopyOnWriteArrayList<Bug>()
    private var score = 0
    private var hits = 0
    private var misses = 0
    private var isGameRunning = false

    private var gameTimer: CountDownTimer? = null
    private var updateTimer: CountDownTimer? = null

    private lateinit var gameSettings: GameSettings
    private var remainingTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameView = view.findViewById(R.id.gameView)
        tvScore = view.findViewById(R.id.tvScore)
        tvTime = view.findViewById(R.id.tvTime)
        tvHits = view.findViewById(R.id.tvHits)
        tvMisses = view.findViewById(R.id.tvMisses)

        setupGameView()
        startGame()
    }

    override fun onResume() {
        super.onResume()
        // Перезагружаем настройки при возвращении на фрагмент
        gameSettings = GameSettings.load(requireContext())
    }

    private fun setupGameView() {
        gameView.onBugClickListener = { bug ->
            onBugClicked(bug)
        }

        gameView.onMissClickListener = {
            onMissClick()
        }
    }

    private fun startGame() {
        // Загружаем актуальные настройки перед началом игры
        gameSettings = GameSettings.load(requireContext())
        remainingTime = gameSettings.roundDuration

        score = 0
        hits = 0
        misses = 0
        bugs.clear()
        isGameRunning = true

        updateUI()
        startTimers()
    }

    private fun startTimers() {
        // Таймер игры
        gameTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                val secondsLeft = millisUntilFinished / 1000
                tvTime.text = "Время: ${secondsLeft}с"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()

        // Таймер обновления позиций жуков и автоспавна
        updateTimer = object : CountDownTimer(remainingTime, 16) { // ~60 FPS
            override fun onTick(millisUntilFinished: Long) {
                updateBugs()
                // Проверяем и спавним жуков до нужного количества
                maintainBugCount()
            }

            override fun onFinish() {
                // Игра закончена
            }
        }.start()
    }

    private fun maintainBugCount() {
        val viewWidth = gameView.width
        val viewHeight = gameView.height

        // Добавляем жуков до нужного количества
        if (viewWidth > 0 && viewHeight > 0) {
            while (bugs.size < gameSettings.maxBugs) {
                val bug = BugFactory.createRandomBug(
                    viewWidth.toFloat(),
                    viewHeight.toFloat(),
                    gameSettings.speedMultiplier
                )
                bugs.add(bug)
            }
            if (bugs.isNotEmpty()) {
                gameView.setBugs(bugs)
            }
        }
    }

    private fun updateBugs() {
        val deltaTime = 0.016f // примерно 1/60 секунды
        val viewWidth = gameView.width.toFloat()
        val viewHeight = gameView.height.toFloat()

        // Проверка на валидные размеры экрана
        if (viewWidth <= 0 || viewHeight <= 0) return

        bugs.forEach { bug ->
            // Обновляем позицию
            bug.x += bug.speedX * deltaTime
            bug.y += bug.speedY * deltaTime

            // Отскок от границ с сохранением энергии
            // Небольшая рандомизация угла для предотвращения зацикливания
            val bounceRandomness = 0.15f

            if (bug.x - bug.size <= 0) {
                bug.x = bug.size
                // Отражаем горизонтальную скорость
                bug.speedX = Math.abs(bug.speedX)
                // Добавляем небольшое изменение вертикальной скорости
                bug.speedY += bug.speedY * (Math.random().toFloat() - 0.5f) * 2 * bounceRandomness
            } else if (bug.x + bug.size >= viewWidth) {
                bug.x = viewWidth - bug.size
                bug.speedX = -Math.abs(bug.speedX)
                bug.speedY += bug.speedY * (Math.random().toFloat() - 0.5f) * 2 * bounceRandomness
            }

            if (bug.y - bug.size <= 0) {
                bug.y = bug.size
                bug.speedY = Math.abs(bug.speedY)
                bug.speedX += bug.speedX * (Math.random().toFloat() - 0.5f) * 2 * bounceRandomness
            } else if (bug.y + bug.size >= viewHeight) {
                bug.y = viewHeight - bug.size
                bug.speedY = -Math.abs(bug.speedY)
                bug.speedX += bug.speedX * (Math.random().toFloat() - 0.5f) * 2 * bounceRandomness
            }

            // Обработка углов - если жук застрял в углу более 2 секунд
            val cornerThreshold = bug.size * 2f
            val isNearLeftEdge = bug.x - bug.size <= cornerThreshold
            val isNearRightEdge = bug.x + bug.size >= viewWidth - cornerThreshold
            val isNearTopEdge = bug.y - bug.size <= cornerThreshold
            val isNearBottomEdge = bug.y + bug.size >= viewHeight - cornerThreshold

            val isInCorner = (isNearLeftEdge || isNearRightEdge) && (isNearTopEdge || isNearBottomEdge)

            if (isInCorner) {
                // Получаем текущую скорость
                val currentSpeed = Math.sqrt((bug.speedX * bug.speedX + bug.speedY * bug.speedY).toDouble()).toFloat()

                // Создаем новое направление от угла к центру с добавлением случайности
                val centerX = viewWidth / 2
                val centerY = viewHeight / 2
                val dirX = centerX - bug.x
                val dirY = centerY - bug.y
                val dirLength = Math.sqrt((dirX * dirX + dirY * dirY).toDouble()).toFloat()

                if (dirLength > 0) {
                    // Нормализуем направление
                    val normalizedDirX = dirX / dirLength
                    val normalizedDirY = dirY / dirLength

                    // Добавляем случайный компонент
                    val randomAngle = (Math.random().toFloat() - 0.5f) * Math.PI.toFloat() / 2
                    val cosAngle = Math.cos(randomAngle.toDouble()).toFloat()
                    val sinAngle = Math.sin(randomAngle.toDouble()).toFloat()

                    // Применяем вращение
                    bug.speedX = (normalizedDirX * cosAngle - normalizedDirY * sinAngle) * currentSpeed
                    bug.speedY = (normalizedDirX * sinAngle + normalizedDirY * cosAngle) * currentSpeed
                }
            }

            // Нормализация скорости для предотвращения чрезмерного ускорения
            val currentSpeed = Math.sqrt((bug.speedX * bug.speedX + bug.speedY * bug.speedY).toDouble()).toFloat()
            val baseSpeed = when (bug.type) {
                com.BigBon.bugs.models.BugType.NORMAL -> 200f
                com.BigBon.bugs.models.BugType.FAST -> 400f
                com.BigBon.bugs.models.BugType.RARE -> 150f
            } * gameSettings.speedMultiplier

            // Если скорость отклонилась более чем на 20% от базовой, корректируем
            if (currentSpeed > 0 && Math.abs(currentSpeed - baseSpeed) > baseSpeed * 0.2f) {
                val scale = baseSpeed / currentSpeed
                bug.speedX *= scale
                bug.speedY *= scale
            }
        }

        gameView.setBugs(bugs)
    }

    private fun onBugClicked(bug: Bug) {
        if (!isGameRunning) return

        score += bug.points
        hits++
        // Используем безопасное удаление
        bugs.removeAll { it.id == bug.id }
        gameView.setBugs(bugs)
        updateUI()
    }

    private fun onMissClick() {
        if (!isGameRunning) return

        misses++
        // Штраф за промах
        score = Math.max(0, score - gameSettings.missClickPenalty)
        updateUI()
    }

    private fun updateUI() {
        tvScore.text = "Очки: $score"
        tvHits.text = "Попадания: $hits"
        tvMisses.text = "Промахи: $misses"
    }

    private fun endGame() {
        isGameRunning = false
        gameTimer?.cancel()
        updateTimer?.cancel()

        val stats = GameStats(score, hits, misses)
        showResults(stats)
    }

    private fun showResults(stats: GameStats) {
        // Показываем диалог с результатами
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Игра окончена!")
            .setMessage(
                "Очки: ${stats.score}\n" +
                "Попадания: ${stats.hits}\n" +
                "Промахи: ${stats.misses}\n" +
                "Точность: ${if (stats.hits + stats.misses > 0)
                    String.format("%.1f%%", stats.hits.toFloat() / (stats.hits + stats.misses) * 100)
                    else "0%"}"
            )
            .setPositiveButton("Новая игра") { _, _ ->
                restartGame()
            }
            .setNegativeButton("Выход") { _, _ ->
                // Возвращаемся на первую вкладку
                val viewPager = requireActivity().findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)
                viewPager?.currentItem = 0
            }
            .setCancelable(false)
            .create()

        dialog.show()
    }

    private fun restartGame() {
        // Очищаем состояние
        bugs.clear()
        score = 0
        hits = 0
        misses = 0

        // Перезапускаем игру
        startGame()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameTimer?.cancel()
        updateTimer?.cancel()
    }

    companion object {
        fun newInstance() = GameFragment()
    }
}

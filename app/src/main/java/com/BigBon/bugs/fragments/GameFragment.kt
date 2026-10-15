package com.BigBon.bugs.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.BigBon.bugs.R
import com.BigBon.bugs.game.BugFactory
import com.BigBon.bugs.models.Bug
import com.BigBon.bugs.models.GameStats
import com.BigBon.bugs.views.GameView

/**
 * Фрагмент с игровым процессом
 */
class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private lateinit var tvScore: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvHits: TextView
    private lateinit var tvMisses: TextView
    private lateinit var btnPause: Button

    private val bugs = mutableListOf<Bug>()
    private var score = 0
    private var hits = 0
    private var misses = 0
    private var isGameRunning = false

    private var gameTimer: CountDownTimer? = null
    private var bugSpawnTimer: CountDownTimer? = null
    private var updateTimer: CountDownTimer? = null

    // Настройки игры (можно будет загружать из SettingsFragment)
    private var gameDuration = 60000L // 60 секунд
    private var bugSpawnInterval = 2000L // 2 секунды
    private var maxBugs = 5

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
        btnPause = view.findViewById(R.id.btnPause)

        setupGameView()
        btnPause.setOnClickListener {
            if (isGameRunning) {
                pauseGame()
            } else {
                resumeGame()
            }
        }

        startGame()
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
        score = 0
        hits = 0
        misses = 0
        bugs.clear()
        isGameRunning = true

        updateUI()

        // Таймер игры
        gameTimer = object : CountDownTimer(gameDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tvTime.text = "Время: ${secondsLeft}с"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()

        // Таймер появления жуков
        bugSpawnTimer = object : CountDownTimer(gameDuration, bugSpawnInterval) {
            override fun onTick(millisUntilFinished: Long) {
                spawnBug()
            }

            override fun onFinish() {
                // Игра закончена
            }
        }.start()

        // Таймер обновления позиций жуков
        updateTimer = object : CountDownTimer(gameDuration, 16) { // ~60 FPS
            override fun onTick(millisUntilFinished: Long) {
                updateBugs()
            }

            override fun onFinish() {
                // Игра закончена
            }
        }.start()
    }

    private fun spawnBug() {
        if (bugs.size < maxBugs) {
            val bug = BugFactory.createRandomBug(
                gameView.width.toFloat(),
                gameView.height.toFloat()
            )
            bugs.add(bug)
            gameView.setBugs(bugs)
        }
    }

    private fun updateBugs() {
        val deltaTime = 0.016f // примерно 1/60 секунды

        bugs.forEach { bug ->
            bug.x += bug.speedX * deltaTime
            bug.y += bug.speedY * deltaTime

            // Отскок от границ
            if (bug.x - bug.size < 0 || bug.x + bug.size > gameView.width) {
                bug.x = bug.x.coerceIn(bug.size, gameView.width - bug.size)
            }
            if (bug.y - bug.size < 0 || bug.y + bug.size > gameView.height) {
                bug.y = bug.y.coerceIn(bug.size, gameView.height - bug.size)
            }
        }

        gameView.setBugs(bugs)
    }

    private fun onBugClicked(bug: Bug) {
        score += bug.points
        hits++
        bugs.remove(bug)
        gameView.setBugs(bugs)
        updateUI()
    }

    private fun onMissClick() {
        misses++
        updateUI()
    }

    private fun updateUI() {
        tvScore.text = "Очки: $score"
        tvHits.text = "Попадания: $hits"
        tvMisses.text = "Промахи: $misses"
    }

    private fun pauseGame() {
        isGameRunning = false
        gameTimer?.cancel()
        bugSpawnTimer?.cancel()
        updateTimer?.cancel()
        btnPause.text = "Продолжить"
    }

    private fun resumeGame() {
        isGameRunning = true
        // Здесь нужно пересоздать таймеры с оставшимся временем
        btnPause.text = "Пауза"
    }

    private fun endGame() {
        isGameRunning = false
        gameTimer?.cancel()
        bugSpawnTimer?.cancel()
        updateTimer?.cancel()

        val stats = GameStats(score, hits, misses)
        showResults(stats)
    }

    private fun showResults(stats: GameStats) {
        // Переход на экран результатов
        val resultsFragment = ResultsFragment.newInstance(stats)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, resultsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameTimer?.cancel()
        bugSpawnTimer?.cancel()
        updateTimer?.cancel()
    }

    companion object {
        fun newInstance() = GameFragment()
    }
}

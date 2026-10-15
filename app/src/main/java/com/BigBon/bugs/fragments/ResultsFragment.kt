package com.BigBon.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.BigBon.bugs.R
import com.BigBon.bugs.models.GameStats

/**
 * Фрагмент с результатами игры
 */
class ResultsFragment : Fragment() {

    private lateinit var tvFinalScore: TextView
    private lateinit var tvFinalHits: TextView
    private lateinit var tvFinalMisses: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var btnPlayAgain: Button
    private lateinit var btnMainMenu: Button

    private var gameStats: GameStats? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val score = it.getInt(ARG_SCORE)
            val hits = it.getInt(ARG_HITS)
            val misses = it.getInt(ARG_MISSES)
            gameStats = GameStats(score, hits, misses)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvFinalScore = view.findViewById(R.id.tvFinalScore)
        tvFinalHits = view.findViewById(R.id.tvFinalHits)
        tvFinalMisses = view.findViewById(R.id.tvFinalMisses)
        tvAccuracy = view.findViewById(R.id.tvAccuracy)
        btnPlayAgain = view.findViewById(R.id.btnPlayAgain)
        btnMainMenu = view.findViewById(R.id.btnMainMenu)

        displayResults()

        btnPlayAgain.setOnClickListener {
            startNewGame()
        }

        btnMainMenu.setOnClickListener {
            returnToMainMenu()
        }
    }

    private fun displayResults() {
        gameStats?.let { stats ->
            tvFinalScore.text = "Очки: ${stats.score}"
            tvFinalHits.text = "Попадания: ${stats.hits}"
            tvFinalMisses.text = "Промахи: ${stats.misses}"
            tvAccuracy.text = String.format("Точность: %.1f%%", stats.accuracy)
        }
    }

    private fun startNewGame() {
        val gameFragment = GameFragment.newInstance()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, gameFragment)
            .commit()
    }

    private fun returnToMainMenu() {
        parentFragmentManager.popBackStack()
    }

    companion object {
        private const val ARG_SCORE = "score"
        private const val ARG_HITS = "hits"
        private const val ARG_MISSES = "misses"

        fun newInstance(stats: GameStats) = ResultsFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_SCORE, stats.score)
                putInt(ARG_HITS, stats.hits)
                putInt(ARG_MISSES, stats.misses)
            }
        }
    }
}

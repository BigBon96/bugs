package com.BigBon.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.BigBon.bugs.R
import com.BigBon.bugs.models.GameSettings

class SettingsFragment : Fragment() {

    private lateinit var seekBarSpeed: SeekBar
    private lateinit var textViewSpeedValue: TextView
    private lateinit var seekBarMaxBugs: SeekBar
    private lateinit var textViewMaxBugsValue: TextView
    private lateinit var seekBarRoundDuration: SeekBar
    private lateinit var textViewRoundDurationValue: TextView
    private lateinit var seekBarMissPenalty: SeekBar
    private lateinit var textViewMissPenaltyValue: TextView
    private lateinit var btnSaveSettings: Button
    private lateinit var btnResetDefaults: Button

    private var currentSettings: GameSettings = GameSettings()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        loadSettings()
        setupSeekBars()
        setupButtons()
    }

    private fun initViews(view: View) {
        seekBarSpeed = view.findViewById(R.id.seekBarSpeed)
        textViewSpeedValue = view.findViewById(R.id.textViewSpeedValue)
        seekBarMaxBugs = view.findViewById(R.id.seekBarMaxBugs)
        textViewMaxBugsValue = view.findViewById(R.id.textViewMaxBugsValue)
        seekBarRoundDuration = view.findViewById(R.id.seekBarRoundDuration)
        textViewRoundDurationValue = view.findViewById(R.id.textViewRoundDurationValue)
        seekBarMissPenalty = view.findViewById(R.id.seekBarMissPenalty)
        textViewMissPenaltyValue = view.findViewById(R.id.textViewMissPenaltyValue)
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings)
        btnResetDefaults = view.findViewById(R.id.btnResetDefaults)
    }

    private fun loadSettings() {
        currentSettings = GameSettings.load(requireContext())

        // Скорость: от 50% до 200% (0.5 - 2.0)
        seekBarSpeed.progress = ((currentSettings.speedMultiplier - 0.5f) * 100).toInt()
        textViewSpeedValue.text = "${(currentSettings.speedMultiplier * 100).toInt()}%"

        // Количество жуков: от 3 до 15
        seekBarMaxBugs.progress = currentSettings.maxBugs - 3
        textViewMaxBugsValue.text = currentSettings.maxBugs.toString()

        // Длительность раунда: от 30 до 120 секунд
        seekBarRoundDuration.progress = (currentSettings.roundDuration / 1000 - 30).toInt()
        textViewRoundDurationValue.text = "${currentSettings.roundDuration / 1000} сек"

        // Штраф за промах: от 0 до 20
        seekBarMissPenalty.progress = currentSettings.missClickPenalty
        textViewMissPenaltyValue.text = currentSettings.missClickPenalty.toString()
    }

    private fun setupSeekBars() {
        // Скорость (50% - 200%)
        seekBarSpeed.max = 150 // (2.0 - 0.5) * 100
        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speedMultiplier = 0.5f + progress / 100f
                textViewSpeedValue.text = "${(speedMultiplier * 100).toInt()}%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Количество жуков (3 - 15)
        seekBarMaxBugs.max = 12 // 15 - 3
        seekBarMaxBugs.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewMaxBugsValue.text = (progress + 3).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Длительность раунда (30 - 120 секунд)
        seekBarRoundDuration.max = 90 // 120 - 30
        seekBarRoundDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewRoundDurationValue.text = "${progress + 30} сек"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Штраф за промах (0 - 20 очков)
        seekBarMissPenalty.max = 20
        seekBarMissPenalty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewMissPenaltyValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupButtons() {
        btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        btnResetDefaults.setOnClickListener {
            resetToDefaults()
        }
    }

    private fun saveSettings() {
        val speedMultiplier = 0.5f + seekBarSpeed.progress / 100f
        val maxBugs = seekBarMaxBugs.progress + 3
        val roundDuration = (seekBarRoundDuration.progress + 30) * 1000L
        val missPenalty = seekBarMissPenalty.progress

        val settings = GameSettings(
            speedMultiplier = speedMultiplier,
            maxBugs = maxBugs,
            roundDuration = roundDuration,
            missClickPenalty = missPenalty
        )

        GameSettings.save(requireContext(), settings)
        currentSettings = settings

        Toast.makeText(requireContext(), "Настройки сохранены", Toast.LENGTH_SHORT).show()
    }

    private fun resetToDefaults() {
        val defaultSettings = GameSettings()
        GameSettings.save(requireContext(), defaultSettings)
        loadSettings()
        Toast.makeText(requireContext(), "Настройки сброшены", Toast.LENGTH_SHORT).show()
    }
}

package com.BigBon.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.BigBon.bugs.R

class SettingsFragment : Fragment() {

    private lateinit var seekBarSpeed: SeekBar
    private lateinit var textViewSpeedValue: TextView
    private lateinit var seekBarMaxBugs: SeekBar
    private lateinit var textViewMaxBugsValue: TextView
    private lateinit var seekBarBonusInterval: SeekBar
    private lateinit var textViewBonusIntervalValue: TextView
    private lateinit var seekBarRoundDuration: SeekBar
    private lateinit var textViewRoundDurationValue: TextView

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
        setupSeekBars()
    }

    private fun initViews(view: View) {
        seekBarSpeed = view.findViewById(R.id.seekBarSpeed)
        textViewSpeedValue = view.findViewById(R.id.textViewSpeedValue)
        seekBarMaxBugs = view.findViewById(R.id.seekBarMaxBugs)
        textViewMaxBugsValue = view.findViewById(R.id.textViewMaxBugsValue)
        seekBarBonusInterval = view.findViewById(R.id.seekBarBonusInterval)
        textViewBonusIntervalValue = view.findViewById(R.id.textViewBonusIntervalValue)
        seekBarRoundDuration = view.findViewById(R.id.seekBarRoundDuration)
        textViewRoundDurationValue = view.findViewById(R.id.textViewRoundDurationValue)
    }

    private fun setupSeekBars() {
        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewSpeedValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarMaxBugs.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewMaxBugsValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarBonusInterval.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewBonusIntervalValue.text = "$progress сек"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarRoundDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewRoundDurationValue.text = "$progress сек"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}

package com.BigBon.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.BigBon.bugs.R
import java.text.SimpleDateFormat
import java.util.*

class RegistrationFragment : Fragment() {

    private lateinit var editTextFullName: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var spinnerCourse: Spinner
    private lateinit var seekBarDifficulty: SeekBar
    private lateinit var textViewDifficultyValue: TextView
    private lateinit var calendarViewBirthDate: CalendarView
    private lateinit var buttonRegister: Button
    private lateinit var textViewResult: TextView
    private lateinit var imageViewZodiac: ImageView

    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupSpinner()
        setupSeekBar()
        setupCalendar()
        setupButton()
    }

    private fun initViews(view: View) {
        editTextFullName = view.findViewById(R.id.editTextFullName)
        radioGroupGender = view.findViewById(R.id.radioGroupGender)
        spinnerCourse = view.findViewById(R.id.spinnerCourse)
        seekBarDifficulty = view.findViewById(R.id.seekBarDifficulty)
        textViewDifficultyValue = view.findViewById(R.id.textViewDifficultyValue)
        calendarViewBirthDate = view.findViewById(R.id.calendarViewBirthDate)
        buttonRegister = view.findViewById(R.id.buttonRegister)
        textViewResult = view.findViewById(R.id.textViewResult)
        imageViewZodiac = view.findViewById(R.id.imageViewZodiac)
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.courses,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourse.adapter = adapter
        }
    }

    private fun setupSeekBar() {
        seekBarDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewDifficultyValue.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupCalendar() {
        calendarViewBirthDate.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        }
    }

    private fun setupButton() {
        buttonRegister.setOnClickListener {
            val fullName = editTextFullName.text.toString()
            val gender = if (view?.findViewById<RadioButton>(R.id.radioButtonMale)?.isChecked == true) {
                getString(R.string.male)
            } else {
                getString(R.string.female)
            }
            val course = spinnerCourse.selectedItem.toString()
            val difficulty = seekBarDifficulty.progress
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val birthDate = dateFormat.format(Date(selectedDate))
            val zodiacSign = getZodiacSign(selectedDate)

            val result = """
                ${getString(R.string.result_title)}
                ФИО: $fullName
                Пол: $gender
                Курс: $course
                Сложность: $difficulty
                Дата рождения: $birthDate
                Знак зодиака: $zodiacSign
            """.trimIndent()

            textViewResult.text = result
            imageViewZodiac.visibility = View.VISIBLE
            imageViewZodiac.setImageResource(getZodiacImage(zodiacSign))
        }
    }

    private fun getZodiacSign(dateInMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1

        return when {
            (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Овен"
            (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Телец"
            (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Близнецы"
            (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Рак"
            (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Лев"
            (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Дева"
            (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Весы"
            (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Скорпион"
            (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Стрелец"
            (month == 12 && day >= 22) || (month == 1 && day <= 19) -> "Козерог"
            (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Водолей"
            else -> "Рыбы"
        }
    }

    private fun getZodiacImage(zodiacSign: String): Int {
        return when (zodiacSign) {
            "Овен" -> R.drawable.zodiac_aries
            "Телец" -> R.drawable.zodiac_taurus
            "Близнецы" -> R.drawable.zodiac_gemini
            "Рак" -> R.drawable.zodiac_cancer
            "Лев" -> R.drawable.zodiac_leo
            "Дева" -> R.drawable.zodiac_virgo
            "Весы" -> R.drawable.zodiac_libra
            "Скорпион" -> R.drawable.zodiac_scorpio
            "Стрелец" -> R.drawable.zodiac_sagittarius
            "Козерог" -> R.drawable.zodiac_capricorn
            "Водолей" -> R.drawable.zodiac_aquarius
            else -> R.drawable.zodiac_pisces
        }
    }
}

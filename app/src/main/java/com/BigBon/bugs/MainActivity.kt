package com.BigBon.bugs

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

data class PlayerData(
    val fullName: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: String,
    val zodiacSign: String
)

class MainActivity : AppCompatActivity() {
    private lateinit var editTextFullName: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioButtonMale: RadioButton
    private lateinit var radioButtonFemale: RadioButton
    private lateinit var spinnerCourse: Spinner
    private lateinit var seekBarDifficulty: SeekBar
    private lateinit var textViewDifficultyValue: TextView
    private lateinit var calendarViewBirthDate: CalendarView
    private lateinit var buttonRegister: Button
    private lateinit var textViewResult: TextView
    private lateinit var imageViewZodiac: ImageView

    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupSpinner()
        setupSeekBar()
        setupCalendar()
        setupRegisterButton()
    }

    private fun initViews() {
        editTextFullName = findViewById(R.id.editTextFullName)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        radioButtonMale = findViewById(R.id.radioButtonMale)
        radioButtonFemale = findViewById(R.id.radioButtonFemale)
        spinnerCourse = findViewById(R.id.spinnerCourse)
        seekBarDifficulty = findViewById(R.id.seekBarDifficulty)
        textViewDifficultyValue = findViewById(R.id.textViewDifficultyValue)
        calendarViewBirthDate = findViewById(R.id.calendarViewBirthDate)
        buttonRegister = findViewById(R.id.buttonRegister)
        textViewResult = findViewById(R.id.textViewResult)
        imageViewZodiac = findViewById(R.id.imageViewZodiac)
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this,
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
            selectedDate.set(year, month, dayOfMonth)
        }
    }

    private fun setupRegisterButton() {
        buttonRegister.setOnClickListener {
            registerPlayer()
        }
    }

    private fun registerPlayer() {
        val fullName = editTextFullName.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите ФИО", Toast.LENGTH_SHORT).show()
            return
        }

        val gender = if (radioButtonMale.isChecked) "Мужской" else "Женский"
        val course = spinnerCourse.selectedItem.toString()
        val difficulty = seekBarDifficulty.progress
        val birthDate = formatDate(selectedDate)
        val zodiacSign = getZodiacSign(
            selectedDate.get(Calendar.DAY_OF_MONTH),
            selectedDate.get(Calendar.MONTH) + 1
        )

        val playerData = PlayerData(fullName, gender, course, difficulty, birthDate, zodiacSign)
        displayPlayerData(playerData)
    }

    private fun formatDate(calendar: Calendar): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        return String.format("%02d.%02d.%d", day, month, year)
    }

    private fun getZodiacSign(day: Int, month: Int): String {
        return when (month) {
            1 -> if (day <= 19) "Козерог" else "Водолей"
            2 -> if (day <= 18) "Водолей" else "Рыбы"
            3 -> if (day <= 20) "Рыбы" else "Овен"
            4 -> if (day <= 19) "Овен" else "Телец"
            5 -> if (day <= 20) "Телец" else "Близнецы"
            6 -> if (day <= 20) "Близнецы" else "Рак"
            7 -> if (day <= 22) "Рак" else "Лев"
            8 -> if (day <= 22) "Лев" else "Дева"
            9 -> if (day <= 22) "Дева" else "Весы"
            10 -> if (day <= 22) "Весы" else "Скорпион"
            11 -> if (day <= 21) "Скорпион" else "Стрелец"
            12 -> if (day <= 21) "Стрелец" else "Козерог"
            else -> "Неизвестно"
        }
    }

    private fun getZodiacDrawable(zodiacSign: String): Int {
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
            "Рыбы" -> R.drawable.zodiac_pisces
            else -> R.drawable.ic_launcher_foreground
        }
    }

    private fun displayPlayerData(playerData: PlayerData) {
        val resultText = """
            Данные игрока:

            ФИО: ${playerData.fullName}
            Пол: ${playerData.gender}
            Курс: ${playerData.course}
            Уровень сложности: ${playerData.difficulty}
            Дата рождения: ${playerData.birthDate}
            Знак зодиака: ${playerData.zodiacSign}
        """.trimIndent()

        textViewResult.text = resultText
        imageViewZodiac.setImageResource(getZodiacDrawable(playerData.zodiacSign))
        imageViewZodiac.visibility = View.VISIBLE
    }
}
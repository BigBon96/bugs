package com.BigBon.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.BigBon.bugs.R
import com.BigBon.bugs.database.AppDatabase
import com.BigBon.bugs.database.dao.RecordWithUsername
import com.BigBon.bugs.repository.GameRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RecordsFragment : Fragment() {

    private lateinit var repository: GameRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerDifficulty: Spinner
    private lateinit var tvNoRecords: TextView
    private lateinit var adapter: RecordsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = GameRepository(database.userDao(), database.gameRecordDao())

        recyclerView = view.findViewById(R.id.recyclerViewRecords)
        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty)
        tvNoRecords = view.findViewById(R.id.tvNoRecords)

        setupRecyclerView()
        setupDifficultyFilter()
    }

    private fun setupRecyclerView() {
        adapter = RecordsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupDifficultyFilter() {
        val difficulties = listOf("Легко", "Средне", "Сложно")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            difficulties
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDifficulty.adapter = spinnerAdapter

        spinnerDifficulty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val difficulty = when (position) {
                    0 -> "easy"
                    1 -> "medium"
                    2 -> "hard"
                    else -> "easy"
                }
                loadRecords(difficulty)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Загружаем записи для первой выбранной сложности
        loadRecords("easy")
    }

    private fun loadRecords(difficulty: String) {
        lifecycleScope.launch {
            repository.getTopRecordsByDifficulty(difficulty, 50).collect { records ->
                if (records.isEmpty()) {
                    tvNoRecords.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    tvNoRecords.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(records)
                }
            }
        }
    }

    companion object {
        fun newInstance() = RecordsFragment()
    }
}

class RecordsAdapter : RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

    private var records = emptyList<RecordWithUsername>()

    fun submitList(newRecords: List<RecordWithUsername>) {
        records = newRecords
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(records[position], position + 1)
    }

    override fun getItemCount() = records.size

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlace: TextView = itemView.findViewById(R.id.tvPlace)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        private val tvStats: TextView = itemView.findViewById(R.id.tvStats)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(record: RecordWithUsername, place: Int) {
            tvPlace.text = "$place."
            tvUsername.text = record.username
            tvScore.text = "Очки: ${record.score}"

            val accuracy = if (record.hits + record.misses > 0) {
                String.format("%.1f%%", record.hits.toFloat() / (record.hits + record.misses) * 100)
            } else {
                "0%"
            }
            tvStats.text = "Попадания: ${record.hits} | Промахи: ${record.misses} | Точность: $accuracy"

            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(record.timestamp))

            // Выделим топ-3
            when (place) {
                1 -> itemView.setBackgroundColor(0xFFFFD700.toInt()) // Золотой
                2 -> itemView.setBackgroundColor(0xFFC0C0C0.toInt()) // Серебро
                3 -> itemView.setBackgroundColor(0xFFCD7F32.toInt()) // Бронза
                else -> itemView.setBackgroundColor(0xFFFFFFFF.toInt()) // Белый
            }
        }
    }
}

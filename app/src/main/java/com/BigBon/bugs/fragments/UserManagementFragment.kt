package com.BigBon.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.BigBon.bugs.R
import com.BigBon.bugs.database.AppDatabase
import com.BigBon.bugs.repository.GameRepository
import com.BigBon.bugs.utils.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserManagementFragment : Fragment() {

    private lateinit var repository: GameRepository
    private lateinit var prefsManager: PreferencesManager

    private lateinit var tvCurrentUser: TextView
    private lateinit var spinnerUsers: Spinner
    private lateinit var etNewUsername: EditText
    private lateinit var btnCreateUser: Button
    private lateinit var btnSwitchUser: Button
    private lateinit var btnDeleteUser: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = GameRepository(database.userDao(), database.gameRecordDao())
        prefsManager = PreferencesManager(requireContext())

        tvCurrentUser = view.findViewById(R.id.tvCurrentUser)
        spinnerUsers = view.findViewById(R.id.spinnerUsers)
        etNewUsername = view.findViewById(R.id.etNewUsername)
        btnCreateUser = view.findViewById(R.id.btnCreateUser)
        btnSwitchUser = view.findViewById(R.id.btnSwitchUser)
        btnDeleteUser = view.findViewById(R.id.btnDeleteUser)

        btnCreateUser.setOnClickListener { createUser() }
        btnSwitchUser.setOnClickListener { switchUser() }
        btnDeleteUser.setOnClickListener { deleteUser() }

        loadUsers()
        updateCurrentUserDisplay()
    }

    private fun updateCurrentUserDisplay() {
        val username = prefsManager.getCurrentUsername()
        tvCurrentUser.text = if (username != null) {
            "Текущий пользователь: $username"
        } else {
            "Текущий пользователь: не выбран"
        }
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            repository.getAllUsers().collect { users ->
                val usernames = users.map { it.username }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    usernames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerUsers.adapter = adapter
            }
        }
    }

    private fun createUser() {
        val username = etNewUsername.text.toString().trim()

        if (username.isEmpty()) {
            Toast.makeText(requireContext(), "Введите имя пользователя", Toast.LENGTH_SHORT).show()
            return
        }

        if (username.length < 3) {
            Toast.makeText(requireContext(), "Имя должно быть не менее 3 символов", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Проверяем, существует ли пользователь
                val existingUser = repository.getUserByUsername(username)
                if (existingUser != null) {
                    Toast.makeText(requireContext(), "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Создаем пользователя
                val userId = repository.createUser(username)
                prefsManager.setCurrentUser(userId, username)

                Toast.makeText(requireContext(), "Пользователь создан и выбран", Toast.LENGTH_SHORT).show()
                etNewUsername.text.clear()
                updateCurrentUserDisplay()
                loadUsers()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка создания пользователя: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchUser() {
        val selectedUsername = spinnerUsers.selectedItem as? String

        if (selectedUsername == null) {
            Toast.makeText(requireContext(), "Выберите пользователя", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val user = repository.getUserByUsername(selectedUsername)
            if (user != null) {
                prefsManager.setCurrentUser(user.id, user.username)
                Toast.makeText(requireContext(), "Пользователь переключен", Toast.LENGTH_SHORT).show()
                updateCurrentUserDisplay()
            } else {
                Toast.makeText(requireContext(), "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUser() {
        val selectedUsername = spinnerUsers.selectedItem as? String

        if (selectedUsername == null) {
            Toast.makeText(requireContext(), "Выберите пользователя для удаления", Toast.LENGTH_SHORT).show()
            return
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Удаление пользователя")
            .setMessage("Вы уверены, что хотите удалить пользователя '$selectedUsername'? Все его рекорды будут удалены.")
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    val user = repository.getUserByUsername(selectedUsername)
                    if (user != null) {
                        repository.deleteUser(user)

                        // Если удалили текущего пользователя, сбрасываем выбор
                        if (prefsManager.getCurrentUserId() == user.id) {
                            prefsManager.clearCurrentUser()
                            updateCurrentUserDisplay()
                        }

                        Toast.makeText(requireContext(), "Пользователь удален", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    companion object {
        fun newInstance() = UserManagementFragment()
    }
}

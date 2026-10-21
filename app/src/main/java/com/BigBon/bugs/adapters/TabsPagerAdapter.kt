package com.BigBon.bugs.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.BigBon.bugs.fragments.AuthorsFragment
import com.BigBon.bugs.fragments.GameFragment
import com.BigBon.bugs.fragments.UserManagementFragment
import com.BigBon.bugs.fragments.RulesFragment
import com.BigBon.bugs.fragments.SettingsFragment
import com.BigBon.bugs.fragments.RecordsFragment

class TabsPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserManagementFragment()
            1 -> RulesFragment()
            2 -> AuthorsFragment()
            3 -> SettingsFragment()
            4 -> GameFragment()
            5 -> RecordsFragment()
            else -> UserManagementFragment()
        }
    }
}

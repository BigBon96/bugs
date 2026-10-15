package com.BigBon.bugs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.BigBon.bugs.R
import com.BigBon.bugs.adapters.AuthorsAdapter
import com.BigBon.bugs.models.Author

class AuthorsFragment : Fragment() {

    private lateinit var listViewAuthors: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listViewAuthors = view.findViewById(R.id.listViewAuthors)

        val authors = listOf(
            Author("Синицын Станислав", R.drawable.author_sinitsyn)
        )

        val adapter = AuthorsAdapter(requireContext(), authors)
        listViewAuthors.adapter = adapter
    }
}

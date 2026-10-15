package com.BigBon.bugs.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.BigBon.bugs.R
import com.BigBon.bugs.models.Author

class AuthorsAdapter(
    private val context: Context,
    private val authors: List<Author>
) : BaseAdapter() {

    override fun getCount(): Int = authors.size

    override fun getItem(position: Int): Any = authors[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_author, parent, false)

        val author = authors[position]

        val imageViewPhoto = view.findViewById<ImageView>(R.id.imageViewAuthorPhoto)
        val textViewName = view.findViewById<TextView>(R.id.textViewAuthorName)

        imageViewPhoto.setImageResource(author.photoResId)
        textViewName.text = author.name

        return view
    }
}

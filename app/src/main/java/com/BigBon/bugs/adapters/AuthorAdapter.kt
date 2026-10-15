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

class AuthorAdapter(
    private val context: Context,
    private val authors: List<Author>
) : BaseAdapter() {

    override fun getCount(): Int = authors.size

    override fun getItem(position: Int): Author = authors[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_author, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val author = getItem(position)
        holder.imageViewPhoto.setImageResource(author.photoResId)
        holder.textViewName.text = author.name

        return view
    }

    private class ViewHolder(view: View) {
        val imageViewPhoto: ImageView = view.findViewById(R.id.imageViewAuthorPhoto)
        val textViewName: TextView = view.findViewById(R.id.textViewAuthorName)
    }
}

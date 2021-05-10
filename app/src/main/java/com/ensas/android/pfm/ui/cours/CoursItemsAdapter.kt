package com.ensas.android.pfm.ui.cours

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ensas.android.pfm.databinding.CoursItemBinding


class CoursItemsAdapter(private val mContext: CoursFragment, private var cursor: Cursor?) : RecyclerView.Adapter<CoursItemsAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: CoursItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(t: String, l: String){
            with(binding){

                title.text = t.toString()
                element.setOnClickListener {
                    val intent: Intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(l)
                    mContext?.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CoursItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!cursor!!.moveToPosition(position)) {
            return
        }
        val title: String = cursor!!.getString(cursor!!.getColumnIndex("title"))
        val link: String = cursor!!.getString(cursor!!.getColumnIndex("link"))
        holder.bind(title,link)

    }

    override fun getItemCount(): Int {
        return cursor!!.count
    }

}
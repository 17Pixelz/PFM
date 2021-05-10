package com.ensas.android.pfm.ui.quiz


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.ensas.android.pfm.R


internal class MyGridAdapter( private val context: Context, private val answers: IntArray) : BaseAdapter() {
    override fun getCount(): Int {
        return answers.size
    }
    override fun getItem(position: Int): Any? {
        return answers.get(position)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position:Int, convertView: View?, parent: ViewGroup?): View?
    {
        val inflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.grid_question_item,null)

        val txt = view.findViewById<TextView>(R.id.q_id)

        txt.text = "Q${position+1}"

        if(answers?.get(position) == 0)
            txt.background = ContextCompat.getDrawable(parent?.context, R.drawable.not_answered_q)
        else
            txt.background = ContextCompat.getDrawable(parent?.context, R.drawable.default_option_border_bg)

        return view
    }
}
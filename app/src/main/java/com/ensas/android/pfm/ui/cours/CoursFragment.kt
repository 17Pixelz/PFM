package com.ensas.android.pfm.ui.cours

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ensas.android.pfm.databinding.FragmentCoursBinding
import com.ensas.android.pfm.dbHandler.DbHandler


class CoursFragment : Fragment() {

    lateinit var binding: FragmentCoursBinding
    lateinit var DbHandler: DbHandler
    private var adapter:CoursItemsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCoursBinding.inflate(inflater)
        DbHandler = DbHandler(context)

        adapter= CoursItemsAdapter(this, DbHandler.getChapters())

        binding.list.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.list.adapter = adapter


        val dividerItemDecoration = DividerItemDecoration(
            this.context,
            (binding.list.layoutManager as LinearLayoutManager).orientation
        )
        binding.list.addItemDecoration(dividerItemDecoration)


        return binding.root
    }
}
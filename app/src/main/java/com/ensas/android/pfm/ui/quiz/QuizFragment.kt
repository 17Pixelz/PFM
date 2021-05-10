package com.ensas.android.pfm.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.ensas.android.pfm.databinding.FragmentQuizBinding
import com.ensas.android.pfm.dbHandler.DbHandler

class QuizFragment : Fragment() {

    lateinit var binding: FragmentQuizBinding
    lateinit var chapitres: ArrayList<String>
    lateinit var dbHandler : DbHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQuizBinding.inflate(inflater)

        dbHandler = DbHandler(context)

        // Load all chapters in chapitres arrayList
        getChapters()

        // Defining a spinnerAdapter for our spinner
        var spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, chapitres)
        spinnerAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(binding.spin)
        {
            adapter = spinnerAdapter
            setSelection(0, false)
            prompt = "Choisissez une chapitre"
            gravity = Gravity.CENTER

        }

        binding.quiz.setOnClickListener{
            LaunchQuiz();
        };

        return binding.root
    }

    private fun LaunchQuiz(){
        val chapter = binding.spin.selectedItemId + 1
        val intent = Intent(this.context, QuizQuestionsActivity::class.java)
        intent.putExtra("chapterId", "$chapter")
        context?.startActivity(intent)
    }

    private fun getChapters(){
        val nch = dbHandler.getCountChaps()
        chapitres = ArrayList(nch)

        val cur = dbHandler.getChapters()
        cur?.moveToFirst()
        do {
            chapitres.add(cur?.getString(cur?.getColumnIndex("title")).toString())
        }while(cur?.moveToNext()!!)
    }
}
package com.ensas.android.pfm.ui.quiz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ensas.android.pfm.Modules.Question
import com.ensas.android.pfm.R
import com.ensas.android.pfm.databinding.ActivityQuizQuestionsBinding
import com.ensas.android.pfm.dbHandler.DbHandler
import com.ensas.android.pfm.dbHandler.DbHandler.CONSTS


class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizQuestionsBinding
    private lateinit var DbHandler: DbHandler

    // Questions and the chosen answears by the user
    private lateinit var questions: ArrayList<Question>
    private lateinit var answers: IntArray

    // Helpful variables
    private var position: Int = 1                   // current position
    private var selected: Int = 0                   // selected choice
    private var chapterId: Int? = null              // chapter id (no need to explicate this ;) )
    private var isEvaluated: Boolean = false        // is it a validation or the first go


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.max = CONSTS.MAX_QUESTS

        DbHandler = DbHandler(this)                                         // initializing our database handler
        val type = intent.getIntExtra("type", 0)                   // getting the type variable to determine the if it's a validation of first go

        if(type == 0){ // if it's not a revision or correction
            firstGo()
        }
        else { // if it's a revision or correction
            position = intent.getIntExtra("n", 0)                         // here we get the number of the question we want to revise or see the correct answer of it
            questions = intent.getParcelableArrayListExtra("questions")!!           // loading the questions
            answers = intent.getIntArrayExtra("answers")!!                          // getting all the options selected
            isEvaluated = intent.getBooleanExtra("isEvaluated", false)    // getting the variable isEvaluated to check if it's a revision or just re-check

            setQuestion()   // Load questions

            if(isEvaluated){ // if it's a correction
                correction()
            }else{ // and finally if it's a revision
                revision()
            }
        }
    }

    fun firstGo(){
        // getting the chapter id
        chapterId = intent.getStringExtra("chapterId")!!.toInt()
        // loading data from database for the chosen chapter
        questions = DbHandler.getQuestions(chapterId!!)
        answers = IntArray(CONSTS.MAX_QUESTS)
        setQuestion()
        addListner()
    }

    fun correction(){
        // here we make all staff we don't need invisible
        binding.sbt.visibility = View.INVISIBLE
        binding.ter.visibility = View.INVISIBLE
        binding.progress.visibility = View.INVISIBLE
        binding.llProgressDetails.visibility = View.INVISIBLE

        binding.op1.setTextColor(Color.parseColor("#000000"))
        binding.op2.setTextColor(Color.parseColor("#000000"))
        binding.op3.setTextColor(Color.parseColor("#000000"))
        binding.op4.setTextColor(Color.parseColor("#000000"))

        // we color the right answer and the wrong answer if it exists
        answerV(answers?.get(position-1)!!, R.drawable.wrong_option_border_bg)
        answerV(questions?.get(position-1)!!.correctAnswer, R.drawable.correct_option_border_bg)
    }


    fun revision(){
        // show the old selected option
        when (answers?.get(position-1)){
            1 -> {
                selectedV(binding.op1, 1)
            }
            2 -> {
                selectedV(binding.op2, 2)
            }
            3 -> {
                selectedV(binding.op3, 3)
            }
            4 -> {
                selectedV(binding.op4, 4)
            }
        }
        addListner()
    }

    fun addListner(){

        binding.op1.setOnClickListener(this)
        binding.op2.setOnClickListener(this)
        binding.op3.setOnClickListener(this)
        binding.op4.setOnClickListener(this)
        binding.sbt.setOnClickListener(this)
        binding.ter.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.op1.id -> {
                selectedV(binding.op1, 1)
            }
            binding.op2.id -> {
                selectedV(binding.op2, 2)
            }
            binding.op3.id -> {
                selectedV(binding.op3, 3)
            }
            binding.op4.id -> {
                selectedV(binding.op4, 4)
            }

            binding.ter.id -> {
                answers?.set(position - 1, selected)
                goToConfirm()
            }

            binding.sbt.id -> {

                answers?.set(position - 1, selected)
                position++
                if (position <= questions!!.size) {
                    setQuestion()
                } else {
                    goToConfirm()
                }

                selected = 0

            }
        }
    }

    private fun goToConfirm(){
        val intent = Intent(this@QuizQuestionsActivity, ConfirmActivity::class.java)
        intent.putParcelableArrayListExtra("questions", questions)
        intent.putExtra("answers", answers)
        intent.putExtra("chapterId",chapterId)
        startActivity(intent)
        finish()
    }

    private fun setQuestion() {
        val question = questions!!.get(position - 1) // Getting the question from the list with the help of current position.

        defaultV()    //

        if (position == questions!!.size) {
            binding.sbt.text = "TERMINER"
            binding.ter.visibility = View.GONE
        } else {
            binding.sbt.text = "SUIVANT"
        }

        binding.progressBar.progress = position
        binding.progress.text = "$position" + "/" + binding.progressBar.getMax()


        binding.q.text = question.question
        binding.op1.text = question.optionOne
        binding.op2.text = question.optionTwo
        binding.op3.text = question.optionThree
        binding.op4.text = question.optionFour
    }


    // These functions allow us to make a special style on :
    //      * selected option
    //      * default view
    //      * answer view (correct and wrong answer)

    private fun selectedV(tv: TextView, selectedOptionNum: Int) {
        defaultV()
        selected = selectedOptionNum
        tv.setTextColor(
            Color.parseColor("#363A43")
        )
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)
    }

    private fun defaultV() {

        val options = ArrayList<TextView>()
        options.add(0, binding.op1)
        options.add(1, binding.op2)
        options.add(2, binding.op3)
        options.add(3, binding.op4)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_border_bg
            )
        }
    }

    private fun answerV(answer: Int, drawableView: Int) {
        when (answer) {
            1 -> {
                binding.op1.background = ContextCompat.getDrawable(this, drawableView)
            }
            2 -> {
                binding.op2.background = ContextCompat.getDrawable(this, drawableView)
            }
            3 -> {
                binding.op3.background = ContextCompat.getDrawable(this, drawableView)
            }
            4 -> {
                binding.op4.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }
}
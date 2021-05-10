package com.ensas.android.pfm.ui.quiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ensas.android.pfm.MainActivity
import com.ensas.android.pfm.Modules.Question
import com.ensas.android.pfm.R
import com.ensas.android.pfm.databinding.ActivityConfirmBinding
import com.ensas.android.pfm.dbHandler.DbHandler


class ConfirmActivity: AppCompatActivity() {

    private lateinit var binding: ActivityConfirmBinding
    private lateinit var questions: ArrayList<Question>
    private var answers: IntArray? = null

    private lateinit var DbHandler: DbHandler

    private lateinit var vibrator:Vibrator

    private lateinit var btns: ArrayList<TextView>

    private var isEvaluated: Boolean = false
    private var score: Int = 0
    private var chapterId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DbHandler = DbHandler(this)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        questions = intent.getParcelableArrayListExtra("questions")!!
        answers = intent.getIntArrayExtra("answers")
        chapterId = intent.getIntExtra("chapterId",0)


        val adapter = MyGridAdapter(this,answers!!)
        binding.grid.adapter = adapter

        binding.grid.setOnItemClickListener { parent, view, position, id ->
            rollBack(position+1)
            if(!isEvaluated)
                finish()
        }

        binding.eval.setOnClickListener {
            solve()
            binding.eval.text = "RETOURNER"
            binding.eval.setOnClickListener {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun rollBack(id: Int){
        val intent = Intent(this, QuizQuestionsActivity::class.java)
        intent.putExtra("n", id)
        intent.putParcelableArrayListExtra("questions", questions)
        intent.putExtra("type", 1)
        intent.putExtra("isEvaluated",isEvaluated)
        intent.putExtra("answers", answers)
        startActivity(intent)
    }

    fun solve(){
        isEvaluated = true
        for (i in 0 until questions.size){
            val gridChild = binding.grid.getChildAt(i).findViewById<TextView>(R.id.q_id)
            if(questions[i].correctAnswer == answers?.get(i)) {
                gridChild.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.correct_option_border_bg
                )
                score++
            }else{
                gridChild.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.wrong_option_border_bg
                )
            }
        }
        if(score > 7){
            binding.result.setTextColor(Color.parseColor("#ff99cc00"))
            binding.result.text = "Bonne travail\n$score/10"
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(100)
                Thread.sleep(110)
                vibrator.vibrate(200)
            }
        }else {
            binding.result.setTextColor(Color.parseColor("#ffff4444"))
            binding.result.text = "Malheuresement\n$score/10"
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(500)
            }
        }

        DbHandler.insertHistory(chapterId,score)
    }
}
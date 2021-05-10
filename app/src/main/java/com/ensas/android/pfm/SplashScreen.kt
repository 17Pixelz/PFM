package com.ensas.android.pfm

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.ensas.android.pfm.Modules.Chapter
import com.ensas.android.pfm.Modules.Question
import com.ensas.android.pfm.dbHandler.DbHandler
import java.io.InputStream


@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    lateinit var DbHandler: DbHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        startAnimation()

        DbHandler = DbHandler(this)

        var prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)


        if (firstStart) { // if it's the first time we run the application
            // this function help us with filling the database with chapters and questions
            fillDB("chapitres.csv", "questions.csv")

            // we deactivate the first run declaration
            val editor = prefs.edit()
            editor.putBoolean("firstStart", false)
            editor.apply()
        }


        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    override fun onRestart() {
        super.onRestart()

        var prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)


        if (firstStart) { // if it's the first time we run the application
            // this function help us with filling the database with chapters and questions
            fillDB("chapitres.csv", "questions.csv")

            // we deactivate the first run declaration
            val editor = prefs.edit()
            editor.putBoolean("firstStart", false)
            editor.apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startAnimation() {
        val mProgressBar = findViewById<View>(R.id.sp_pb) as ProgressBar
        val progressAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 100)
        progressAnimator.duration = 2000
        progressAnimator.interpolator = LinearInterpolator()
        progressAnimator.start()
    }

    fun fillDB(chapterFile: String, questionsFile: String){
        loadChapters(chapterFile)
        loadQuestions(questionsFile)
    }

    private fun getDataFromCSV(filename: String): List<String> {
        val inputStream: InputStream = assets.open(filename)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        return String(buffer).split("\n")
    }

    private fun loadQuestions(filename: String) {
        var data = arrayListOf<String>()
        val lines = getDataFromCSV(filename)
        for (line in lines) {
            data = line.split(',') as ArrayList<String>
            DbHandler.insertQuestion(
                Question(
                    data[0].trim().toInt(),
                    data[1],
                    data[2],
                    data[3],
                    data[4],
                    data[5],
                    data[6].trim().toInt(),
                    data[7].trim().toInt()
                )

            )
        }
    }

    private fun loadChapters(filename: String) {
        val lines = getDataFromCSV(filename)
        var data = arrayListOf<String>()
        for (line in lines) {
            data = line.split(',') as ArrayList<String>
            DbHandler.insertChapter(
                Chapter(
                    data[0].toInt(),
                    data[1],
                    data[2]
                )
            )
        }
    }

}
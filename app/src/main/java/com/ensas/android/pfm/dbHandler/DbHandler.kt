package com.ensas.android.pfm.dbHandler

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.ensas.android.pfm.Modules.Chapter
import com.ensas.android.pfm.Modules.Question


class DbHandler(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object CONSTS{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "QCM_DB"
        private const val TABLE_CHAPTER = "chapter"
        private const val TABLE_QUESTION = "question"
        private const val TABLE_HISTORY = "history"

        const val MAX_QUESTS = 10

        private const val KEY_ID = "id"
        // chapitres table
        private const val KEY_TITLE = "title"
        private const val KEY_LINK = "link"

        // SQL statement to create chaptres table
        private const val CREATE_CHAPTER_TABLE =("CREATE TABLE " + TABLE_CHAPTER + "(" +
                KEY_ID + " INT PRIMARY KEY," +
                KEY_TITLE + " TEXT," +
                KEY_LINK + " TEXT" +
                ");"
                )


        // questions table
        private const val KEY_QUESTION = "question"
        private const val KEY_OPTION1 = "optionOne"
        private const val KEY_OPTION2 = "optionTwo"
        private const val KEY_OPTION3 = "optionThree"
        private const val KEY_OPTION4 = "optionFour"
        private const val KEY_ANSWER = "answer"
        private const val KEY_CHAPTER = "chapterId" // works also in history


        // SQL statement to create questions table
        private const val CREATE_QUESTION_TABLE = (
                "CREATE TABLE " + TABLE_QUESTION + "(" +
                        KEY_ID + " INT PRIMARY KEY,"+
                        KEY_QUESTION + " TEXT," +
                        KEY_OPTION1 + " TEXT," +
                        KEY_OPTION2 + " TEXT," +
                        KEY_OPTION3 + " TEXT," +
                        KEY_OPTION4 + " TEXT," +
                        KEY_ANSWER + " TEXT," +
                        KEY_CHAPTER + " INT," +
                        " FOREIGN KEY ("+ KEY_CHAPTER+") REFERENCES "+ TABLE_CHAPTER +"("+ KEY_ID +")"+
                        ");"
                )


        // history table
        private const val KEY_SCORE = "score"

        // SQL statement to create history table
        private const val CREATE_HISTORY_TABLE = (
                "CREATE TABLE " + TABLE_HISTORY + "("+
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_CHAPTER + " INT," +
                        KEY_SCORE + " INT," +
                        " FOREIGN KEY ("+ KEY_CHAPTER+") REFERENCES "+ TABLE_CHAPTER +"("+ KEY_ID +")"+
                        ");"
                )


    }



    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_CHAPTER_TABLE)
        db?.execSQL(CREATE_QUESTION_TABLE)
        db?.execSQL(CREATE_HISTORY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CHAPTER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
    }

    ////////////////// Functions for chapitres table

    //  Return a cursor which contains all chapters
    fun getChapters(): Cursor?{
        val query = "SELECT * FROM $TABLE_CHAPTER"

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(query, null)
        }catch (e: SQLiteException) {
            db.execSQL(query)
            return cursor
        }
        return cursor
    }

    // Insert a chapter in the database
    fun insertChapter(chapter: Chapter){
        val db = this.readableDatabase
        val contentValues= ContentValues()

        contentValues.put(KEY_ID, chapter.id)
        contentValues.put(KEY_TITLE, chapter.title)
        contentValues.put(KEY_LINK, chapter.link)
        db.insertWithOnConflict(TABLE_CHAPTER, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE)
    }

    // Get how many chapters we have in the DB
    fun getCountChaps(): Int = DatabaseUtils.queryNumEntries(this.readableDatabase, TABLE_CHAPTER).toInt()




    ////////////////// Functions for the questions table

    // Returns an arraylist of Question in random order with a max defined in MAX_QUESTS
    fun getQuestions(chapter_id: Int): ArrayList<Question> {
        val questionsList = ArrayList<Question>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_QUESTION WHERE $KEY_CHAPTER = $chapter_id ORDER BY RANDOM() LIMIT $MAX_QUESTS",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex(KEY_ID)).toInt()
                val question = cursor.getString(cursor.getColumnIndex(KEY_QUESTION))
                val option1 = cursor.getString(cursor.getColumnIndex(KEY_OPTION1))
                val option2 = cursor.getString(cursor.getColumnIndex(KEY_OPTION2))
                val option3 = cursor.getString(cursor.getColumnIndex(KEY_OPTION3))
                val option4 = cursor.getString(cursor.getColumnIndex(KEY_OPTION4))
                val answer = cursor.getString(cursor.getColumnIndex(KEY_ANSWER)).toInt()

                questionsList.add( Question(id, question, option1, option2, option3, option4, answer, chapter_id) )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return questionsList
    }

    // Insert a question to the DB
    fun insertQuestion(question: Question){
        val db = this.readableDatabase
        val cv= ContentValues()

        cv.put(KEY_ID, question.id)
        cv.put(KEY_QUESTION,question.question)
        cv.put(KEY_OPTION1, question.optionOne)
        cv.put(KEY_OPTION2,question.optionTwo)
        cv.put(KEY_OPTION3,question.optionThree)
        cv.put(KEY_OPTION4,question.optionFour)
        cv.put(KEY_ANSWER,question.correctAnswer)
        cv.put(KEY_CHAPTER, question.chapterId)
        db.insertWithOnConflict(TABLE_QUESTION, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
    }



    ////////////////// Functions for history table

    // Get how many quiz we have passed
    fun getCountQuizs(): Int = DatabaseUtils.queryNumEntries(this.readableDatabase, TABLE_HISTORY).toInt()

    // Insert a quiz history in the SB
    fun insertHistory(ch_id: Int, score:Int){
        val db = this.readableDatabase
        val cv= ContentValues()

        cv.put(KEY_CHAPTER, ch_id)
        cv.put(KEY_SCORE, score)
        db.insertWithOnConflict(TABLE_HISTORY, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
    }

    // Get how many quiz we got correct
    fun getCorCountQuizs(): Int = DatabaseUtils.queryNumEntries(this.readableDatabase, TABLE_HISTORY, "$KEY_SCORE > 7").toInt()

    // Get how many quiz we got wrong
    fun getWroCountQuizs(): Int = DatabaseUtils.queryNumEntries(this.readableDatabase, TABLE_HISTORY, "$KEY_SCORE < 7").toInt()

    // Get how many quiz we passed for a specific chapter
    fun getCountQuizByChap(id: Int): Int = DatabaseUtils.queryNumEntries(this.readableDatabase, TABLE_HISTORY, "$KEY_CHAPTER = $id").toInt()

    // Get how many quiz we passed in each chapter
    fun getChaStat() : ArrayList<Int>{
        val n = getCountChaps()
        var list: ArrayList<Int> = ArrayList(n)

        for (i in 0 until getCountChaps()){
                val n = getCountQuizByChap(i+1)
                list.add(n)
            }
        return list;
    }

    // Get statistics of a specific chapter (correct times and wrong times)
    fun getChapStats(id: Int): ArrayList<Int>{
        var a: ArrayList<Int> = ArrayList(2)
        val db = this.readableDatabase
        a.add(DatabaseUtils.queryNumEntries(db, TABLE_HISTORY, "$KEY_CHAPTER = $id AND $KEY_SCORE > 7").toInt())
        a.add(DatabaseUtils.queryNumEntries(db, TABLE_HISTORY, "$KEY_CHAPTER = $id AND $KEY_SCORE < 7").toInt())
        return a
    }
}
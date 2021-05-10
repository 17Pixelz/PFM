package com.ensas.android.pfm.Modules

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question  (
        val id: Int,
        val question: String,
        val optionOne: String,
        val optionTwo: String,
        val optionThree: String,
        val optionFour: String,
        val correctAnswer: Int,
        val chapterId: Int
):Parcelable
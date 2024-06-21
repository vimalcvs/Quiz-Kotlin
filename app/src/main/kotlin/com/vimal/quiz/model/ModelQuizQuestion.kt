package com.vimal.quiz.model

import android.os.Parcel
import android.os.Parcelable

data class ModelQuizQuestion(
    val category: String,
    val question: String,
    val correct_answer: String,
    val exp: String,
    val incorrect_answers: Array<String>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArray()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(question)
        parcel.writeString(correct_answer)
        parcel.writeString(exp)
        parcel.writeStringArray(incorrect_answers)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ModelQuizQuestion(category='$category', question='$question', correct_answer='$correct_answer', exp='$exp', incorrect_answers=${incorrect_answers.contentToString()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModelQuizQuestion) return false

        if (category != other.category) return false
        if (question != other.question) return false
        if (correct_answer != other.correct_answer) return false
        if (exp != other.exp) return false
        if (!incorrect_answers.contentEquals(other.incorrect_answers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = category.hashCode()
        result = 31 * result + question.hashCode()
        result = 31 * result + correct_answer.hashCode()
        result = 31 * result + exp.hashCode()
        result = 31 * result + incorrect_answers.contentHashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<ModelQuizQuestion> {
        override fun createFromParcel(parcel: Parcel): ModelQuizQuestion {
            return ModelQuizQuestion(parcel)
        }

        override fun newArray(size: Int): Array<ModelQuizQuestion?> {
            return arrayOfNulls(size)
        }
    }
}

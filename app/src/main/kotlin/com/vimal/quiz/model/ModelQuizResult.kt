package com.vimal.quiz.model

import android.os.Parcel
import android.os.Parcelable

data class ModelQuizResult(
    val question: String,
    val rightAnswer: String,
    val userAnswer: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(question)
        parcel.writeString(rightAnswer)
        parcel.writeString(userAnswer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ModelQuizResult> {
        override fun createFromParcel(parcel: Parcel): ModelQuizResult {
            return ModelQuizResult(parcel)
        }

        override fun newArray(size: Int): Array<ModelQuizResult?> {
            return arrayOfNulls(size)
        }
    }
}

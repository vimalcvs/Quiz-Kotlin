package com.vimal.quiz.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import com.vimal.quiz.BuildConfig
import com.vimal.quiz.R
import com.vimal.quiz.model.ModelCategory
import com.vimal.quiz.model.ModelColor
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import java.util.Date
import java.util.Locale


object Utils {

    fun getErrors(e: Exception?) {
        println("TAG :: " + Log.getStackTraceString(e))
    }

    fun fromHtml(source: String): Spanned {
        return HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getVibrator(context: Context, durationMillis: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val duration = if (durationMillis == 0L) 50L else durationMillis
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    inline fun <reified T : Parcelable> Intent.getParcelableArrayListExtraCompat(key: String): ArrayList<T>? =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableArrayListExtra(
                key,
                T::class.java
            )

            else -> @Suppress("DEPRECATION") getParcelableArrayListExtra<T>(key)
        }

    inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
                key,
                T::class.java
            )

            else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T?
        }


    fun getScreenshotShare(context: Context, view: View) {
        try {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val imagesDir =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_images")
            imagesDir.mkdirs()
            val imageFile = File(imagesDir, "screenshot.png")
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/png"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.share_app_message) + Constant.APPLICATION_ID_URL
            )
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(shareIntent, "Share Screenshot"))
        } catch (e: Exception) {
            getErrors(e)
        }
    }


    fun getMultipleColor(position: Int, context: Context?): Int {
        val itemColors = intArrayOf(
            R.color.color_blue,
            R.color.color_red,
            R.color.color_orange,
            R.color.color_green,
            R.color.color_violet
        )
        return ContextCompat.getColor(context!!, itemColors[position % itemColors.size])
    }

    fun getDateToString(date: Date?): String {
        val result: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
            dateFormat.format(date)
        } else {
            val dateFormat = java.text.SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
            dateFormat.format(date!!)
        }
        return result
    }

    fun getShareUrl(context: Context, url: String?) {
        try {
            if (!url.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Can't open", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            getErrors(e)
        }
    }


    fun getQuizList(): ArrayList<ModelCategory> {
        val quizList = ArrayList<ModelCategory>()
        quizList.add(
            ModelCategory(
                1,
                "Aptitude",
                R.drawable.aptitude_quiz,
                "aptitude",
                "831+",
                "#F0903C",
                "#664415",
                "#FFD596"
            )
        )
        quizList.add(
            ModelCategory(
                2,
                "Art",
                R.drawable.art_quiz,
                "art",
                "1206+",
                "#64AA7C",
                "#1F492A",
                "#B6EC8E"
            )
        )
        quizList.add(
            ModelCategory(
                3,
                "Computer",
                R.drawable.computer_quiz,
                "computer",
                "963+",
                "#6EB6F9",
                "#1B457D",
                "#B0E8FF"
            )
        )
        quizList.add(
            ModelCategory(
                4,
                "Economy",
                R.drawable.economy_quiz,
                "economy",
                "878+",
                "#5BC5D3",
                "#32747C",
                "#AAF3F8"
            )
        )
        quizList.add(
            ModelCategory(
                5,
                "Geography",
                R.drawable.geography_quiz,
                "geography",
                "1009+",
                "#D68CF8",
                "#5A1277",
                "#f8ddff"
            )
        )
        quizList.add(
            ModelCategory(
                6,
                "Hindi",
                R.drawable.hindi_quiz,
                "hindi",
                "656+",
                "#6EB6F9",
                "#1B457D",
                "#B0E8FF"
            )
        )
        quizList.add(
            ModelCategory(
                7,
                "History",
                R.drawable.history_quiz,
                "history",
                "2950+",
                "#64AA7C",
                "#1F492A",
                "#ABEDC5"
            )
        )
        quizList.add(
            ModelCategory(
                8,
                "Maths",
                R.drawable.math_quiz,
                "maths",
                "900+",
                "#F0903C",
                "#664415",
                "#FFD596"
            )
        )
        quizList.add(
            ModelCategory(
                9,
                "Politics",
                R.drawable.politics_quiz,
                "politics",
                "1201+",
                "#64AA7C",
                "#1F492A",
                "#B6EC8E"
            )
        )
        quizList.add(
            ModelCategory(
                10,
                "Random",
                R.drawable.random_quiz,
                "random",
                "2500+",
                "#6EB6F9",
                "#1B457D",
                "#B0E8FF"
            )
        )
        quizList.add(
            ModelCategory(
                11,
                "Science",
                R.drawable.science_quiz,
                "science",
                "621+",
                "#5BC5D3",
                "#32747C",
                "#AAF3F8"
            )
        )
        quizList.add(
            ModelCategory(
                12,
                "Sports",
                R.drawable.sports_quiz,
                "sports",
                "391+",
                "#D68CF8",
                "#5A1277",
                "#f8ddff"
            )
        )
        return quizList
    }


    fun feedbackApp(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(context.getString(R.string.developer_email))
            )
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_subject))
            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.send_feedback)
                )
            )
        } catch (e: Exception) {
            getErrors(e)
        }
    }


    fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.share_app_message) + BuildConfig.APPLICATION_ID
            )
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    context.getString(R.string.share_app_title)
                )
            )
        } catch (e: Exception) {
            getErrors(e)
        }
    }


    fun versionName(): String {
        return "Version: ${BuildConfig.VERSION_NAME}"
    }


    fun getColor(): ArrayList<ModelColor> {
        val color = ArrayList<ModelColor>()
        color.add(
            ModelColor(
                1,
                "#F0903C",
                "#664415",
                "#FFD596"
            )
        )
        color.add(
            ModelColor(
                2,
                "#64AA7C",
                "#1F492A",
                "#B6EC8E"
            )
        )
        color.add(
            ModelColor(
                3,
                "#6EB6F9",
                "#1B457D",
                "#B0E8FF"
            )
        )
        color.add(
            ModelColor(
                4,
                "#5BC5D3",
                "#32747C",
                "#AAF3F8"
            )
        )
        color.add(
            ModelColor(
                5,

                "#D68CF8",
                "#5A1277",
                "#f8ddff"
            )
        )
        color.add(
            ModelColor(
                6,

                "#6EB6F9",
                "#1B457D",
                "#B0E8FF"
            )
        )
        color.add(
            ModelColor(
                7,

                "#64AA7C",
                "#1F492A",
                "#ABEDC5"
            )
        )
        color.add(
            ModelColor(
                8,

                "#F0903C",
                "#664415",
                "#FFD596"
            )
        )
        color.add(
            ModelColor(
                9,

                "#64AA7C",
                "#1F492A",
                "#B6EC8E"
            )
        )
        color.add(
            ModelColor(
                10,

                "#6EB6F9",
                "#1B457D",
                "#B0E8FF"
            )
        )
        color.add(
            ModelColor(
                11,

                "#5BC5D3",
                "#32747C",
                "#AAF3F8"
            )
        )
        color.add(
            ModelColor(
                12,
                "#D68CF8",
                "#5A1277",
                "#f8ddff"
            )
        )
        return color
    }

}
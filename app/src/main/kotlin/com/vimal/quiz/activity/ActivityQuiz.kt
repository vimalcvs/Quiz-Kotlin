package com.vimal.quiz.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vimal.quiz.R
import com.vimal.quiz.databases.CallbackQuiz
import com.vimal.quiz.databinding.ActivityQuizViewBinding
import com.vimal.quiz.databinding.DialogQuizExitBinding
import com.vimal.quiz.databinding.DialogQuizHintBinding
import com.vimal.quiz.db.Repository
import com.vimal.quiz.model.ModelQuizLive
import com.vimal.quiz.model.ModelQuizQuestion
import com.vimal.quiz.model.ModelQuizResult
import com.vimal.quiz.model.ModelQuizScore
import com.vimal.quiz.rests.RestAdapters
import com.vimal.quiz.utils.Constant
import com.vimal.quiz.utils.Constant.PAYLOAD_QUESTION_SERVICE
import com.vimal.quiz.utils.Utils.getDateToString
import com.vimal.quiz.utils.Utils.getErrors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class ActivityQuiz : AppCompatActivity() {
    private var questionAmount = 0
    private val handler = Handler(Looper.getMainLooper())
    private val quizResultsList: MutableList<ModelQuizResult?> = ArrayList()

    private val repository: Repository by lazy { Repository.getInstance(this)!! }

    private var typeCategory: String? = null
    private lateinit var binding: ActivityQuizViewBinding
    private var animation: Animation? = null
    private var countDownTimer: CountDownTimer? = null
    private var time = 0.0
    private var numCorrectAnswers = 0
    private var numCountQuestion = -1
    private var numIncorrectAnswers = 0
    private var options: MutableList<String>? = null
    private var currentQuestion: ModelQuizQuestion? = null
    private var quizModel: ModelQuizLive? = null
    private var buttonClickedRecently = false
    private var userAnswer: String? = null
    private var call: Call<CallbackQuiz>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizViewBinding.inflate(
            layoutInflater
        )
        setContentView(binding.getRoot())

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                alertDialogBack()
            }
        })

        questionAmount = intent.getIntExtra(Constant.TOTAL_QUESTION, 25)
        typeCategory = intent.getStringExtra(Constant.QUIZ_TYPE_CATEGORY)



        binding.toolbar.setNavigationOnClickListener { alertDialogBack() }
        binding.icInclude.tvHint.setOnClickListener { alertDialogHint() }

        binding.btQuizError.setOnClickListener { loadQuizApi() }
        loadQuizApi()

        animation = AnimationUtils.loadAnimation(this, R.anim.answer_correct)
        startStopTapped()

        quizModel = ViewModelProviders.of(this)[ModelQuizLive::class.java]

        optionClick()

        startCountdownTimer()
    }

    private fun loadQuizApi() {
        binding.quizRlProgress.visibility = View.VISIBLE
        binding.constraintLayouts.visibility = View.GONE

        call = RestAdapters.createAPI(this).getQuiz(questionAmount, typeCategory)
        call!!.enqueue(object : Callback<CallbackQuiz> {
            override fun onResponse(
                call: Call<CallbackQuiz>,
                response: Response<CallbackQuiz>
            ) {
                val resp = response.body()
                if (resp != null && resp.response_code == Constant.SUCCESS) {
                    quizModel!!.questions = resp.results
                    generateQuizData()
                } else {
                    Toast.makeText(
                        this@ActivityQuiz,
                        "No Signal. Please check your internet connection.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CallbackQuiz>, t: Throwable) {
                if (!call.isCanceled) {
                    binding.quizLlError.visibility = View.VISIBLE
                    binding.constraintLayouts.visibility = View.GONE
                    Toast.makeText(
                        this@ActivityQuiz,
                        "No Signal. Please check your internet connection.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        })
    }


    private fun generateQuizData() {
        try {
            val nextQuestion = quizModel?.nextQuestion()
            if (nextQuestion != null) {
                currentQuestion = nextQuestion
                options = ArrayList()
                (options as ArrayList<String>).add(currentQuestion!!.correct_answer)
                Collections.addAll(options!!, *currentQuestion!!.incorrect_answers)
                (options as ArrayList<String>).shuffle()
                displayQuiz()
                binding.quizRlProgress.visibility = View.GONE
                binding.constraintLayouts.visibility = View.VISIBLE
                if (countDownTimer != null) {
                    countDownTimer!!.cancel()
                }
                startCountdownTimer()
                numCountQuestion++
                startQueCount()
                startButtonClickDelay()
            } else {
                Toast.makeText(
                    this@ActivityQuiz,
                    "No Signal. Please check your internet connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            getErrors(e)
        }
    }


    private fun displayQuiz() {
        try {
            binding.icInclude.txtQuestion.text = currentQuestion!!.question
            val animationUp = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom)
            val animation = AnimationUtils.loadAnimation(this, R.anim.down_from_top)
            binding.icInclude.cardOption1.startAnimation(animation)
            binding.icInclude.cardOption2.startAnimation(animation)
            binding.icInclude.cardOption3.startAnimation(animation)
            binding.icInclude.cardOption4.startAnimation(animation)
            binding.icInclude.txtQuestion.startAnimation(animationUp)
            binding.icInclude.cardOption1.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.quiz_btn_color_normal
                )
            )
            binding.icInclude.btnOption1.text = options!![0]
            binding.icInclude.btnOption1.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
            )
            binding.icInclude.txtOption1.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
            )
            binding.icInclude.card1SelectRight.setVisibility(View.GONE)
            binding.icInclude.card1SelectWrong.setVisibility(View.GONE)
            binding.icInclude.cardOption2.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.quiz_btn_color_normal
                )
            )
            binding.icInclude.btnOption2.text = options!![1]
            binding.icInclude.btnOption2.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
            )
            binding.icInclude.txtOption2.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
            )
            binding.icInclude.card2SelectRight.setVisibility(View.GONE)
            binding.icInclude.card2SelectWrong.setVisibility(View.GONE)
            binding.icInclude.cardOption3.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.quiz_btn_color_normal
                )
            )
            binding.icInclude.btnOption3.text = options!![2]
            binding.icInclude.btnOption3.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
            )
            binding.icInclude.txtOption3.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
            )
            binding.icInclude.card3SelectRight.setVisibility(View.GONE)
            binding.icInclude.card3SelectWrong.setVisibility(View.GONE)
            binding.icInclude.cardOption4.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.quiz_btn_color_normal
                )
            )
            binding.icInclude.btnOption4.text = options!![3]
            binding.icInclude.btnOption4.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
            )
            binding.icInclude.txtOption4.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.text_primary
                )
            )
            binding.icInclude.card4SelectRight.setVisibility(View.GONE)
            binding.icInclude.card4SelectWrong.setVisibility(View.GONE)
        } catch (e: Exception) {
            getErrors(e)
        }
    }

    private fun optionClick() {
        binding.icInclude.cardOption1.setOnClickListener {
            try {
                if (!buttonClickedRecently) {
                    buttonClickedRecently = true
                    userAnswer = binding.icInclude.btnOption1.getText().toString()
                    quizResultsList.add(
                        ModelQuizResult(
                            currentQuestion!!.question,
                            currentQuestion!!.correct_answer,
                            userAnswer!!
                        )
                    )
                    disableButtons()
                    if (binding.icInclude.btnOption1.getText() == currentQuestion!!.correct_answer) {
                        binding.icInclude.cardOption1.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.quiz_btn_color_right
                            )
                        )
                        binding.icInclude.btnOption1.setTextColor(Color.WHITE)
                        binding.icInclude.txtOption1.setTextColor(Color.WHITE)
                        binding.icInclude.card1SelectRight.setVisibility(View.VISIBLE)
                        quizModel!!.incrementScore()
                        numCorrectAnswers++
                    } else {
                        binding.icInclude.cardOption1.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.quiz_btn_color_wrong
                            )
                        )
                        binding.icInclude.card1SelectWrong.setVisibility(View.VISIBLE)
                        highlightCorrectAnswer()
                        binding.icInclude.btnOption1.setTextColor(Color.WHITE)
                        binding.icInclude.txtOption1.setTextColor(Color.WHITE)
                        numIncorrectAnswers++
                    }
                    if (quizModel!!.currentQueIndex < quizModel!!.totalQuestions() - 1) {
                        handler.postDelayed(
                            { generateQuizData() },
                            Constant.QUESTION_CHANGE.toLong()
                        )
                    } else {
                        endQuizScore()
                    }
                }
            } catch (e: Exception) {
                getErrors(e)
            }
        }
        binding.icInclude.cardOption2.setOnClickListener {
            try {
                if (!buttonClickedRecently) {
                    buttonClickedRecently = true
                    userAnswer = binding.icInclude.btnOption2.getText().toString()
                    quizResultsList.add(
                        ModelQuizResult(
                            currentQuestion!!.question,
                            currentQuestion!!.correct_answer,
                            userAnswer!!
                        )
                    )
                    disableButtons()
                    if (binding.icInclude.btnOption2.getText() == currentQuestion!!.correct_answer) {

                        binding.icInclude.cardOption2.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.quiz_btn_color_right
                            )
                        )

                        binding.icInclude.btnOption2.setTextColor(Color.WHITE)
                        binding.icInclude.card2SelectRight.setVisibility(View.VISIBLE)
                        binding.icInclude.txtOption2.setTextColor(Color.WHITE)
                        quizModel!!.incrementScore()
                        numCorrectAnswers++
                    } else {


                        binding.icInclude.cardOption2.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.quiz_btn_color_wrong
                            )
                        )

                        highlightCorrectAnswer()
                        binding.icInclude.card2SelectWrong.setVisibility(View.VISIBLE)
                        binding.icInclude.txtOption2.setTextColor(Color.WHITE)
                        binding.icInclude.btnOption2.setTextColor(Color.WHITE)
                        numIncorrectAnswers++
                    }
                    if (quizModel!!.currentQueIndex < quizModel!!.totalQuestions() - 1) {
                        handler.postDelayed(
                            { generateQuizData() },
                            Constant.QUESTION_CHANGE.toLong()
                        )
                    } else {
                        endQuizScore()
                    }
                }
            } catch (e: Exception) {
                getErrors(e)
            }
        }
        binding.icInclude.cardOption3.setOnClickListener {
            try {
                if (!buttonClickedRecently) {
                    buttonClickedRecently = true
                    userAnswer = binding.icInclude.btnOption3.getText().toString()
                    quizResultsList.add(
                        ModelQuizResult(
                            currentQuestion!!.question,
                            currentQuestion!!.correct_answer,
                            userAnswer!!
                        )
                    )
                    disableButtons()
                    if (binding.icInclude.btnOption3.getText() == currentQuestion!!.correct_answer) {

                        binding.icInclude.cardOption3.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.quiz_btn_color_right
                            )
                        )

                        binding.icInclude.btnOption3.setTextColor(Color.WHITE)
                        binding.icInclude.card3SelectRight.setVisibility(View.VISIBLE)
                        binding.icInclude.txtOption3.setTextColor(Color.WHITE)
                        quizModel!!.incrementScore()
                        numCorrectAnswers++
                    } else {

                        binding.icInclude.cardOption3.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.quiz_btn_color_wrong
                            )
                        )

                        binding.icInclude.btnOption3.setTextColor(Color.WHITE)
                        binding.icInclude.txtOption3.setTextColor(Color.WHITE)
                        binding.icInclude.card3SelectWrong.setVisibility(View.VISIBLE)
                        highlightCorrectAnswer()
                        numIncorrectAnswers++
                    }
                    if (quizModel!!.currentQueIndex < quizModel!!.totalQuestions() - 1) {
                        handler.postDelayed(
                            { generateQuizData() },
                            Constant.QUESTION_CHANGE.toLong()
                        )
                    } else {
                        endQuizScore()
                    }
                }
            } catch (e: Exception) {
                getErrors(e)
            }
        }
        binding.icInclude.cardOption4.setOnClickListener {
            try {
                if (!buttonClickedRecently) {
                    buttonClickedRecently = true
                    userAnswer = binding.icInclude.btnOption4.getText().toString()
                    quizResultsList.add(
                        ModelQuizResult(
                            currentQuestion!!.question,
                            currentQuestion!!.correct_answer,
                            userAnswer!!
                        )
                    )
                    disableButtons()
                    if (binding.icInclude.btnOption4.getText() == currentQuestion!!.correct_answer) {

                        binding.icInclude.cardOption4.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.quiz_btn_color_right
                            )
                        )

                        binding.icInclude.btnOption4.setTextColor(Color.WHITE)
                        binding.icInclude.card4SelectRight.setVisibility(View.VISIBLE)
                        binding.icInclude.txtOption4.setTextColor(Color.WHITE)
                        quizModel!!.incrementScore()
                        numCorrectAnswers++
                    } else {


                        binding.icInclude.cardOption4.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.quiz_btn_color_wrong
                            )
                        )

                        binding.icInclude.btnOption4.setTextColor(Color.WHITE)
                        binding.icInclude.card4SelectWrong.setVisibility(View.VISIBLE)
                        binding.icInclude.txtOption4.setTextColor(Color.WHITE)
                        highlightCorrectAnswer()
                        numIncorrectAnswers++
                    }
                    if (quizModel!!.currentQueIndex < quizModel!!.totalQuestions() - 1) {
                        handler.postDelayed(
                            { generateQuizData() },
                            Constant.QUESTION_CHANGE.toLong()
                        )
                    } else {
                        endQuizScore()
                    }
                }
            } catch (e: Exception) {
                getErrors(e)
            }
        }
        binding.icInclude.tvSkip.setOnClickListener {
            try {
                if (quizModel!!.currentQueIndex < quizModel!!.totalQuestions() - 1) {
                    handler.postDelayed({ generateQuizData() }, Constant.QUESTION_CHANGE.toLong())
                } else {
                    endQuizScore()
                }
                startQueCount()
            } catch (e: Exception) {
                getErrors(e)
            }
        }
    }

    private fun highlightCorrectAnswer() {
        try {
            when (currentQuestion!!.correct_answer) {
                binding.icInclude.btnOption1.getText() -> {
                    binding.icInclude.cardOption1.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.quiz_btn_color_right
                        )
                    )
                    binding.icInclude.card1SelectRight.setVisibility(View.VISIBLE)
                    binding.icInclude.btnOption1.setEnabled(true)
                    binding.icInclude.btnOption1.setTextColor(Color.WHITE)
                    binding.icInclude.txtOption1.setTextColor(Color.WHITE)
                    binding.icInclude.cardOption1.startAnimation(animation)
                    startQueCount()
                }

                binding.icInclude.btnOption2.getText() -> {
                    binding.icInclude.cardOption2.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.quiz_btn_color_right
                        )
                    )
                    binding.icInclude.btnOption2.setEnabled(true)
                    binding.icInclude.card2SelectRight.setVisibility(View.VISIBLE)
                    binding.icInclude.txtOption2.setTextColor(Color.WHITE)
                    binding.icInclude.btnOption2.setTextColor(Color.WHITE)
                    binding.icInclude.cardOption2.startAnimation(animation)
                    startQueCount()
                }

                binding.icInclude.btnOption3.getText() -> {
                    binding.icInclude.cardOption3.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.quiz_btn_color_right
                        )
                    )
                    binding.icInclude.btnOption3.setEnabled(true)
                    binding.icInclude.card3SelectRight.setVisibility(View.VISIBLE)
                    binding.icInclude.txtOption3.setTextColor(Color.WHITE)
                    binding.icInclude.btnOption3.setTextColor(Color.WHITE)
                    binding.icInclude.cardOption3.startAnimation(animation)
                    startQueCount()
                }

                binding.icInclude.btnOption4.getText() -> {
                    binding.icInclude.cardOption4.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.quiz_btn_color_right
                        )
                    )
                    binding.icInclude.btnOption4.setEnabled(true)
                    binding.icInclude.card4SelectRight.setVisibility(View.VISIBLE)
                    binding.icInclude.txtOption4.setTextColor(Color.WHITE)
                    binding.icInclude.btnOption4.setTextColor(Color.WHITE)
                    binding.icInclude.cardOption4.startAnimation(animation)
                    startQueCount()
                }
            }
        } catch (e: Exception) {
            getErrors(e)
        }
    }

    private fun disableButtons() {
        binding.icInclude.btnOption1.setEnabled(false)
        binding.icInclude.btnOption2.setEnabled(false)
        binding.icInclude.btnOption3.setEnabled(false)
        binding.icInclude.btnOption4.setEnabled(false)
    }

    private fun enableButtons() {
        binding.icInclude.btnOption1.setEnabled(true)
        binding.icInclude.btnOption2.setEnabled(true)
        binding.icInclude.btnOption3.setEnabled(true)
        binding.icInclude.btnOption4.setEnabled(true)
    }

    private fun startButtonClickDelay() {
        handler.removeCallbacks {
            buttonClickedRecently = false
            enableButtons()
        }
        handler.postDelayed({
            buttonClickedRecently = false
            enableButtons()
        }, 10)
    }

    private fun startQueCount() {
        val string = "Question $numCountQuestion/$questionAmount"
        binding.icInclude.tvTotalQus.text = string
        binding.icInclude.tvWrong.text = numIncorrectAnswers.toString()
        binding.icInclude.tvRight.text = numCorrectAnswers.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        if (call != null) {
            call!!.cancel()
        }
    }

    private fun startCountdownTimer() {
        try {
            countDownTimer = object : CountDownTimer((30 * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = (millisUntilFinished / 1000).toInt()
                    binding.icInclude.tvTime.text = secondsRemaining.toString()
                    binding.icInclude.cpiProgress.setMax(30)
                    binding.icInclude.cpiProgress.progress = secondsRemaining
                }

                override fun onFinish() {
                    binding.icInclude.tvTime.text = resources.getString(R.string.zero)
                }
            }
            (countDownTimer as CountDownTimer).start()
        } catch (e: Exception) {
            getErrors(e)
        }
    }

    private fun endQuizScore() {
        try {
            val intent = Intent(this, ActivityQuizResult::class.java)
            intent.putExtra(Constant.TOTAL_TIME_USE, timerText)
            intent.putExtra(Constant.TOTAL_CORRECT_ANSWER, numCorrectAnswers)
            intent.putExtra(Constant.TOTAL_INCORRECT_ANSWER, numIncorrectAnswers)
            intent.putExtra(Constant.TOTAL_QUESTION, questionAmount)
            intent.putParcelableArrayListExtra(
                PAYLOAD_QUESTION_SERVICE,
                quizResultsList as ArrayList<out Parcelable?>
            )
            startActivity(intent)
            numCountQuestion++
            val percentCorrect = (numCorrectAnswers.toFloat() / questionAmount * 100).toInt()

            val list = ModelQuizScore(
                0,
                getDateToString(Date()),
                percentCorrect,
                numCountQuestion,
                currentQuestion!!.category
            )

            repository.insertScore(list)
            finish()

        } catch (e: Exception) {
            getErrors(e)
        }
    }

    private fun startStopTapped() {
        val timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    time++
                    binding.tvToolbar.text = timerText
                }
            }
        }
        Timer().schedule(timerTask, 0, 1000)
    }


    private val timerText: String
        get() {
            val rounded = Math.round(time).toInt()
            val seconds = rounded % 86400 % 3600 % 60
            val minutes = rounded % 86400 % 3600 / 60
            return formatTime(seconds, minutes)
        }

    private fun formatTime(seconds: Int, minutes: Int): String {
        return String.format(Locale.getDefault(), "Time: %02d:%02d", minutes, seconds)
    }

    private fun alertDialogBack() {
        try {
            val binding = DialogQuizExitBinding.inflate(LayoutInflater.from(this))
            val builder = MaterialAlertDialogBuilder(this, R.style.CustomDialog)
            builder.setView(binding.getRoot())
            builder.setCancelable(true)
            val dialog = builder.create()
            val window = dialog.window
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.tvGo.setOnClickListener { finish() }
            binding.tvCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        } catch (e: Exception) {
            getErrors(e)
        }
    }

    private fun alertDialogHint() {
        try {
            val binding = DialogQuizHintBinding.inflate(LayoutInflater.from(this))
            val builder = MaterialAlertDialogBuilder(this, R.style.CustomDialog)
            builder.setView(binding.getRoot())
            builder.setCancelable(true)
            val dialog = builder.create()
            val window = dialog.window
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.tvSubtitle.text = currentQuestion!!.exp
            binding.ivClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
        } catch (e: Exception) {
            getErrors(e)
        }
    }
}

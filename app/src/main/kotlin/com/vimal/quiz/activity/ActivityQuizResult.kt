package com.vimal.quiz.activity

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vimal.quiz.R
import com.vimal.quiz.adapter.AdapterQuizResult
import com.vimal.quiz.databinding.ActivityQuizResultBinding
import com.vimal.quiz.databinding.BottomRecyclerviewBinding
import com.vimal.quiz.model.ModelQuizResult
import com.vimal.quiz.utils.Constant
import com.vimal.quiz.utils.Utils
import com.vimal.quiz.utils.Utils.getErrors
import com.vimal.quiz.utils.Utils.getParcelableArrayListExtraCompat


class ActivityQuizResult : AppCompatActivity() {

    private var list: List<ModelQuizResult>? = null
    private lateinit var binding: ActivityQuizResultBinding
    private var totalTimeUse: String? = null
    private var totalCorrectAnswer = 0
    private var totalIncorrectAnswer = 0
    private var totalQuestion = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(
            layoutInflater
        )
        setContentView(binding.getRoot())
        Utils.getVibrator(this, 500)

        binding.toolbar.setNavigationOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        list = intent.getParcelableArrayListExtraCompat(Constant.PAYLOAD_QUESTION_SERVICE)
        totalTimeUse = intent.getStringExtra(Constant.TOTAL_TIME_USE)
        totalCorrectAnswer = intent.getIntExtra(Constant.TOTAL_CORRECT_ANSWER, 0)
        totalIncorrectAnswer = intent.getIntExtra(Constant.TOTAL_INCORRECT_ANSWER, 0)
        totalQuestion = intent.getIntExtra(Constant.TOTAL_QUESTION, 0)


        if (totalTimeUse != null) {
            binding.tvTimeDl.text = totalTimeUse
        }

        binding.txtDialogMessage.text = totalCorrectAnswer.toString()
        binding.wrong.text = totalIncorrectAnswer.toString()


        var totalCorrectPercentage = 0
        if (totalQuestion > 0) {
            totalCorrectPercentage =
                (totalCorrectAnswer.toDouble() / totalQuestion * 100).toInt()
        }
        if (totalCorrectPercentage > 100) {
            totalCorrectPercentage = 100
        }


        binding.tvTime.text = totalCorrectPercentage.toString()
        binding.cpiProgress.setMax(100)

        binding.cpiProgress.progress = totalCorrectPercentage
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        if (totalCorrectPercentage >= 40) {
            binding.tvCongratulations.text = getString(R.string.result_win)
            binding.ivAnimation.startAnimation(rotateAnimation)
            binding.lvAnimationView.setAnimation(R.raw.winner)
            binding.lvAnimationView.playAnimation()
        } else {
            binding.tvCongratulations.text = getString(R.string.result_fail)
            binding.ivAnimation.startAnimation(rotateAnimation)
            binding.lvAnimationView.setAnimation(R.raw.fail)
            binding.lvAnimationView.playAnimation()
        }

        binding.tvShare.setOnClickListener {
            Utils.getScreenshotShare(
                this,
                binding.constraintLayout
            )
        }

        binding.tvHome.setOnClickListener { finish() }
        binding.tvAnswer.setOnClickListener { showAnswerKeys() }
    }

    private fun showAnswerKeys() {
        try {
            val bottomSheetDialog = BottomSheetDialog(this)
            val binding = BottomRecyclerviewBinding.inflate(this.layoutInflater)
            bottomSheetDialog.setContentView(binding.getRoot())

            binding.rvRecycler.setLayoutManager(LinearLayoutManager(this))
            binding.rvRecycler.setAdapter(AdapterQuizResult(list!!))

            binding.ivClose.setOnClickListener { bottomSheetDialog.dismiss() }
            bottomSheetDialog.show()
        } catch (e: java.lang.Exception) {
            getErrors(e)
        }
    }
}

package com.vimal.quiz.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vimal.quiz.R
import com.vimal.quiz.adapter.AdapterQuizStart
import com.vimal.quiz.databinding.ActivityQuizStartBinding
import com.vimal.quiz.databinding.BottomQuizTotalBinding
import com.vimal.quiz.model.ModelCategory
import com.vimal.quiz.model.ModelQuizStart
import com.vimal.quiz.utils.Constant
import com.vimal.quiz.utils.Utils
import com.vimal.quiz.utils.Utils.getSerializableExtraCompat

class ActivityQuizStart : AppCompatActivity() {

    private var model: ModelCategory? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityQuizStartBinding.inflate(
            layoutInflater
        )
        setContentView(binding.getRoot())

        binding.toolbar.setNavigationOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        model = intent.getSerializableExtraCompat(Constant.QUIZ_TYPE_CATEGORY)

        val colorInt = Color.parseColor(model!!.colorPrimary)
        binding.clConstraint.backgroundTintList = ColorStateList.valueOf(colorInt)
        binding.ctToolbar.setContentScrimColor(colorInt)
        binding.ctToolbar.setStatusBarScrimColor(colorInt)
        binding.tvStudentName1.text = model!!.totalQuestions
        binding.txtChannelName.text = model!!.name

        binding.rvRecycler.setLayoutManager(LinearLayoutManager(this))
        val adapter = AdapterQuizStart(generateNumbers(), colorInt)
        binding.rvRecycler.setAdapter(adapter)
        adapter.setOnItemClickListener { _: ModelQuizStart? -> startQuiz() }


        binding.appbar.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                try {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.totalScrollRange
                    }
                    if (scrollRange + verticalOffset == 0) {
                        binding.tvToolbar.text =
                            getResources().getString(R.string.mock_test)
                        isShow = true
                    } else if (isShow) {
                        binding.tvToolbar.text = resources.getString(R.string.empty_string)
                        isShow = false
                    }
                } catch (e: Exception) {
                    Utils.getErrors(e)
                }
            }
        })
    }

    private fun generateNumbers(): List<ModelQuizStart> {
        val numbers: MutableList<ModelQuizStart> = ArrayList()
        numbers.add(ModelQuizStart(1, "Mock Test-1", "Qus: 256+"))
        numbers.add(ModelQuizStart(2, "Mock Test-2", "Qus 336+"))
        numbers.add(ModelQuizStart(3, "Mock Test-3", "Qus 178+"))
        numbers.add(ModelQuizStart(4, "Mock Test-4", "Qus 254+"))
        numbers.add(ModelQuizStart(5, "Mock Test-5", "Qus 276+"))
        numbers.add(ModelQuizStart(6, "Mock Test-6", "Qus 516+"))
        numbers.add(ModelQuizStart(7, "Mock Test-7", "Qus 513+"))
        numbers.add(ModelQuizStart(8, "Mock Test-8", "Qus 256+"))
        numbers.add(ModelQuizStart(5, "Mock Test-9", "Qus 556+"))
        numbers.add(ModelQuizStart(6, "Mock Test-10", "Qus 356+"))
        return numbers
    }

    private fun startQuiz() {
        try {
            val bottomSheetDialog = BottomSheetDialog(this)
            val binding = BottomQuizTotalBinding.inflate(this.layoutInflater)
            bottomSheetDialog.setContentView(binding.getRoot())

            binding.etEditText.setText(R.string.dialog_twenty_five)
            binding.etEditText.requestFocus()

            val colorInt = Color.parseColor(model!!.colorPrimary)
            val colorCard = Color.parseColor(model!!.colorDark)
            binding.etEditText.setBackgroundTintList(ColorStateList.valueOf(colorInt))
            binding.dialogCategoryButton.backgroundTintList = ColorStateList.valueOf(colorCard)

            val colorLight = Color.parseColor(model!!.colorPrimary)

            binding.dialogChipAny.chipBackgroundColor = ColorStateList.valueOf(colorLight)
            binding.dialogChipEasy.chipBackgroundColor = ColorStateList.valueOf(colorLight)
            binding.dialogChipMedium.chipBackgroundColor = ColorStateList.valueOf(colorLight)
            binding.dialogChipDifficult.chipBackgroundColor = ColorStateList.valueOf(colorLight)


            binding.etEditText.setSelection(binding.etEditText.getText().length)
            binding.etEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    try {
                        var inputText = s.toString()
                        if (inputText.isEmpty()) {
                            inputText = "25"
                        }
                        val isValid: Boolean = try {
                            val value = inputText.toInt()
                            value in 1..100
                        } catch (e: NumberFormatException) {
                            false
                        }
                        binding.dialogCategoryButton.setEnabled(isValid)
                        binding.tvError.visibility = if (isValid) View.GONE else View.VISIBLE
                    } catch (e: Exception) {
                        Utils.getErrors(e)
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
            binding.dialogCategoryButton.setOnClickListener {
                try {
                    val inputText: String =
                        binding.etEditText.getText().toString().ifEmpty { "25" }
                    val intent = Intent(this, ActivityQuiz::class.java)
                    intent.putExtra(Constant.TOTAL_QUESTION, inputText.toInt())
                    intent.putExtra(Constant.QUIZ_TYPE_CATEGORY, model!!.category)
                    startActivity(intent)
                    bottomSheetDialog.dismiss()
                } catch (e: Exception) {
                    Utils.getErrors(e)
                }
            }
            binding.ivClose.setOnClickListener { bottomSheetDialog.dismiss() }
            bottomSheetDialog.show()
        } catch (e: Exception) {
            Utils.getErrors(e)
        }
    }
}
package com.vimal.quiz.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vimal.quiz.R
import com.vimal.quiz.adapter.AdapterQuizScore
import com.vimal.quiz.databinding.ActivityRecyclerviewBinding
import com.vimal.quiz.databinding.DialogQuizDeleteBinding
import com.vimal.quiz.db.Repository

class ActivityQuizScore : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerviewBinding
    private lateinit var adapter: AdapterQuizScore
    private val repository: Repository by lazy { Repository.getInstance(this)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.inflateMenu(R.menu.menu_quiz_delete)
        binding.tvToolbar.text = getString(R.string.score)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.nav_delete) {
                deleteAllScores()
                true
            } else {
                false
            }
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.ivInclude.tvError.setText(R.string.quiz_score_empty)
        binding.ivInclude.ivError.setImageResource(R.drawable.ic_no_score)
        binding.ivInclude.btError.visibility = View.GONE

        adapter = AdapterQuizScore(this)
        repository.allScore().observe(this) { products ->
            if (products.isNotEmpty()) {
                adapter.submitList(products)
                binding.ivInclude.llError.visibility = View.GONE
            } else {
                adapter.submitList(ArrayList())
                binding.ivInclude.llError.visibility = View.VISIBLE

            }
        }
        binding.rvRecycler.layoutManager = LinearLayoutManager(this)
        binding.rvRecycler.adapter = adapter

    }

    private fun deleteAllScores() {
        val dialogBinding = DialogQuizDeleteBinding.inflate(LayoutInflater.from(this))
        val builder = MaterialAlertDialogBuilder(this, R.style.CustomDialog)
        builder.setView(dialogBinding.getRoot())
        builder.setCancelable(true)
        val dialog = builder.create()
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvGo.setOnClickListener {
            repository.deleteAllScore()
            dialog.dismiss()
            setupRecyclerView()
        }
        dialogBinding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
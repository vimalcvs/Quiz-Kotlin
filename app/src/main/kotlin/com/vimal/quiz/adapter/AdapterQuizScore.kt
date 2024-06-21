package com.vimal.quiz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vimal.quiz.R
import com.vimal.quiz.databinding.ItemQuizScoreBinding
import com.vimal.quiz.model.ModelQuizScore
import com.vimal.quiz.utils.Utils
import java.util.Locale

class AdapterQuizScore(private var context: Context) :
    RecyclerView.Adapter<AdapterQuizScore.ScoreViewHolder>() {

    private var scores = listOf<ModelQuizScore>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ItemQuizScoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]

        holder.binding.txtDate.text = score.date
        holder.binding.txtQuestion.text = score.question.toString()

        val category = score.category.substring(0, 1)
            .uppercase(Locale.getDefault()) + score.category.substring(1)
        holder.binding.txtCategory.text = category

        val scoreText = String.format(context.getString(R.string.score_format), score.score)
        holder.binding.txtScore.text = scoreText



        holder.binding.viewBackgroundList.setBackgroundColor(
            Utils.getMultipleColor(
                position,
                holder.itemView.context
            )
        )

        holder.binding.imgAppleList.setColorFilter(
            Utils.getMultipleColor(
                position,
                holder.itemView.context
            )
        )

    }

    fun submitList(newScores: List<ModelQuizScore>) {
        scores = newScores
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = scores.size

    class ScoreViewHolder(val binding: ItemQuizScoreBinding) : RecyclerView.ViewHolder(binding.root)
}
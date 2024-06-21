package com.vimal.quiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vimal.quiz.R
import com.vimal.quiz.databinding.ItemQuizResultBinding
import com.vimal.quiz.model.ModelQuizResult
import com.vimal.quiz.utils.Utils

class AdapterQuizResult(private val scores: List<ModelQuizResult>) :

    RecyclerView.Adapter<AdapterQuizResult.ScoreViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        return ScoreViewHolder(
            ItemQuizResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]

        holder.binding.tvQuestion.text = Utils.fromHtml(score.question)

        holder.binding.tvAnswer.text = score.rightAnswer

        holder.binding.tvUseAnswer.text = score.userAnswer

        val color = if (score.rightAnswer == score.userAnswer) {
            ContextCompat.getColor(holder.itemView.context, R.color.quiz_btn_color_right)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.quiz_btn_color_wrong)
        }
        holder.binding.ivColor.setBackgroundColor(color)

        if (score.rightAnswer == score.userAnswer) {
            holder.binding.ivQuestion.setImageResource(R.drawable.ic_selected_right_white_bg)
        } else {
            holder.binding.ivQuestion.setImageResource(R.drawable.ic_selected_wrong_white_bg)
        }


    }

    override fun getItemCount(): Int {
        return scores.size
    }

    class ScoreViewHolder(val binding: ItemQuizResultBinding) : RecyclerView.ViewHolder(
        binding.getRoot()
    )
}
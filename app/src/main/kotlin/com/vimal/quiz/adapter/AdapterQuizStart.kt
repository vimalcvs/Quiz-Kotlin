package com.vimal.quiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vimal.quiz.databinding.ItemQuizStartBinding
import com.vimal.quiz.model.ModelQuizStart

class AdapterQuizStart(
    private val items: List<ModelQuizStart>,
    val color: Int
) :
    RecyclerView.Adapter<AdapterQuizStart.ViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemQuizStartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modelCourse = items[position]
        holder.binding.tvTitle.text = modelCourse.time
        holder.binding.tvQuestion.text = modelCourse.title

        holder.binding.ivPlay.setColorFilter(color)

        holder.binding.cvCard.setOnClickListener {
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(modelCourse)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun interface OnItemClickListener {
        fun onItemClick(obj: ModelQuizStart?)
    }

    class ViewHolder(val binding: ItemQuizStartBinding) : RecyclerView.ViewHolder(
        binding.getRoot()
    )
}
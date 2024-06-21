package com.vimal.quiz.adapter


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vimal.quiz.R
import com.vimal.quiz.databinding.ItemCategoryBinding
import com.vimal.quiz.model.ModelCategory
import com.vimal.quiz.utils.Constant.SHARED_ALPHABET
import com.vimal.quiz.utils.Utils
import java.util.concurrent.atomic.AtomicBoolean


class AdapterHome(
    val context: Context, private val list: List<ModelCategory>
) : RecyclerView.Adapter<AdapterHome.ViewHolder>() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_ALPHABET, Context.MODE_PRIVATE)

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.binding.tvTitle.text = model.name
        holder.binding.tvCount.text = model.totalQuestions

        holder.binding.tvTitle.setTextColor(Color.parseColor(model.colorDark))
        holder.binding.tvCount.setTextColor(Color.parseColor(model.colorPrimary))

        val atomicBoolean = AtomicBoolean(sharedPreferences.getBoolean(model.name, false))
        if (atomicBoolean.get()) {
            holder.binding.ivDot.visibility = View.GONE
        } else {
            holder.binding.ivDot.setImageResource(R.drawable.shape_dialog)
        }

        holder.binding.ivColor.setBackgroundColor(Color.parseColor(model.colorLight))



        holder.binding.ivImage.setImageResource(model.categoryImage)

        holder.binding.cvCard.setOnClickListener {
            try {
                mOnItemClickListener?.onItemClick(model)
                holder.binding.ivDot.visibility = View.GONE
                atomicBoolean.set(true)
                sharedPreferences.edit().putBoolean(model.name, atomicBoolean.get()).apply()
            } catch (e: Exception) {
                Utils.getErrors(e)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(modelLang: ModelCategory?)
    }

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}
package com.vimal.quiz.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vimal.quiz.activity.ActivityQuizStart
import com.vimal.quiz.adapter.AdapterHome
import com.vimal.quiz.databinding.FragmentHomeBinding
import com.vimal.quiz.model.ModelCategory
import com.vimal.quiz.utils.Constant
import com.vimal.quiz.utils.Constant.SHARED_ALPHABET
import com.vimal.quiz.utils.Utils
import com.vimal.quiz.utils.Utils.getQuizList
import java.util.Locale


class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.idLayouts.tvButton.setOnClickListener { Utils.shareApp(requireActivity()) }
        showList()
        calculateData()
    }

    private fun showList() {
        binding.rvRecycler.layoutManager = StaggeredGridLayoutManager(2, 1)
        val adapter = AdapterHome(requireActivity(), getQuizList())
        binding.rvRecycler.isNestedScrollingEnabled = false
        binding.rvRecycler.adapter = adapter
        adapter.setOnItemClickListener(object : AdapterHome.OnItemClickListener {
            override fun onItemClick(modelLang: ModelCategory?) {
                try {
                    val intent = Intent(context, ActivityQuizStart::class.java)
                    intent.putExtra(Constant.QUIZ_TYPE_CATEGORY, modelLang)
                    startActivity(intent)
                } catch (e: Exception) {
                    Utils.getErrors(e)
                }
            }
        })
    }

    private fun calculateData() {
        val count = calculateProgress(getQuizList())
        val stProgress = String.format(Locale.getDefault(), "%d Day Left", 12 - count)
        val stCount = String.format(Locale.getDefault(), "%.1f%%", (count / 12.0) * 100)
        binding.tvDayLeft.text = stProgress
        binding.tvProgress.text = stCount
        binding.progress.max = 12
        binding.progress.progress = count
    }


    private fun calculateProgress(list: List<ModelCategory>): Int {
        var onCount = 0
        for (model in list) {
            val isDay = requireContext().getSharedPreferences(
                SHARED_ALPHABET,
                Context.MODE_PRIVATE
            ).getBoolean(model.name, false)
            if (isDay) {
                onCount++
            }
        }
        return onCount
    }

    override fun onResume() {
        super.onResume()
        calculateData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
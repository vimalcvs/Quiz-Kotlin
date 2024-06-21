package com.vimal.quiz.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vimal.quiz.R
import com.vimal.quiz.adapter.AdapterQuizScore
import com.vimal.quiz.databinding.FragmentRecyclerviewBinding
import com.vimal.quiz.db.Repository

class FragmentScore : Fragment() {

    private val repository: Repository by lazy { Repository.getInstance(requireActivity())!! }

    private lateinit var adapter: AdapterQuizScore

    var binding: FragmentRecyclerviewBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecyclerviewBinding.inflate(inflater, container, false)
        val view: View = binding!!.getRoot()
        binding!!.ivInclude.tvError.setText(R.string.quiz_score_empty)
        binding!!.ivInclude.ivError.setImageResource(R.drawable.ic_no_score)
        binding!!.ivInclude.btError.visibility = View.GONE

        adapter = AdapterQuizScore(requireActivity())
        repository.allScore().observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                adapter.submitList(products)
                binding!!.ivInclude.llError.visibility = View.GONE
            } else {
                adapter.submitList(ArrayList())
                binding!!.ivInclude.llError.visibility = View.VISIBLE

            }
        }
        binding!!.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding!!.recyclerView.adapter = adapter

        return view
    }
}

package com.vimal.quiz.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vimal.quiz.activity.ActivityAppearance
import com.vimal.quiz.databinding.FragmentSettingsBinding
import com.vimal.quiz.utils.Constant
import com.vimal.quiz.utils.Utils

class FragmentSettings : Fragment() {


    private var binding: FragmentSettingsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view: View = binding!!.getRoot()
        binding!!.settingAppearanceLayout.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    ActivityAppearance::class.java
                )
            )
        }
        binding!!.settingPrivacyLayout.setOnClickListener {
            Utils.getShareUrl(
                requireActivity(),
                Constant.PRIVACY_POLICY
            )
        }

        binding!!.settingShareLayout.setOnClickListener { Utils.shareApp(requireContext()) }

        binding!!.settingFeedbackLayout.setOnClickListener { Utils.feedbackApp(requireContext()) }

        binding!!.tvVersionName.text = Utils.versionName()

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
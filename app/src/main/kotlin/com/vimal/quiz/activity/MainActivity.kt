package com.vimal.quiz.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vimal.quiz.R
import com.vimal.quiz.databinding.ActivityMainBinding
import com.vimal.quiz.databinding.DialogQuizDeleteBinding
import com.vimal.quiz.db.Repository


class MainActivity : AppCompatActivity() {

    private val repository: Repository by lazy { Repository.getInstance(this)!! }

    private var binding: ActivityMainBinding? = null
    private var nativeAd: NativeAd? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())
        binding!!.toolbar.setTitle(R.string.empty_string)
        setSupportActionBar(binding!!.toolbar)

        binding!!.bvNavigation.itemIconTintList = null
        val navController = findNavController(this, R.id.nav_fragment)
        setupWithNavController(binding!!.bvNavigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            try {
                binding!!.tvToolbar.text = destination.label
            } catch (e: Exception) {
                binding!!.tvToolbar.text = getString(R.string.app_name)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_quiz_delete, menu)
        when (findNavController(R.id.nav_fragment).currentDestination?.id) {
            R.id.navigation_home -> {
                menu.findItem(R.id.nav_delete).isVisible = false
            }

            R.id.navigation_score -> {
                menu.findItem(R.id.nav_delete).isVisible = false
                repository.allScore().observe(this) {
                    menu.findItem(R.id.nav_delete).isVisible = it.isNotEmpty()
                }
            }

            R.id.navigation_settings -> {
                menu.findItem(R.id.nav_delete).isVisible = false
            }

            else -> {
                menu.findItem(R.id.nav_delete).isVisible = false
            }
        }
        invalidateOptionsMenu()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_delete -> {
                deleteAllScores()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
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
        }
        dialogBinding.tvCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    override fun onDestroy() {
        nativeAd?.destroy()
        super.onDestroy()
    }
}
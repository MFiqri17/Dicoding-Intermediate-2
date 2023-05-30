package com.project.storyapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.storyapp.R
import com.project.storyapp.data.remote.response.Story
import com.project.storyapp.databinding.ActivityMainBinding
import com.project.storyapp.ui.adapter.StoryAdapter
import com.project.storyapp.ui.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.getToken().collect { token ->
                if (token !== null) {
                    val jwt = "Bearer $token"
                    showLoading()
                    viewModel.getListStories(
                        1,
                        20,
                        0,
                        jwt
                    ).collectLatest { result ->
                        hideLoading()
                        if (result.isSuccess) {
                            val response = result.getOrThrow()
                            setStoryListData(response.listStory)
                        } else {
                            showToast("Failed: ${result.exceptionOrNull()}")
                        }
                    }
                } else {
                    navigateToLoginActivity()
                }
            }
        }

        binding.btnAddStory.setOnClickListener{
            navigateToAddStoryActivity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_topbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                logOutHandler()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setStoryListData(storyList: List<Story>?) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val adapter = StoryAdapter(storyList ?: emptyList()) {story ->
            navigateToDetailActivity(story.id)
        }
        binding.rvStory.adapter = adapter
    }

    private fun logOutHandler() {
        lifecycleScope.launch {
            viewModel.logOut().collect {result ->
                if (result.isSuccess) {
                    showToast("Log Out Success")
                    navigateToLoginActivity()
                } else {
                    showToast("Log Out Failed ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAddStoryActivity() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDetailActivity(storyId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.STORY_ID, storyId)
        startActivity(intent)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

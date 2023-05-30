package com.project.storyapp.ui.activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.project.storyapp.data.remote.response.Story
import com.project.storyapp.databinding.ActivityDetailBinding
import com.project.storyapp.ui.viewModel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    companion object {
        const val STORY_ID = "STORY_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(STORY_ID)
        Log.d("DetailActivity", "Story ID: $storyId")

        observeStoryDetail(storyId)
    }

    private fun observeStoryDetail(storyId: String?) {
        lifecycleScope.launch {
            viewModel.getToken().collect { token ->
                if (token != null && storyId != null) {
                    val jwt = "Bearer $token"
                    showLoading()
                    viewModel.getStoryDetail(storyId, jwt).collect { result ->
                        hideLoading()
                        if (result.isSuccess) {
                            val storyDetail = result.getOrThrow()
                            setStoryDetail(storyDetail.storyDetail)
                        } else {
                            val errorMessage = result.exceptionOrNull()?.message
                            showToast("Detail Failed: $errorMessage")
                        }
                    }
                }
            }
        }
    }

    private fun setStoryDetail(storyDetail: Story?) {
        if (storyDetail != null) {
            Glide.with(this)
                .load(storyDetail.photoUrl)
                .into(binding.ivDetailPhoto)
            binding.tvDetailName.text = storyDetail.name
            binding.tvDetailDescription.text = storyDetail.description
        } else {
            showToast("Story detail is null")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.ivDetailPhoto.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.ivDetailPhoto.visibility = View.VISIBLE
    }
}

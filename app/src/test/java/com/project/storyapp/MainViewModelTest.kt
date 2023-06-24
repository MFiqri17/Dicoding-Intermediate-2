package com.project.storyapp

import MainDispatcher
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.project.storyapp.data.remote.response.Story
import com.project.storyapp.data.repository.AuthRepository
import com.project.storyapp.data.repository.StoryRepository
import com.project.storyapp.ui.adapter.StoryAdapter
import com.project.storyapp.ui.viewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcher = MainDispatcher()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: MainViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(storyRepository, authRepository)
    }

    @Test
    fun `test successful data loading`() = runBlocking {
        val authHeader = "dummy jwt"
        val expectedData = listOf(
            Story(
                "Fiqri",
                "A man",
                "1",
                "https://source.unsplash.com/random/300x300/?nature",
                "2021-01-01",
                4.5,
                20.0
            ),
            Story(
                "Fiqri 2",
                "A man 2",
                "2",
                "https://source.unsplash.com/random/300x300/?love",
                "2023-01-01",
                4.5,
                20.0
            )
        )
        val data: PagingData<Story> = StoryPagingSourceTestUtils.createSnapshot(expectedData)
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getListStories(authHeader)).thenReturn(expectedStory)

        // Act
        val actualStory = viewModel.getListStories(authHeader).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        // Assert
        assertNotNull(differ.snapshot())
        assertEquals(expectedData.size, differ.snapshot().size)
        assertEquals(expectedData[0], differ.snapshot()[0])
    }

    @Test
    fun `test no story data available`() = runBlocking {
        // Arrange
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data

        `when`(storyRepository.getListStories("Dummy jwt")).thenReturn(expectedStory)
        val actualStory: PagingData<Story> = viewModel.getListStories("Dummy jwt").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }

    companion object {
        val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {}
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }
    }
}


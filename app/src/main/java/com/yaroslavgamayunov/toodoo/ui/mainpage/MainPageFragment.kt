package com.yaroslavgamayunov.toodoo.ui.mainpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.databinding.FragmentMainPageBinding
import com.yaroslavgamayunov.toodoo.domain.common.Result
import com.yaroslavgamayunov.toodoo.ui.viewmodel.MainPageViewModel
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TooDooViewModelFactory
import com.yaroslavgamayunov.toodoo.util.getDrawableCompat
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainPageFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: TooDooViewModelFactory
    private val mainPageViewModel: MainPageViewModel by viewModels { viewModelFactory }

    private var binding: FragmentMainPageBinding? = null
    private lateinit var taskAdapter: TaskAdapter


    private var taskCollectingJob: Job? = null
    private var isShowingCompletedTasks = false
        set(value) {
            taskCollectingJob?.cancel()
            if (view != null) {
                taskCollectingJob = collectTasks(value)
            }
            field = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as TooDooApplication).viewModelComponent.inject(this)
        isShowingCompletedTasks =
            savedInstanceState?.getBoolean(SHOW_COMPLETED_TAG, false) ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SHOW_COMPLETED_TAG, isShowingCompletedTasks)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMainPageBinding.bind(view)
        setupTaskList()
        setupHeader()

        binding!!.apply {
            addTaskFab.setOnClickListener {
                findNavController().navigate(R.id.action_mainPageFragment_to_taskEditFragment)
            }
            showCompletedTasksImageView.setOnClickListener {
                if (isShowingCompletedTasks) {
                    (it as ImageView).setImageDrawable(
                        it.context.getDrawableCompat(
                            R.drawable.ic_visibility
                        )
                    )
                } else {
                    (it as ImageView).setImageDrawable(
                        it.context.getDrawableCompat(
                            R.drawable.ic_visibility_off
                        )
                    )
                }
                isShowingCompletedTasks = !isShowingCompletedTasks
            }
            mainPageAppbarLayout.setOnClickListener {
                mainPageScrollView.smoothScrollTo(0, 0)
                mainPageAppbarLayout.setExpanded(true)
            }
        }
    }


    private fun collectTasks(showingCompleted: Boolean): Job {
        return viewLifecycleOwner.lifecycleScope.launch {
            val result = mainPageViewModel.tasks(showingCompleted)
            if (result is Result.Success) {
                result.data.collect {
                    taskAdapter.submitList(it)
                }
            }
        }
    }

    private fun setupTaskList() {
        taskAdapter =
            TaskAdapter(
                onTaskCheck = {
                    lifecycleScope.launch {
                        mainPageViewModel.completeTask(it, it.isCompleted.not())
                    }
                },
                onTaskDelete = { task ->
                    lifecycleScope.launch {
                        mainPageViewModel.deleteTask(task)
                    }
                },
                onTaskEdit = {})

        binding?.apply {
            taskRecyclerView.adapter = taskAdapter

            ItemTouchHelper(TaskItemTouchHelperCallback()).apply {
                attachToRecyclerView(taskRecyclerView)
            }
        }

        taskCollectingJob = collectTasks(isShowingCompletedTasks)
    }

    private fun setupHeader() {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = mainPageViewModel.getNumberOfCompletedTasks()
            if (result is Result.Success) {
                result.data.collect {
                    binding!!.completedTaskCountTextView.text =
                        requireActivity().resources.getQuantityString(
                            R.plurals.completed_task_count,
                            it,
                            it
                        )
                }
            }
        }
    }


    companion object {
        private const val SHOW_COMPLETED_TAG = "SHOW_COMPLETED"
    }
}
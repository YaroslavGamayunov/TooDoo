package com.yaroslavgamayunov.toodoo.ui.mainpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainPageFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: TooDooViewModelFactory
    private val mainPageViewModel: MainPageViewModel by viewModels { viewModelFactory }

    private var binding: FragmentMainPageBinding? = null
    private lateinit var taskAdapter: TaskAdapter

    private var isShowingCompletedTasks = false


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
        binding?.addTaskFab?.setOnClickListener {
            findNavController().navigate(R.id.action_mainPageFragment_to_taskEditFragment)
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
                    viewLifecycleOwner.lifecycleScope.launch {
                        mainPageViewModel.deleteTask(task)
                    }
                },
                onTaskEdit = {})

        viewLifecycleOwner.lifecycleScope.launch {
            val result = mainPageViewModel.tasks(isShowingCompletedTasks)
            if (result is Result.Success) {
                result.data.collect {
                    taskAdapter.submitList(it)
                }
            }
        }

        binding?.apply {
            taskRecyclerView.adapter = taskAdapter

            ItemTouchHelper(TaskItemTouchHelperCallback()).apply {
                attachToRecyclerView(taskRecyclerView)
            }
        }
    }

    companion object {
        private const val SHOW_COMPLETED_TAG = "SHOW_COMPLETED"
    }
}
package com.yaroslavgamayunov.toodoo.ui.mainpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.databinding.FragmentMainPageBinding
import com.yaroslavgamayunov.toodoo.domain.common.doIfSuccess
import com.yaroslavgamayunov.toodoo.ui.base.BaseFragment
import com.yaroslavgamayunov.toodoo.ui.viewmodel.MainPageViewModel
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TooDooViewModelFactory
import com.yaroslavgamayunov.toodoo.util.getDrawableCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainPageFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: TooDooViewModelFactory
    private val mainPageViewModel: MainPageViewModel by viewModels { viewModelFactory }

    private var binding: FragmentMainPageBinding? = null
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as TooDooApplication).appComponent.inject(this)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        setupSnackbar()
        setupErrorHandling()

        binding!!.apply {
            addTaskFab.setOnClickListener {
                findNavController().navigate(R.id.action_mainPageFragment_to_taskEditFragment)
            }
            showCompletedTasksImageView.setOnClickListener {
                if (mainPageViewModel.isShowingCompletedTasks) {
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
                mainPageViewModel.isShowingCompletedTasks =
                    !mainPageViewModel.isShowingCompletedTasks
            }

            mainPageAppbarLayout.setOnClickListener {
                mainPageScrollView.smoothScrollTo(0, 0)
                mainPageAppbarLayout.setExpanded(true)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                mainPageViewModel.isRefreshing.collect {
                    withContext(Dispatchers.Main) {
                        mainPageSwipeRefreshLayout.isRefreshing = it
                    }
                }
            }

            mainPageSwipeRefreshLayout.setOnRefreshListener {
                mainPageViewModel.refreshTasks()
            }
        }
    }

    private fun setupTaskList() {
        taskAdapter =
            TaskAdapter(
                onTaskCheck = {
                    mainPageViewModel.completeTask(it, it.isCompleted.not())
                },
                onTaskDelete = { task ->
                    mainPageViewModel.deleteTask(task)
                },
                onTaskEdit = { task ->
                    val action =
                        MainPageFragmentDirections
                            .actionMainPageFragmentToTaskEditFragment(taskId = task.taskId)
                    findNavController().navigate(action)
                })

        binding?.apply {
            taskRecyclerView.adapter = taskAdapter

            ItemTouchHelper(TaskItemTouchHelperCallback()).apply {
                attachToRecyclerView(taskRecyclerView)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainPageViewModel.tasks.collect {
                taskAdapter.submitList(it)
            }
        }
    }

    private fun setupHeader() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainPageViewModel.completedTaskCount.collect { result ->
                result.doIfSuccess {
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

    private fun setupSnackbar() {
        val snackbar: Snackbar by lazy {
            Snackbar.make(
                binding!!.mainPageCoordinatorLayout,
                "",
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.undo) {
                mainPageViewModel.undoDeletedTasks()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainPageViewModel.deletionUndoCount.collect {
                if (it == 0) {
                    snackbar.dismiss()
                } else {
                    snackbar.setText(
                        requireActivity().resources.getQuantityString(
                            R.plurals.deleted_task_count,
                            it,
                            it
                        )
                    )
                    snackbar.show()
                }
            }
        }
    }

    private fun setupErrorHandling() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainPageViewModel.failures.collect {
                Toast.makeText(requireContext(), "failure=$it", Toast.LENGTH_LONG).show()
            }
        }
    }
}
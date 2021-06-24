package com.yaroslavgamayunov.toodoo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.databinding.FragmentMainPageBinding
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TaskViewModel
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TooDooViewModelFactory
import java.time.Instant
import javax.inject.Inject

class MainPageFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: TooDooViewModelFactory
    private val taskViewModel: TaskViewModel by viewModels { viewModelFactory }

    private var binding: FragmentMainPageBinding? = null
    private lateinit var taskAdapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as TooDooApplication).viewModelComponent.inject(this)
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
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainPageBinding.bind(view)

        setupToolbar()
        setupTaskList()
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        binding?.mainPageToolbar?.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupTaskList() {
        taskAdapter = TaskAdapter({}, {})
        binding?.run {
            taskRecyclerView.adapter = taskAdapter
            taskAdapter.submitList((1..150).map {
                Task(
                    it,
                    "Купить что-то где-то когда-нибудь в недалеком будущем Купить чтКупить что-то где-то когда-нибудь в недалеком будущемКупить что-то где-то когда-нибудь в недалеком будущемКупить что-то где-то когда-нибудь в недалеком будущемКупить что-то где-то когда-нибудь в недалеком будущемо-то где-то когда-нибудь в недалеком будущем",
                    true,
                    Instant.MAX,
                    false,
                    TaskPriority.High
                )
            })
        }

    }

}
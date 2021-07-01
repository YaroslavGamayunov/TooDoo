package com.yaroslavgamayunov.toodoo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.databinding.FragmentTaskEditBinding
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TaskEditViewModel
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TooDooViewModelFactory
import com.yaroslavgamayunov.toodoo.util.formatDate
import com.yaroslavgamayunov.toodoo.util.getColorFromAttrs
import com.yaroslavgamayunov.toodoo.util.getColoredText
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject


class TaskEditFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: TooDooViewModelFactory
    private val taskEditViewModel: TaskEditViewModel by viewModels { viewModelFactory }

    private var binding: FragmentTaskEditBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as TooDooApplication).viewModelComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentTaskEditBinding.bind(view)
        binding!!.apply {
            priorityPicker.setOnClickListener {
                showPriorityMenu()
            }
            dateTimePicker.setOnClickListener {
                showDatePicker()
            }
            taskTimeSwitch.setOnClickListener {
                if (taskTimeSwitch.isChecked) {
                    showDatePicker()
                } else {
                    taskEditViewModel.updateScheduleMode(TaskScheduleMode.Unspecified)
                }
            }

            taskDescriptionEditText.addTextChangedListener {
                taskEditViewModel.updateDescription(it.toString())
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            taskEditViewModel.task.collect {
                updateLayout(it)
            }
        }
        setupToolbar()
    }

    private fun updateLayout(task: Task) {
        binding?.apply {
            taskDescriptionEditText.apply {
                setText(task.description)
                setSelection(task.description.length)
            }

            taskDeadlineTextView.text =
                if (task.scheduleMode == TaskScheduleMode.Unspecified) ""
                else task.deadline.formatDate(
                    showTime = task.scheduleMode == TaskScheduleMode.ExactTime
                )

            val priorities = requireActivity().resources.getStringArray(R.array.task_priorities)
            priorityTextView.text = priorities[task.priority.level]

            taskTimeSwitch.isChecked = (task.scheduleMode == TaskScheduleMode.Unspecified).not()
        }
    }


    private fun showPriorityMenu() {
        (requireActivity() as MainActivity).showMenu(
            binding!!.priorityTextView,
            R.menu.menu_task_priority,
            onMenuInflated = { popupMenu ->
                popupMenu.menu.findItem(R.id.high).let {
                    it.title = it.title.getColoredText(
                        requireActivity().getColorFromAttrs(R.attr.tooDooRed)
                    )
                }
            },
            onItemClick = {
                when (it.itemId) {
                    R.id.none -> {
                        taskEditViewModel.updatePriority(TaskPriority.None)
                        true
                    }
                    R.id.low -> {
                        taskEditViewModel.updatePriority(TaskPriority.Low)
                        true
                    }
                    R.id.high -> {
                        taskEditViewModel.updatePriority(TaskPriority.High)
                        true
                    }
                    else -> false
                }
            }
        )
    }

    private fun showDatePicker() {
        val constraints =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()).build()

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(requireActivity().getString(R.string.datepicker_title))
                .setCalendarConstraints(constraints)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.addOnPositiveButtonClickListener { time ->
            taskEditViewModel.updateDeadline(Instant.ofEpochMilli(time))
            taskEditViewModel.updateScheduleMode(TaskScheduleMode.NotExactTime)

            showTimePicker(Instant.ofEpochMilli(time))
        }

        datePicker.show(childFragmentManager, null)
    }

    private fun showTimePicker(date: Instant) {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(requireActivity().getString(R.string.timepicker_title))
                .build()

        picker.addOnPositiveButtonClickListener {
            val selectedDateAndTime = date
                .plus(picker.hour.toLong(), ChronoUnit.HOURS)
                .plus(picker.minute.toLong(), ChronoUnit.MINUTES)

            taskEditViewModel.updateDeadline(selectedDateAndTime)
            taskEditViewModel.updateScheduleMode(TaskScheduleMode.ExactTime)
        }

        picker.show(childFragmentManager, null)
    }

    private fun setupToolbar() {
        binding?.apply {
            taskEditFragmentToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionSave -> runBlocking {
                        taskEditViewModel.saveChanges()
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
            taskEditFragmentToolbar.setNavigationOnClickListener {
                // TODO: Show confirmation dialog
                findNavController().navigateUp()
            }
        }
    }
}
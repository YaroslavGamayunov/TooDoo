package com.yaroslavgamayunov.toodoo.ui.mainpage

import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.domain.entities.Task
import com.yaroslavgamayunov.toodoo.domain.entities.TaskPriority
import com.yaroslavgamayunov.toodoo.domain.entities.TaskScheduleMode
import com.yaroslavgamayunov.toodoo.util.formatDate
import com.yaroslavgamayunov.toodoo.util.getColorFromAttrs
import com.yaroslavgamayunov.toodoo.util.getColorStateListCompat
import com.yaroslavgamayunov.toodoo.util.getDrawableCompat
import java.time.Instant

class TaskAdapter(
    private val onTaskCheck: (Task) -> Unit,
    private val onTaskEdit: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit
) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiff) {


    class TaskViewHolder(
        itemView: View,
        private val onCheck: (Int) -> Unit,
        private val onEdit: (Int) -> Unit,
        private val onDelete: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {

        private val checkBox: MaterialCheckBox =
            itemView.findViewById(R.id.completedTaskCheckBox)

        private val taskDescriptionTextView: TextView =
            itemView.findViewById(R.id.taskDescriptionTextView)

        private val taskDeadlineTextView: TextView =
            itemView.findViewById(R.id.taskDeadlineTextView)

        private val taskPriorityIcon: ImageView =
            itemView.findViewById(R.id.taskPriorityIcon)

        init {
            itemView.findViewById<View>(R.id.taskInfoImageView).setOnClickListener {
                onEdit(adapterPosition)
            }
            itemView.setOnClickListener {
                onCheck(adapterPosition)
            }
        }

        fun swipeLeft(): Unit = onDelete(adapterPosition)
        fun swipeRight(): Unit = onCheck(adapterPosition)

        private var isCompleted: Boolean = false
            set(value) {
                checkBox.isChecked = value
                taskDescriptionTextView.isSelected = value

                val description = taskDescriptionTextView.text.toString()
                taskDescriptionTextView.text = if (value) {
                    SpannableString(description).apply {
                        setSpan(
                            StrikethroughSpan(),
                            0,
                            description.lastIndex + 1,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                } else description
                field = value
            }

        fun bind(task: Task) {
            taskDescriptionTextView.text = task.description
            isCompleted = task.isCompleted

            if (task.scheduleMode == TaskScheduleMode.Unspecified) {
                taskDeadlineTextView.visibility = View.GONE
            } else {
                taskDeadlineTextView.visibility = View.VISIBLE
                taskDeadlineTextView.text = task.deadline.formatDate(
                    showTime = task.scheduleMode == TaskScheduleMode.ExactTime
                )
            }

            checkBox.buttonTintList = if (task.deadline.isBefore(Instant.now())) {
                itemView.context.getColorStateListCompat(R.color.checkbox_button_tint_outdated)
            } else {
                itemView.context.getColorStateListCompat(R.color.checkbox_button_tint_normal)
            }


            val taskDrawable = when (task.priority) {
                TaskPriority.None -> null
                TaskPriority.Low -> itemView.context.getDrawableCompat(
                    R.drawable.ic_low_priority,
                    color = itemView.context.getColorFromAttrs(R.attr.tooDooGray)
                )
                TaskPriority.High -> itemView.context.getDrawableCompat(
                    R.drawable.ic_high_priority,
                    color = itemView.context.getColorFromAttrs(R.attr.tooDooRed)
                )
            }

            taskPriorityIcon.setImageDrawable(taskDrawable)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView,
            onCheck = { position ->
                onTaskCheck(getItem(position))
            },
            onEdit = { position ->
                onTaskEdit(getItem(position))
            },
            onDelete = { position ->
                onTaskDelete(getItem(position))
            })
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object TaskDiff : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}
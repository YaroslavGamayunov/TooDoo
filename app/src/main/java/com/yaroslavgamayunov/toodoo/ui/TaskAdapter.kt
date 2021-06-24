package com.yaroslavgamayunov.toodoo.ui

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.domain.entities.Task

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskView: (Task) -> Unit
) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiff) {

    class TaskViewHolder(itemView: View, onClick: (Int) -> Unit, onView: (Int) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val checkBox: MaterialCheckBox =
            itemView.findViewById(R.id.completedTaskCheckBox)
        private val taskDescriptionTextView: TextView =
            itemView.findViewById(R.id.taskDescriptionTextView)

        init {
            itemView.findViewById<View>(R.id.taskInfoImageView).setOnClickListener {
                onView(adapterPosition)
            }
            itemView.setOnClickListener {
                isCompleted = !isCompleted
            }
        }

        private var isCompleted: Boolean = false
            set(value) {
                checkBox.isChecked = value
                taskDescriptionTextView.isSelected = value

                if (value != field) {
                    val description = taskDescriptionTextView.text.toString()
                    taskDescriptionTextView.text = if (value) {
                        SpannableString(description).apply {
                            setSpan(
                                StrikethroughSpan(),
                                0,
                                description.lastIndex,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE
                            )
                        }
                    } else description
                }
                field = value
            }

        fun bind(task: Task) {
            taskDescriptionTextView.text = task.description
            isCompleted = task.isCompleted
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView,
            onClick = { position ->
                onTaskClick(getItem(position))
            },
            onView = { position ->
                onTaskView(getItem(position))
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
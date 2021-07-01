package com.yaroslavgamayunov.toodoo.ui.mainpage

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.util.getColorFromAttrs
import com.yaroslavgamayunov.toodoo.util.getDimension
import com.yaroslavgamayunov.toodoo.util.getDrawableCompat
import kotlin.math.roundToInt


class TaskItemTouchHelperCallback :
    ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder is TaskAdapter.TaskViewHolder) {
            if (direction == ItemTouchHelper.RIGHT) {
                viewHolder.swipeRight()
            } else {
                viewHolder.swipeLeft()
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        drawBackground(itemView, c, dX)

        val icon: Drawable? = getIcon(itemView, dX)
        drawIcon(viewHolder.itemView, c, icon, dX)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    private fun drawBackground(itemView: View, canvas: Canvas, dX: Float) {
        if (dX > 0) {
            canvas.clipRect(
                0f, itemView.top.toFloat(),
                dX, itemView.bottom.toFloat()
            )
        } else {
            canvas.clipRect(
                itemView.right.toFloat() + dX, itemView.top.toFloat(),
                itemView.right.toFloat(), itemView.bottom.toFloat()
            )
        }
        val backgroundColorId = if (dX > 0) R.attr.tooDooGreen else R.attr.tooDooRed
        canvas.drawColor(itemView.context.getColorFromAttrs(backgroundColorId))
    }

    private fun getIcon(itemView: View, dX: Float): Drawable? {
        val drawableId = if (dX > 0) R.drawable.ic_check else R.drawable.ic_delete
        return itemView.context.getDrawableCompat(drawableId)
    }

    private fun drawIcon(itemView: View, canvas: Canvas, icon: Drawable?, dX: Float) {
        if (icon == null) return

        val iconColor = itemView.context.getColorFromAttrs(R.attr.tooDooWhite)

        icon.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            iconColor,
            BlendModeCompat.SRC_ATOP
        )

        val iconMargin: Int =
            itemView.context.getDimension(R.dimen.task_item_icon_margin).roundToInt()
        val horizontalMargin = (itemView.height - icon.intrinsicHeight) / 2

        val top = itemView.top + horizontalMargin
        val bottom = itemView.bottom - horizontalMargin

        icon.bounds = if (dX > 0) Rect(
            itemView.left + iconMargin,
            top,
            itemView.left + iconMargin + icon.intrinsicWidth,
            bottom
        ) else Rect(
            itemView.right - (icon.intrinsicWidth + iconMargin),
            top,
            itemView.right - iconMargin,
            bottom
        )

        icon.draw(canvas)
    }
}
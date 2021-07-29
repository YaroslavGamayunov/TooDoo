package com.yaroslavgamayunov.toodoo.ui.base

import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    fun showMenu(
        anchor: View,
        @MenuRes menuRes: Int,
        onMenuInflated: (PopupMenu) -> Unit,
        onItemClick: (MenuItem) -> Boolean,
        onDismiss: () -> Unit = {}
    ) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(menuRes, popup.menu)

        onMenuInflated(popup)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            onItemClick(menuItem)
        }

        popup.setOnDismissListener {
            onDismiss()
        }
        popup.show()
    }
}
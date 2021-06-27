package com.yaroslavgamayunov.toodoo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import com.yaroslavgamayunov.toodoo.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showMenu(
        anchor: View,
        @MenuRes menuRes: Int,
        onMenuInflated: (android.widget.PopupMenu) -> Unit,
        onItemClick: (MenuItem) -> Boolean,
        onDismiss: () -> Unit = {}
    ) {
        val popup = android.widget.PopupMenu(this, anchor)
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
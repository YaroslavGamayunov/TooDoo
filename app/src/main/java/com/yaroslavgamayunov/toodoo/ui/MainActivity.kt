package com.yaroslavgamayunov.toodoo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.TooDooApplication
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TaskViewModel
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TaskViewModel_Factory
import com.yaroslavgamayunov.toodoo.ui.viewmodel.TooDooViewModelFactory
import java.time.Instant
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
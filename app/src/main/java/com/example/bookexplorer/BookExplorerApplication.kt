package com.example.bookexplorer

import android.app.Application
import com.example.bookexplorer.data.AppContainer
import com.example.bookexplorer.data.DefaultAppContainer

class BookExplorerApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
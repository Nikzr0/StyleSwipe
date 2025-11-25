package com.example.styleswipe

import android.app.Application
import com.example.styleswipe.data.AppDatabase
import com.example.styleswipe.data.ClothingRepository

class StyleSwipeApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy { ClothingRepository(database.clothingDao()) }
}
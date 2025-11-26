package com.example.styleswipe.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val brand: String,
    val size: String,
    val price: String,

    val imagePaths: List<String>,

    val isLiked: Boolean = false
)
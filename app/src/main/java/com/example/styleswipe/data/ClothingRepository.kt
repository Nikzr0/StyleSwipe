package com.example.styleswipe.data

import kotlinx.coroutines.flow.Flow

class ClothingRepository(private val clothingDao: ClothingDao) {

    val allItems: Flow<List<ClothingItem>> = clothingDao.getAllItems()

    suspend fun insert(item: ClothingItem) {
        clothingDao.insertItem(item)
    }

    suspend fun delete(itemId: Int) {
        clothingDao.deleteItem(itemId)
    }
}
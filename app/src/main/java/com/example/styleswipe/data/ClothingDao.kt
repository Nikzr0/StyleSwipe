package com.example.styleswipe.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update // <--- ТОВА Е ВАЖНОТО!
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {
    @Query("SELECT * FROM clothing_items")
    fun getAllItems(): Flow<List<ClothingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClothingItem)

    @Update
    suspend fun updateItem(item: ClothingItem)

    @Query("DELETE FROM clothing_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int)
}
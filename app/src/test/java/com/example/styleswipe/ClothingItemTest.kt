package com.example.styleswipe

import com.example.styleswipe.data.ClothingItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ClothingItemTest {

    @Test
    fun createClothingItem_storesCorrectData() {
        // 1. Създаваме (Create) тестова дреха
        val item = ClothingItem(
            name = "Test Shirt",
            brand = "Nike",
            size = "L",
            price = "50",
            imagePaths = listOf("path/to/image.jpg")
        )

        // 2. Проверяваме (Assert) дали данните са запазени правилно
        assertEquals("Test Shirt", item.name)
        assertEquals("Nike", item.brand)
        assertEquals("L", item.size)
        assertEquals("50", item.price)
        assertEquals(1, item.imagePaths.size)
        assertEquals("path/to/image.jpg", item.imagePaths[0])
    }

    @Test
    fun createClothingItem_defaultLikedIsFalse() {
        // Проверяваме дали по подразбиране "isLiked" е false
        val item = ClothingItem(
            name = "Test",
            brand = "B",
            size = "S",
            price = "10",
            imagePaths = emptyList()
        )

        assertEquals(false, item.isLiked)
    }
}
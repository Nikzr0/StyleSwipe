package com.example.styleswipe

import com.example.styleswipe.data.Converters
import org.junit.Test
import org.junit.Assert.*

class ConvertersTest {

    private val converter = Converters()

    // Тест 1: Проверяваме дали превръщането от Списък в Текст работи
    @Test
    fun fromStringList_convertsCorrectly() {
        val list = listOf("image1.jpg", "image2.jpg")
        val result = converter.fromStringList(list)

        // Очакваме резултатът да е текст, разделен със запетаи
        assertEquals("image1.jpg,image2.jpg", result)
    }

    // Тест 2: Проверяваме дали превръщането от Текст в Списък работи
    @Test
    fun toStringList_convertsCorrectly() {
        val string = "imgA.png,imgB.png"
        val result = converter.toStringList(string)

        // Очакваме списък с 2 елемента
        assertEquals(2, result.size)
        assertEquals("imgA.png", result[0])
        assertEquals("imgB.png", result[1])
    }

    // Тест 3: Проверяваме какво става с празен списък
    @Test
    fun emptyList_handling() {
        val list = emptyList<String>()
        val result = converter.fromStringList(list)
        assertEquals("", result)
    }
}
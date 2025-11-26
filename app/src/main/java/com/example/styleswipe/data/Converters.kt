package com.example.styleswipe.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        // Превръща списъка в текст: "img1.jpg,img2.jpg,img3.jpg"
        return value.joinToString(separator = ",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        // Превръща текста обратно в списък
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.trim() }
    }
}
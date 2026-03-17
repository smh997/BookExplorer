package com.example.bookexplorer.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "saved_books")
data class SavedBookEntity(
    @PrimaryKey val bookId: Int,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val isFavorite: Boolean,
    val status: ReadingStatus,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ReadingStatus { NONE, TO_READ, READING, DONE }

class Converters {
    @TypeConverter
    fun toStatus(value: String): ReadingStatus = ReadingStatus.valueOf(value)
    @TypeConverter
    fun fromStatus(status: ReadingStatus): String = status.name
}

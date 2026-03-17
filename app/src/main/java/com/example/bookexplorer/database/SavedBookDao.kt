package com.example.bookexplorer.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedBookDao {

    @Upsert
    suspend fun upsert(book: SavedBookEntity)

    @Query("SELECT * FROM saved_books WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun observeFavorites(): Flow<List<SavedBookEntity>>

    @Query("SELECT * FROM saved_books WHERE status != 'NONE' ORDER BY updatedAt DESC")
    fun observeReadList(): Flow<List<SavedBookEntity>>

    @Query("SELECT * FROM saved_books WHERE bookId = :id LIMIT 1")
    fun observeById(id: Int): Flow<SavedBookEntity?>

    @Query("SELECT * FROM saved_books")
    fun observeAll(): Flow<List<SavedBookEntity>>

    @Query("DELETE FROM saved_books WHERE isFavorite = 0 AND status = 'NONE'")
    suspend fun deleteUnmarked()

    @Query("SELECT * FROM saved_books WHERE bookId = :id LIMIT 1")
    suspend fun getById(id: Int): SavedBookEntity?

    @Query("DELETE FROM saved_books WHERE bookId = :id")
    suspend fun deleteById(id: Int): Int

}

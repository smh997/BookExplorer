package com.example.bookexplorer.data

import android.content.Context
import androidx.room.Room
import com.example.bookexplorer.database.AppDatabase
import com.example.bookexplorer.database.SavedBookDao
import com.example.bookexplorer.network.BookApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val bookRepository: BooksRepository
    val libraryRepository: LibraryRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl = "https://gutendex.com/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    private val retrofitService by lazy {
        retrofit.create(BookApiService::class.java)
    }
    override val bookRepository: BooksRepository by lazy {
        NetworkBookRepository(retrofitService)
    }


    // --- Room ---
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "books.db"
        ).build()
    }

    private val savedBookDao: SavedBookDao by lazy {
        database.savedBookDao()
    }

    override val libraryRepository: LibraryRepository by lazy {
        OfflineLibraryRepository(savedBookDao)
    }

}
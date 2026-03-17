package com.example.bookexplorer.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface BookApiService {
    @GET("books")
    suspend fun getBooks(
        @Query("search") query: String
    ): BooksResponseDto

    @GET
    suspend fun getBooksByUrl(@Url url: String): BooksResponseDto
}
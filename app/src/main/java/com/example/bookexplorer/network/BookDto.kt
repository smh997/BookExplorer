package com.example.bookexplorer.network

data class BooksResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<BookDto>
)

data class BookDto(
    val id: Int,
    val title: String,
    val authors: List<AuthorDto>?,
    val formats: Map<String, String>?
)

data class AuthorDto(
    val name: String
)







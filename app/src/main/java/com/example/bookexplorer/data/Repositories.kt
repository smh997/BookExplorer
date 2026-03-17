package com.example.bookexplorer.data

import com.example.bookexplorer.database.ReadingStatus
import com.example.bookexplorer.database.SavedBookDao
import com.example.bookexplorer.database.SavedBookEntity
import com.example.bookexplorer.network.BookApiService
import com.example.bookexplorer.network.BookDto
import com.example.bookexplorer.network.BooksResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface BooksRepository {
    suspend fun search(
        query: String
    ): BooksResponseDto

    suspend fun loadNext(
        nextUrl: String
    ): BooksResponseDto

    suspend fun loadPrev(
        prevUrl: String
    ): BooksResponseDto
}

class NetworkBookRepository(private val bookApiService: BookApiService) : BooksRepository {

    override suspend fun search(query: String): BooksResponseDto = bookApiService.getBooks(query)

    override suspend fun loadNext(nextUrl: String): BooksResponseDto =
        bookApiService.getBooksByUrl(nextUrl)

    override suspend fun loadPrev(prevUrl: String): BooksResponseDto =
        bookApiService.getBooksByUrl(prevUrl)
}


interface LibraryRepository {
    fun observeFavorites(): Flow<List<SavedBookEntity>>
    fun observeReadList(): Flow<List<SavedBookEntity>>
    fun observeSavedMap(): Flow<Map<Int, SavedBookEntity>>
    fun observeSavedBook(id: Int): Flow<SavedBookEntity?>
    suspend fun toggleFavorite(book: BookDto)
    suspend fun setStatus(book: BookDto, status: ReadingStatus)
}

class OfflineLibraryRepository(
    private val dao: SavedBookDao
) : LibraryRepository {

    override fun observeFavorites() = dao.observeFavorites()

    override fun observeReadList() = dao.observeReadList()

    override fun observeSavedMap() =
        dao.observeAll().map { list -> list.associateBy { it.bookId } }

    override fun observeSavedBook(id: Int) = dao.observeById(id)

    override suspend fun toggleFavorite(book: BookDto) {
        val author = book.authors?.firstOrNull()?.name ?: "Unknown author"
        val coverUrl = book.formats?.get("image/jpeg")

        // get existing entry if any
        val existing = dao.getById(book.id)

        val newIsFavorite = !(existing?.isFavorite ?: false)
        val newStatus = existing?.status ?: ReadingStatus.NONE

        if (!newIsFavorite && newStatus == ReadingStatus.NONE) {
            // optional cleanup: nothing saved anymore
            dao.deleteById(book.id)
        } else {
            dao.upsert(
                SavedBookEntity(
                    bookId = book.id,
                    title = book.title,
                    author = author,
                    coverUrl = coverUrl,
                    isFavorite = newIsFavorite,
                    status = newStatus,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun setStatus(book: BookDto, status: ReadingStatus) {
        val author = book.authors?.firstOrNull()?.name ?: "Unknown author"
        val coverUrl = book.formats?.get("image/jpeg")

        val existing = dao.getById(book.id)
        val isFavorite = existing?.isFavorite ?: false

        if (!isFavorite && status == ReadingStatus.NONE) {
            dao.deleteById(book.id)
        } else {
            dao.upsert(
                SavedBookEntity(
                    bookId = book.id,
                    title = book.title,
                    author = author,
                    coverUrl = coverUrl,
                    isFavorite = isFavorite,
                    status = status,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}

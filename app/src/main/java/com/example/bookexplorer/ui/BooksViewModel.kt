package com.example.bookexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookexplorer.BookExplorerApplication
import com.example.bookexplorer.data.BooksRepository
import com.example.bookexplorer.network.BooksResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface BooksUiState {
    data class Success(
        val booksResponseDto: BooksResponseDto,
        val isLoadingMore: Boolean = false
    ) : BooksUiState

    data object Error : BooksUiState
    data object Loading : BooksUiState
}

class BooksViewModel(private val bookRepository: BooksRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)

    val uiState: StateFlow<BooksUiState> = _uiState

    init {
        search("Love")
    }

    fun search(query: String = "book") {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            _uiState.value = try {
                BooksUiState.Success(bookRepository.search(query = query))
            } catch (e: IOException) {
                BooksUiState.Error
            } catch (e: HttpException) {
                BooksUiState.Error
            }
        }
    }

    fun loadMore() {
        val current = _uiState.value
        if (current !is BooksUiState.Success) return
        if (current.isLoadingMore) return
        val nextUrl = current.booksResponseDto.next ?: return

        viewModelScope.launch {
            // Optional: expose "loading more" state by re-emitting Success with a flag
            _uiState.value = BooksUiState.Success(
                booksResponseDto = current.booksResponseDto,
                isLoadingMore = true
            )

            _uiState.value = try {
                val nextPage = bookRepository.loadNext(nextUrl)

                val merged = current.booksResponseDto.copy(
                    // append new results to old results
                    results = current.booksResponseDto.results + nextPage.results,
                    // advance pagination pointers
                    next = nextPage.next,
                    previous = nextPage.previous,
                    // keep or update count (either is fine; this uses server value)
                    count = nextPage.count
                )

                BooksUiState.Success(
                    booksResponseDto = merged,
                    isLoadingMore = false
                )
            } catch (e: IOException) {
                BooksUiState.Error
            } catch (e: HttpException) {
                BooksUiState.Error
            }
        }
    }

    fun findBookById(id: Int): com.example.bookexplorer.network.BookDto? {
        val current = _uiState.value
        if (current !is BooksUiState.Success) return null
        return current.booksResponseDto.results.find { it.id == id }

    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookExplorerApplication)
                BooksViewModel(application.container.bookRepository)
            }
        }
    }
}
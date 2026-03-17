package com.example.bookexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookexplorer.BookExplorerApplication
import com.example.bookexplorer.data.LibraryRepository
import com.example.bookexplorer.database.ReadingStatus
import com.example.bookexplorer.database.SavedBookEntity
import com.example.bookexplorer.network.BookDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    val favorites: StateFlow<List<SavedBookEntity>> =
        libraryRepository.observeFavorites()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    val readList: StateFlow<List<SavedBookEntity>> =
        libraryRepository.observeReadList()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    val savedMap: StateFlow<Map<Int, SavedBookEntity>> =
        libraryRepository.observeSavedMap()
            .stateIn(viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyMap()
            )

    fun observeSavedBook(id: Int): Flow<SavedBookEntity?> = libraryRepository.observeSavedBook(id)


    fun toggleFavorite(book: BookDto) {
        viewModelScope.launch { libraryRepository.toggleFavorite(book) }
    }

    fun setStatus(book: BookDto, status: ReadingStatus) {
        viewModelScope.launch { libraryRepository.setStatus(book, status) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as BookExplorerApplication)
                LibraryViewModel(app.container.libraryRepository)
            }
        }
    }
}

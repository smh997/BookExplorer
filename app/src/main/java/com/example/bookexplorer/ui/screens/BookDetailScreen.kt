package com.example.bookexplorer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.bookexplorer.database.ReadingStatus
import com.example.bookexplorer.database.SavedBookEntity
import com.example.bookexplorer.network.AuthorDto
import com.example.bookexplorer.network.BookDto
import com.example.bookexplorer.ui.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Int,
    book: BookDto?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val libraryVm: LibraryViewModel = viewModel(factory = LibraryViewModel.Factory)
    val saved by libraryVm.observeSavedBook(bookId).collectAsState(initial = null)

    val title = book?.title ?: saved?.title ?: "Book"
    val author = book?.authors?.firstOrNull()?.name ?: saved?.author ?: "Unknown author"
    val coverUrl = book?.formats?.get("image/jpeg") ?: saved?.coverUrl
    val isFav = saved?.isFavorite ?: false
    val status = saved?.status ?: ReadingStatus.NONE

    val writableDto: BookDto? = book ?: saved?.toBookDto()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        BookDetailContent(
            title = title,
            author = author,
            coverUrl = coverUrl,
            isFavorite = isFav,
            status = status,
            onToggleFavorite = {
                writableDto?.let { libraryVm.toggleFavorite(it) }
            },
            onSetStatus = { newStatus ->
                writableDto?.let { libraryVm.setStatus(it, newStatus) }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}


@Composable
fun BookDetailContent(
    title: String,
    author: String,
    coverUrl: String?,
    isFavorite: Boolean,
    status: ReadingStatus,
    onToggleFavorite: () -> Unit,
    onSetStatus: (ReadingStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl)
                .build(),
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(author, style = MaterialTheme.typography.bodyMedium)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle favorite"
                )
            }

            StatusChips(
                current = status,
                onSelect = onSetStatus
            )
        }
    }
}

@Composable
fun StatusChips(
    current: ReadingStatus,
    onSelect: (ReadingStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        ReadingStatus.NONE to "None",
        ReadingStatus.TO_READ to "To Read",
        ReadingStatus.READING to "Reading",
        ReadingStatus.DONE to "Done"
    )

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (status, label) ->
            FilterChip(
                selected = current == status,
                onClick = { onSelect(status) },
                label = { Text(label) }
            )
        }
    }
}

fun SavedBookEntity.toBookDto(): BookDto {
    return BookDto(
        id = bookId,
        title = title,
        authors = listOf(AuthorDto(author)),
        formats = if (coverUrl != null) mapOf("image/jpeg" to coverUrl) else emptyMap()
    )
}



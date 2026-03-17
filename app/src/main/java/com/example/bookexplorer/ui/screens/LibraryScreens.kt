package com.example.bookexplorer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.bookexplorer.database.SavedBookEntity
import com.example.bookexplorer.ui.LibraryViewModel

@Composable
fun FavoritesScreen(
    onClickBook: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: LibraryViewModel = viewModel(factory = LibraryViewModel.Factory)
    val favorites by vm.favorites.collectAsState()

    SavedBooksList(
        title = "Favorites",
        items = favorites,
        onClickBook = onClickBook,
        modifier = modifier
    )
}

@Composable
fun ReadListScreen(
    onClickBook: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: LibraryViewModel = viewModel(factory = LibraryViewModel.Factory)
    val readList by vm.readList.collectAsState()

    SavedBooksList(
        title = "Readlist",
        items = readList,
        onClickBook = onClickBook,
        modifier = modifier
    )
}

@Composable
private fun SavedBooksList(
    title: String,
    items: List<SavedBookEntity>,
    onClickBook: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        if (items.isEmpty()) {
            Text(
                text = "Nothing here yet",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.bookId }) { b ->
                SavedBookRow(
                    book = b,
                    onClick = { onClickBook(b.bookId) }
                )
            }
        }
    }
}

@Composable
private fun SavedBookRow(
    book: SavedBookEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .build(),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth(0.25f)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Text(book.author, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                Text(
                    text = buildString {
                        if (book.isFavorite) append("★ Favorite  ")
                        if (book.status.name != "NONE") append("• ${book.status.name}")
                    }.ifBlank { " " },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

package com.example.bookexplorer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.bookexplorer.R
import com.example.bookexplorer.network.BookDto
import com.example.bookexplorer.ui.BooksUiState

@Composable
fun BooksScreen(
    booksUiState: BooksUiState,
    onLoadMore: () -> Unit,
    onClickBook: (Int) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("Love") }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                val q = query.trim()
                if (q.isNotEmpty()) onSearch(q)
            }
        )

        when (booksUiState) {
            is BooksUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
            is BooksUiState.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
            is BooksUiState.Success -> {
                val dto = booksUiState.booksResponseDto
                BooksGridScreen(
                    books = dto.results,
                    canLoadMore = dto.next != null,
                    isLoadingMore = booksUiState.isLoadingMore,
                    onLoadMore = onLoadMore,
                    onClickBook = onClickBook,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            label = { Text("Search") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
        )
        Button(onClick = onSearch) {
            Text("Search")
        }
    }
}


@Composable
fun BooksGridScreen(
    books: List<BookDto>,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onClickBook: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = books, key = { it.id }) { book ->
            BookCard(
                book = book,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onClickBook(book.id)
                }
            )
        }

        item(span = { GridItemSpan(2) }) {
            PaginationFooter(
                canLoadMore = canLoadMore,
                isLoadingMore = isLoadingMore,
                onLoadMore = onLoadMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
    }
}


@Composable
private fun PaginationFooter(
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoadingMore -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading more…",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            canLoadMore -> {
                Button(onClick = onLoadMore) {
                    Text("Load more")
                }
            }

            else -> {
                Text(
                    text = "End reached",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun BookCard(
    book: BookDto,
    onClick: (() -> Unit),
    modifier: Modifier = Modifier
) {
    val coverUrl = book.formats?.get("image/jpeg")
    val authorText = book.authors?.firstOrNull()?.name ?: "Unknown author"

    Card(
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        onClick = { onClick() },
        modifier = modifier
            .aspectRatio(0.75f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cover
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverUrl)
                    .build(),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Title + author
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )
                Text(
                    text = authorText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_connection_error),
            contentDescription = stringResource(R.string.connection_error)
        )
        Text(
            text = stringResource(R.string.failed_to_load),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

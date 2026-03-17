# BookExplorer

An Android app for discovering books and managing a personal reading list.

## Features

- 🔍 Search and explore books using a public API (Gutendex)
- 📚 View book details including title, author, and cover
- ❤️ Add or remove books from favorites
- 📖 Track reading status (To Read, Reading, Done)
- 💾 Persistent storage using Room database
- 🔄 Pagination with "Load More"

## Tech Stack

- Kotlin
- Jetpack Compose
- MVVM Architecture
- StateFlow (UDF)
- Retrofit + Gson
- Room Database
- Coil (image loading)
- Navigation Compose

## Architecture

The app follows a layered architecture:

- **UI Layer**: Compose screens + ViewModels  
- **Data Layer**: Repositories + Network (API) + Local Database (Room)

- `BooksRepository` handles remote data (API)
- `LibraryRepository` manages local persistence (favorites & reading list)

## Screens

- Discover (search & browse books)
- Details (book information)
- Favorites (saved books)
- Readlist (reading status tracking)

## Screenshots

<p align="center">
  <img src="image/dark-main.png" alt="BookExplorer Screenshot 1" width="300"/>
  <img src="image/dark-favorite.png" alt="BookExplorer Screenshot 2" width="300"/>
  <img src="image/dark-readlist.png" alt="BookExplorer Screenshot 3" width="300"/>
  <img src="image/light-main.png" alt="BookExplorer Screenshot 4" width="300"/>
  <img src="image/light-favorite.png" alt="BookExplorer Screenshot 5" width="300"/>
  <img src="image/light-readlist.png" alt="BookExplorer Screenshot 6" width="300"/>
</p>

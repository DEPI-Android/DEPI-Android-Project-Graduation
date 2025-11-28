# 🏗️ Egypt Tour App - Complete Architecture Guide

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     ANDROID FRAMEWORK                       │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           Application Layer                         │  │
│  │  ┌────────────────────────────────────────────────┐ │  │
│  │  │  @HiltAndroidApp - EgyptTourApp               │ │  │
│  │  │  └─ Initializes Hilt DI Container            │ │  │
│  │  └────────────────────────────────────────────────┘ │  │
│  │                         ↓                            │  │
│  │  ┌────────────────────────────────────────────────┐ │  │
│  │  │  @AndroidEntryPoint - MainActivity             │ │  │
│  │  │  └─ Creates NavController & AppNavigation     │ │  │
│  │  └────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│              NAVIGATION & UI LAYER                          │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  AppNavigation                                      │  │
│  │  ├─ startDestination: governorates                │  │
│  │  ├─ routes:                                         │  │
│  │  │  ├─ governorates → GovernorateListScreen      │  │
│  │  │  ├─ landmarks/{id} → LandmarkListScreen       │  │
│  │  │  └─ landmark/{id} → LandmarkDetailScreen(TODO)│  │
│  │  └─ NavController (manages navigation)            │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  UI Screens                                         │  │
│  │  ├─ GovernorateListScreen                          │  │
│  │  │  ├─ TopAppBar (Gold)                            │  │
│  │  │  ├─ LazyColumn                                  │  │
│  │  │  └─ GovernorateCard × 5                         │  │
│  │  ├─ LandmarkListScreen                             │  │
│  │  │  ├─ TopAppBar + Back Button                     │  │
│  │  │  ├─ SearchBar                                   │  │
│  │  │  └─ LazyColumn (Landmarks)                      │  │
│  │  └─ LandmarkDetailScreen                           │  │
│  │     └─ (Implementation pending)                    │  │
│  └──────────────────────────────────────────────────────┘  │
│                         ↓                                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Theme Layer                                        │  │
│  │  ├─ Colors (Gold, Gray, White)                     │  │
│  │  ├─ Typography                                      │  │
│  │  └─ Styling                                        │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│            VIEWMODEL & STATE MANAGEMENT                     │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  @HiltViewModel - LandmarkListViewModel             │  │
│  │  ├─ landmarksState: StateFlow<Result<List>>         │  │
│  │  ├─ searchQuery: StateFlow<String>                  │  │
│  │  ├─ filteredLandmarks: StateFlow<List>              │  │
│  │  ├─ scrollPosition: StateFlow<Int>                  │  │
│  │  └─ @Inject repository & savedStateHandle          │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│         DEPENDENCY INJECTION (Hilt Module)                  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  DataModule (@Module @InstallIn)                    │  │
│  │  └─ @Provides @Singleton LandmarkRepository         │  │
│  └──────────────────────────────────────────────────────┘  │
│                         ↓                                    │
│  Dependencies provided:                                     │
│  ├─ LandmarkRepository (Singleton)                         │
│  ├─ SavedStateHandle (Auto)                                │
│  └─ Other Android components (Auto)                        │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│           DATA LAYER (Repository Pattern)                   │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  LandmarkRepository                                 │  │
│  │  ├─ @Inject constructor(WikiApiService)             │  │
│  │  ├─ getLandmarks(governorate)                        │  │
│  │  │  ├─ Calls WikiApiService                         │  │
│  │  │  ├─ Transforms response to LandMark objects      │  │
│  │  │  └─ Returns Result<List<LandMark>>               │  │
│  │  └─ Error handling & retry logic                    │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│           API LAYER (Retrofit + Coil)                       │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  WikiApiService (Retrofit Interface)                │  │
│  │  └─ getCategoryMembers(category)                    │  │
│  │     └─ Returns WikiResponse                         │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  RetrofitInstance (Singleton)                       │  │
│  │  ├─ OkHttp Client with interceptors                 │  │
│  │  └─ Retrofit setup for Wikipedia API                │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Coil Image Loading                                 │  │
│  │  ├─ AsyncImage composable                           │  │
│  │  └─ Smart caching & loading                         │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────┐
│              MODELS & DATA STRUCTURES                       │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Data Models                                        │  │
│  │  ├─ Governorate (enum with 5 values)               │  │
│  │  ├─ LandMark (data class)                           │  │
│  │  ├─ Result<T> (sealed class for state)              │  │
│  │  └─ Wiki* DTOs (API response models)                │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## Data Flow Example: Loading Landmarks

```
User taps "Cairo" card
        ↓
GovernorateListScreen.onGovernorateClick("cairo")
        ↓
navController.navigate("landmarks/cairo")
        ↓
LandmarkListScreen receives governorateId = "cairo"
        ↓
LaunchedEffect triggers
        ↓
viewModel.loadLandmarks(Governorate.CAIRO)
        ↓
LandmarkListViewModel._landmarksState.value = Loading
        ↓
viewModelScope.launch (coroutine)
        ↓
repository.getLandmarks(Governorate.CAIRO)
        ↓
LandmarkRepository.withContext(Dispatchers.IO)
        ↓
WikiApiService.getCategoryMembers("Category:Tourist_attractions_in_Cairo")
        ↓
HTTP GET to Wikipedia API
        ↓
Response: WikiResponse with pages
        ↓
Transform to LandMark objects
        ↓
Return: Result.Success<List<LandMark>>
        ↓
Update: _landmarksState.value = Result.Success(landmarks)
        ↓
UI Recomposes with new data
        ↓
LazyColumn displays landmarks
```

---

## Hilt Dependency Injection Flow

```
App Startup
    ↓
AndroidManifest.xml references <application android:name=".EgyptTourApp">
    ↓
EgyptTourApp.onCreate() is called
    ↓
@HiltAndroidApp annotation triggers
    ↓
Hilt generates and initializes DI Container
    ↓
Hilt scans for @Module classes
    ↓
Finds: DataModule
    ↓
DataModule provides:
    └─ @Provides fun provideLandmarkRepository(): LandmarkRepository
    ├─ Creates RetrofitInstance.api
    ├─ Wraps in LandmarkRepository
    └─ Marks as @Singleton
    ↓
MainActivity.onCreate() is called
    ↓
@AndroidEntryPoint annotation enables Hilt
    ↓
Hilt resolves activity dependencies
    ↓
LandmarkListScreen calls viewModel()
    ↓
Hilt sees @HiltViewModel annotation
    ↓
Hilt resolves constructor parameters:
    ├─ repository: LandmarkRepository
    │  └─ Found in DataModule ✅
    └─ savedStateHandle: SavedStateHandle
       └─ Provided by Hilt automatically ✅
    ↓
ViewModel instance created successfully ✅
```

---

## State Management Architecture

```
┌────────────────────────────────────┐
│  LandmarkListViewModel             │
├────────────────────────────────────┤
│  StateFlows (Read-only from UI):   │
│  ├─ landmarksState                 │
│  ├─ searchQuery                    │
│  ├─ filteredLandmarks              │
│  └─ scrollPosition                 │
├────────────────────────────────────┤
│  MutableStateFlows (Internal):     │
│  ├─ _landmarksState                │
│  ├─ _searchQuery                   │
│  ├─ _filteredLandmarks             │
│  └─ _scrollPosition                │
├────────────────────────────────────┤
│  Public API (Methods):             │
│  ├─ loadLandmarks(gov)             │
│  ├─ updateSearchQuery(query)       │
│  ├─ clearSearch()                  │
│  ├─ retry()                        │
│  └─ saveScrollPosition(index)      │
├────────────────────────────────────┤
│  SavedStateHandle Integration:     │
│  └─ Survives process death         │
└────────────────────────────────────┘
```

---

## Navigation Graph

```
digraph {
    rankdir=TB
    
    START → GOVERNORATES
    GOVERNORATES → LANDMARKS [label="tap card\npass governorateId"]
    LANDMARKS → GOVERNORATES [label="back button"]
    LANDMARKS → DETAIL [label="tap landmark\npass landmarkId"]
    DETAIL → LANDMARKS [label="back"]
    DETAIL → GOVERNORATES [label="up navigation"]
}

Visual:
    ┌─────────────────┐
    │  GOVERNORATES   │◄─────────────┐
    │  (Home)         │              │
    └────────┬────────┘              │
             │ tap card             │ back button
             │ navigate             │
             ▼                      │
    ┌─────────────────┐             │
    │  LANDMARKS      ├─────────────┘
    │  (List)         │
    └────────┬────────┘
             │ tap landmark
             │ navigate
             ▼
    ┌─────────────────┐
    │  DETAIL         │
    │  (TODO)         │
    └─────────────────┘
```

---

## Thread Management

```
Main Thread (UI):
├─ Composable functions
├─ UI recomposition
└─ User interactions

ViewModelScope (IO Dispatcher):
├─ repository.getLandmarks()
│  ├─ withContext(Dispatchers.IO)
│  ├─ API call (blocking)
│  └─ Data transformation
└─ State updates (back to Main)

Coroutine Lifecycle:
├─ Survives configuration changes
└─ Cancels when ViewModel is cleared
```

---

## Error Handling

```
Result<T> Sealed Class:
├─ Success(data: T)
├─ Error(exception: Exception)
└─ Loading

UI Layer:
├─ is Result.Loading → Show LoadingScreen
├─ is Result.Success → Show data or EmptyScreen
└─ is Result.Error → Show ErrorScreen + retry
```

---

## Summary

This architecture provides:
- ✅ Clean separation of concerns
- ✅ Dependency injection (Hilt)
- ✅ State management (StateFlow)
- ✅ Reactive UI (Compose)
- ✅ Proper error handling
- ✅ Network isolation (Repository)
- ✅ Process death recovery (SavedStateHandle)
- ✅ Coroutine management
- ✅ Type safety
- ✅ Testability

It follows MVVM pattern with clean architecture principles.


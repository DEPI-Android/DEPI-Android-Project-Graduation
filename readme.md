# Egypt Explorer: An Android Guide to the Wonders of Egypt

**Egypt Tour** is a modern, content-rich Android application designed to guide users through the historical and cultural landmarks of Egypt. Built with the latest Android technologies, it provides a robust, efficient, and visually appealing user experience.

The app features a complete **Firebase Authentication** system (Sign Up, Sign In, Forgot Password) and intelligently fetches data from both a local, curated database and the public Wikipedia API. This ensures users receive high-quality, relevant, and up-to-date information in a secure, personalized environment.

## ✨ Features

*   **Full User Authentication:**
    *   **Secure Sign-Up & Sign-In:** Complete authentication flow using Firebase Auth with email and password.
    *   **User Profile Management:** Firestore is used to store and retrieve user profiles (username, email). The app includes a dedicated profile screen with options to "Logout" or "Logout from All Devices".
    *   **Session Persistence:** "Remember Me" functionality and local caching of user profiles for a seamless experience across app sessions.

*   **Favorites & Saved Places:**
    *   **Heart to Favorite:** Tap the ❤️ icon on any landmark detail screen to add it to your favorites.
    *   **Bookmark to Save:** Tap the 🔖 icon to save places for later.
    *   **Cloud Sync:** Favorites and saves are stored in Firebase Firestore and sync across devices.
    *   **Local Caching:** Instant access with cache-first approach – data loads from local storage first, then syncs with cloud in background.
    *   **Dedicated Lists:** View all your favorited and saved landmarks from the Profile screen.

*   **Dark Mode Support:**
    *   **Theme Toggle:** Switch between light and dark themes from the Profile settings.
    *   **Persistent Preference:** Theme choice is saved locally and persists across app sessions.
    *   **Full Theme Support:** All screens adapt to the selected theme with proper colors.

*   **Dynamic Content & Smart Data Sourcing:**
    *   **Explore by Governorate:** Fetches unique landmark lists for 10 different Egyptian governorates.
    *   **Local-First Approach:** Instantly loads curated landmarks from a local JSON database for a fast, offline-first experience.
    *   **Intelligent Wikipedia Fallback:** If local data is unavailable, it queries the Wikipedia API using a sophisticated multi-strategy approach (known landmarks, categories, and geo-search).

*   **Advanced Data Curation:**
    *   Employs a rigorous filtering and scoring system to ensure only the most relevant and high-quality landmarks are displayed.
    *   It filters out irrelevant articles (e.g., "List of...") and sorts results by a calculated "relevance score" based on data quality and importance.

*   **Robust & Interactive UI:**
    *   **Modern Declarative UI:** Built entirely with Jetpack Compose.
    *   **Real-time Search:** Instantly filters the list of landmarks by name or description.
    *   **Graceful State Handling:** Displays user-friendly loading indicators and error messages.
    *   **Lifecycle-Aware & Resilient:** Handles screen rotations and other configuration changes without losing data.
    *   **Process Death Recovery:** Thanks to `SavedStateHandle`, the app automatically restores the user's last-viewed governorate and scroll position if the OS closes the app in the background, providing a truly seamless journey.

## 🛠️ Technical Stack & Architecture

This project is a showcase of modern Android development best practices.

*   **Architecture:** Follows the official **MVVM (Model-View-ViewModel)** and **Clean Architecture** principles to create a decoupled, scalable, and testable codebase.
    *   **UI Layer:** Jetpack Compose, ViewModels, Hilt for ViewModel injection.
    *   **Data Layer:** Repositories, API Services, Local Data Sources.
*   **Core Technologies:**
    *   **[Kotlin](https://kotlinlang.org/):** The official language for Android development.
    *   **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html):** For managing all asynchronous operations, from API calls to database queries.
    *   **[Jetpack Compose](https://developer.android.com/jetpack/compose):** For building the entire UI declaratively.
    *   **[Hilt](https://dagger.dev/hilt/):** For robust Dependency Injection across the application.
*   **Firebase Integration:**
    *   **[Firebase Authentication](https://firebase.google.com/docs/auth):** For managing user sign-up and sign-in.
    *   **[Cloud Firestore](https://firebase.google.com/docs/firestore):** As a cloud database for storing user profile information, favorites, and saved places.
*   **Local Storage:**
    *   **SharedPreferences:** For caching user preferences (dark mode), session data, and favorites/saves for instant access.
*   **Libraries:**
    *   **[Retrofit](https://square.github.io/retrofit/):** For type-safe networking and communication with the Wikipedia API.
    *   **[OkHttp](https://square.github.io/okhttp/):** The underlying HTTP client, configured with interceptors for logging and adding required headers.
    *   **[Gson](https://github.com/google/gson):** For parsing JSON data into Kotlin data classes.
    *   **[Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation):** For navigating between screens.
    *   **[StateFlow & MutableStateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow):** For managing and observing UI state reactively.
    *   **[Lifecycle Components](https://developer.android.com/jetpack/lifecycle):** `ViewModel`, `viewModelScope`, and `SavedStateHandle` for creating lifecycle-aware and highly resilient UI components.

## 🖼️ App Screenshots
<img width="270" alt="Screenshot 2025-12-05 063852" src="https://github.com/user-attachments/assets/3bec9d3e-ac7b-4dd5-bc03-e60cc1048433" />
<img width="270" alt="Screenshot 2025-12-05 063924" src="https://github.com/user-attachments/assets/3be60234-31d3-4359-b9f0-6a3b8246a961" />
<img width="270" alt="Screenshot 2025-12-05 063935" src="https://github.com/user-attachments/assets/edcc17aa-cb1a-46c9-887b-8afcfcda7f0e" />
<img width="270" alt="Screenshot 2025-12-05 063208" src="https://github.com/user-attachments/assets/52828bac-0cc1-493a-92c7-08e4101f9695" />
<img width="270" alt="Screenshot 2025-12-05 063401" src="https://github.com/user-attachments/assets/62a5b17a-c2dd-4b21-89e3-c34d5cc85069" />
<img width="270" alt="Screenshot 2025-12-05 063421" src="https://github.com/user-attachments/assets/cca71148-f001-4875-a756-bb32dd22b9c1" />
<img width="270" alt="Screenshot 2025-12-05 063455" src="https://github.com/user-attachments/assets/3bf6796b-4365-48ad-afcb-eed1660f83d1" />
<img width="270" alt="Screenshot 2025-12-05 063541" src="https://github.com/user-attachments/assets/44e9ae12-a979-45d1-a352-131c7593fbc1" />
<img width="270" alt="Screenshot 2025-12-05 063625" src="https://github.com/user-attachments/assets/fb76e866-d010-45e3-8336-30261808f531" />
<img width="270" alt="Screenshot 2025-12-05 063654" src="https://github.com/user-attachments/assets/6dca68e8-d2b9-4ada-afc4-5268a61a4714" />
<img width="270" alt="Screenshot 2025-12-05 063842" src="https://github.com/user-attachments/assets/087cd4f1-2f68-4ebe-af3e-d88c5d796495" />


## 📂 Folder Structure
```text
app
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── hfad
│   │   │           └── egypttour
│   │   │               ├── EgyptTourApp.kt  (Application class / Hilt Entry Point)
│   │   │               │
│   │   │               ├── Login  (Authentication Activities/Screens)
│   │   │               │   ├── ForgotPassword.kt
│   │   │               │   ├── LoginActivity.kt
│   │   │               │   ├── Sign_in.kt
│   │   │               │   └── Signup.kt
│   │   │               │
│   │   │               ├── data  (Data Layer)
│   │   │               │   ├── api
│   │   │               │   │   ├── model
│   │   │               │   │   │   ├── WikiApiModels.kt
│   │   │               │   │   │   ├── WikiCoordinatesDto.kt
│   │   │               │   │   │   ├── WikiImageDto.kt
│   │   │               │   │   │   ├── WikiPageDto.kt
│   │   │               │   │   │   ├── WikiQuery.kt
│   │   │               │   │   │   └── WikiResponse.kt
│   │   │               │   │   ├── RetrofitInstance.kt
│   │   │               │   │   └── WikiApiService.kt
│   │   │               │   │
│   │   │               │   ├── local  (Offline/JSON Handling)
│   │   │               │   │   ├── GovernorateMapper.kt
│   │   │               │   │   ├── IdMapper.kt
│   │   │               │   │   ├── LandmarkJsonReader.kt
│   │   │               │   │   ├── LocalImageMapping.kt
│   │   │               │   │   └── LocalLandmark.kt
│   │   │               │   │
│   │   │               │   ├── model  (Domain Models & DI)
│   │   │               │   │   ├── DataModule.kt  (Hilt Module)
│   │   │               │   │   ├── Governorate.kt
│   │   │               │   │   ├── LandMark.kt
│   │   │               │   │   ├── Result.kt
│   │   │               │   │   └── User.kt
│   │   │               │   │
│   │   │               │   ├── repository  (Business Logic)
│   │   │               │   │   ├── AuthRepository.kt
│   │   │               │   │   ├── LandmarkRepository.kt
│   │   │               │   │   └── UserRepository.kt  (NEW: Favorites/Saves)
│   │   │               │   │
│   │   │               │   ├── session  (Local Storage)
│   │   │               │   │   └── SessionManager.kt  (User prefs, caching)
│   │   │               │   │
│   │   │               │   └── util  (Helpers)
│   │   │               │       ├── Constants.kt
│   │   │               │       └── PlaceholderImages.kt
│   │   │               │
│   │   │               └── ui  (Presentation Layer)
│   │   │                   ├── MainActivity.kt
│   │   │                   │
│   │   │                   ├── navigation
│   │   │                   │   └── AppNavigation.kt
│   │   │                   │
│   │   │                   ├── screens
│   │   │                   │   ├── GovernorateListScreen.kt
│   │   │                   │   ├── LandmarkDetailScreen.kt
│   │   │                   │   ├── LandmarksListScreen.kt
│   │   │                   │   ├── Profile.kt
│   │   │                   │   └── SavedLandmarksScreen.kt  (NEW: Favorites/Saves list)
│   │   │                   │
│   │   │                   ├── theme
│   │   │                   │   ├── Color.kt  (Light & Dark colors)
│   │   │                   │   ├── Theme.kt  (Light & Dark schemes)
│   │   │                   │   └── Type.kt
│   │   │                   │
│   │   │                   └── viewmodel
│   │   │                       ├── LandmarkListViewModel.kt
│   │   │                       ├── ProfileViewModel.kt
│   │   │                       └── SavedLandmarksViewModel.kt  (NEW)
│   │   │
│   │   ├── res  (Resources)
│   │   │   ├── drawable (Images like cairo.jpg, luxor.jpg, etc.)
│   │   │   ├── layout
│   │   │   ├── values
│   │   │   └── xml
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   └── assets  (Local Data)
│       ├── landmarks.json
│       └── landmark_images.json
│
└── build.gradle.kts (Module :app)
```

## 🏗️ Key Data Flows

### Favorites/Saves (Cache-First Pattern)
```
User taps ❤️ → UI updates instantly → Cache updated → Firestore sync (background)
                                                    ↓
Screen opens → Load from cache (instant) → Show UI → Firestore sync → Update if changed
```

### Dark Mode
```
User toggles switch → SessionManager saves preference → Theme recomposes → All screens update
```

## 🚀 How to Build and Run

1.  **Firebase Setup:**
    *   Create a new project on the [Firebase Console](https://console.firebase.google.com/).
    *   Add an Android app to your Firebase project with the package name `com.hfad.egypttour`.
    *   Download the `google-services.json` file and place it in the `app/` directory of the project.
    *   In the Firebase console, enable **Email/Password** authentication in the "Authentication" section.
    *   Enable **Cloud Firestore** in the "Firestore Database" section.

2.  **Clone the repository:**
    *   git clone https://github.com/your-username/egypt-tour.git

3. **Open in Android Studio:**
    *   Open the project in a recent version of Android Studio.

4. **Build the project:**
    *   Let Gradle sync and download all the required dependencies.

4. **Run the app:**
    *   Select a device or emulator and click the "Run" button.

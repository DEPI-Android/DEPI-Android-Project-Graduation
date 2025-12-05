# Egypt Explorer: An Android Guide to the Wonders of Egypt

**Egypt Tour** is a modern, content-rich Android application designed to guide users through the historical and cultural landmarks of Egypt. Built with the latest Android technologies, it provides a robust, efficient, and visually appealing user experience.

The app features a complete **Firebase Authentication** system (Sign Up, Sign In, Forgot Password) and intelligently fetches data from both a local, curated database and the public Wikipedia API. This ensures users receive high-quality, relevant, and up-to-date information in a secure, personalized environment.

## ✨ Features

*   **Full User Authentication:**
    *   **Secure Sign-Up & Sign-In:** Complete authentication flow using Firebase Auth with email and password.
    *   **User Profile Management:** Firestore is used to store and retrieve user profiles (username, email). The app includes a dedicated profile screen with options to "Logout" or "Logout from All Devices".
    *   **Session Persistence:** "Remember Me" functionality and local caching of user profiles for a seamless experience across app sessions.

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
    *   **[Cloud Firestore](https://firebase.google.com/docs/firestore):** As a cloud database for storing user profile information.
*   **Libraries:**
    *   **[Retrofit](https://square.github.io/retrofit/):** For type-safe networking and communication with the Wikipedia API.
    *   **[OkHttp](https://square.github.io/okhttp/):** The underlying HTTP client, configured with interceptors for logging and adding required headers.
    *   **[Gson](https://github.com/google/gson):** For parsing JSON data into Kotlin data classes.
    *   **[Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation):** For navigating between screens.
    *   **[StateFlow & MutableStateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow):** For managing and observing UI state reactively.
    *   **[Lifecycle Components](https://developer.android.com/jetpack/lifecycle):** `ViewModel`, `viewModelScope`, and `SavedStateHandle` for creating lifecycle-aware and highly resilient UI components.

## 🖼️ App Screenshots
<img width="357" height="800" alt="Screenshot 2025-12-05 063208" src="https://github.com/user-attachments/assets/cff423ec-ce40-41c2-9717-c9e5d4a49dab" />
<img width="353" height="797" alt="Screenshot 2025-12-05 063226" src="https://github.com/user-attachments/assets/95c7afc0-7e4d-42a3-8a8f-210e810e7779" />
<img width="353" height="791" alt="Screenshot 2025-12-05 063401" src="https://github.com/user-attachments/assets/2ea0fad9-0e80-4f41-8da3-f7c36cabe590" />
<img width="358" height="795" alt="Screenshot 2025-12-05 063421" src="https://github.com/user-attachments/assets/a6eed9b7-19a0-424d-9651-e0c075c817b1" />
<img width="353" height="796" alt="Screenshot 2025-12-05 063455" src="https://github.com/user-attachments/assets/5f5973d6-6894-4873-82f7-4e070afeab56" />
<img width="355" height="795" alt="Screenshot 2025-12-05 063541" src="https://github.com/user-attachments/assets/98e9494c-2b01-4f8e-8be0-cdf23f845277" />
<img width="355" height="789" alt="Screenshot 2025-12-05 063559" src="https://github.com/user-attachments/assets/9d115479-fc0b-44cd-9696-3db8c0dad574" />
<img width="358" height="797" alt="Screenshot 2025-12-05 063625" src="https://github.com/user-attachments/assets/8244ce48-750f-4257-a5b6-227dd0bcf8ec" />
<img width="354" height="794" alt="Screenshot 2025-12-05 063654" src="https://github.com/user-attachments/assets/11f2609e-4041-48f0-8613-b8376b3f8d9b" />
<img width="352" height="794" alt="Screenshot 2025-12-05 063842" src="https://github.com/user-attachments/assets/f1e4758f-05ea-48f3-b303-e5e1f1584c87" />
<img width="349" height="781" alt="Screenshot 2025-12-05 063852" src="https://github.com/user-attachments/assets/bb80d95b-2fff-42fc-bcc9-9879d7c7fa40" />
<img width="354" height="797" alt="Screenshot 2025-12-05 063924" src="https://github.com/user-attachments/assets/f5e673fc-bd5a-4a26-b7ad-3ed0d078618f" />
<img width="353" height="791" alt="Screenshot 2025-12-05 063935" src="https://github.com/user-attachments/assets/d6e03fdf-9947-45af-b31a-69d21b5f4292" />

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
│   │   │               ├── MainActivity.kt
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
│   │   │               │   │   └── LandmarkRepository.kt
│   │   │               │   │
│   │   │               │   └── util  (Helpers)
│   │   │               │       ├── Constants.kt
│   │   │               │       └── PlaceholderImages.kt
│   │   │               │
│   │   │               └── ui  (Presentation Layer)
│   │   │                   ├── navigation
│   │   │                   │   └── AppNavigation.kt
│   │   │                   │
│   │   │                   ├── screens
│   │   │                   │   ├── GovernorateListScreen.kt
│   │   │                   │   ├── LandmarkDetailScreen.kt
│   │   │                   │   ├── LandmarksListScreen.kt
│   │   │                   │   └── Profile.kt
│   │   │                   │
│   │   │                   ├── theme
│   │   │                   │   ├── Color.kt
│   │   │                   │   ├── Theme.kt
│   │   │                   │   └── Type.kt
│   │   │                   │
│   │   │                   └── viewmodel
│   │   │                       ├── LandmarkListViewModel.kt
│   │   │                       └── ProfileViewModel.kt
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

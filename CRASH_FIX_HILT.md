# CRASH FIX SUMMARY: LandmarkListViewModel Instantiation Error

## Problem
The app crashed on launch with:
```
java.lang.RuntimeException: Cannot create an instance of class 
com.hfad.egypttour.ui.viewmodel.LandmarkListViewModel
```

## Root Cause
The project had Hilt dependency injection configured in `build.gradle.kts`, but was **not properly initialized**:

1. **Missing `@HiltAndroidApp` Application class** - Hilt needs an entry point to initialize the DI container
2. **ViewModel not using `@HiltViewModel` annotation** - The ViewModel couldn't be injected
3. **MainActivity not using `@AndroidEntryPoint`** - Activities need this to receive Hilt injection
4. **AndroidManifest.xml not referencing the Application class** - The app wasn't using the Hilt-enabled Application
5. **ViewModel trying to instantiate repository manually** - Was passing `LandmarkRepository(RetrofitInstance.api)` as default parameter

## Solution Applied

### 1. Created `EgyptTourApp.kt` - Application Class with Hilt
**File:** `app/src/main/java/com/hfad/egypttour/EgyptTourApp.kt`

```kotlin
@HiltAndroidApp
class EgyptTourApp : Application()
```

This initializes Hilt's dependency injection container.

### 2. Created `DataModule.kt` - Hilt Dependency Provider
**File:** `app/src/main/java/com/hfad/egypttour/data/di/DataModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideLandmarkRepository(): LandmarkRepository {
        return LandmarkRepository(RetrofitInstance.api)
    }
}
```

This module provides the `LandmarkRepository` singleton to any class that needs it.

### 3. Updated `LandmarkListViewModel.kt` - Hilt Injection
**Key Changes:**
- Added `@HiltViewModel` annotation
- Changed constructor to use `@Inject` constructor
- Removed default parameter `LandmarkRepository = LandmarkRepository(RetrofitInstance.api)`
- Dependencies are now injected by Hilt

```kotlin
@HiltViewModel
class LandmarkListViewModel @Inject constructor(
    private val repository: LandmarkRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()
```

### 4. Updated `MainActivity.kt` - Hilt Support
**Key Changes:**
- Added `@AndroidEntryPoint` annotation to enable Hilt dependency injection in the Activity
- Ensured `EgyptTourApp` is used as the application class

### 5. Updated `AndroidManifest.xml` - Reference Hilt App
**Change:**
```xml
<application android:name=".EgyptTourApp" ...>
```

This tells Android to use the Hilt-enabled application class.

## How Hilt Dependency Injection Works

1. **@HiltAndroidApp** initializes the DI container when the app starts
2. **DataModule** declares how to create `LandmarkRepository` instances
3. **@HiltViewModel** marks the ViewModel for Hilt injection
4. **@AndroidEntryPoint** allows Activities to receive injected dependencies
5. When `LandmarkListScreen` calls `viewModel()`, Hilt:
   - Sees it needs `LandmarkListViewModel`
   - Looks at the constructor parameters: `repository` and `savedStateHandle`
   - Gets `repository` from `DataModule.provideLandmarkRepository()`
   - Automatically creates `savedStateHandle` for process-death recovery
   - Constructs and returns the ViewModel

## Result
✅ App will no longer crash on startup
✅ ViewModel properly receives dependencies
✅ Process-death state is preserved via SavedStateHandle
✅ Repository is a singleton (same instance across the app)

## Testing
Build and run the app:
```bash
./gradlew clean build
./gradlew installDebug
```

The app should now launch without crashing and properly load landmarks when you tap on a governorate.


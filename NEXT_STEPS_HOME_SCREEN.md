# Next Steps: Building the Home Screen with Governorates

Now that the crash is fixed, here's what you need to do to create a beautiful home screen showing all governorates.

## What You Need to Build

### Step 1: Create a GovernorateListScreen
**Location:** `app/src/main/java/com/hfad/egypttour/ui/screens/GovernorateListScreen.kt`

This screen will display all governorates in a beautiful grid/list layout.

### Step 2: Create Navigation with Compose Navigation
Update your navigation to:
- Home Screen (shows governorates)
- Landmark List Screen (shows landmarks for selected governorate)
- Landmark Detail Screen (shows details for a specific landmark)

### Step 3: Update MainActivity to Use Navigation
Instead of directly displaying `LandmarkListScreen`, use Compose Navigation:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EgyptTourTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}
```

---

## Your Governorate Data

You already have this in `Governorate.kt`:

```kotlin
enum class Governorate(
    val id: String,
    val displayName: String,
    val wikiCategory: String,
    val imageUrl: String
) {
    CAIRO(...),
    LUXOR(...),
    ASWAN(...),
    GIZA(...),
    ALEXANDRIA(...)
}
```

You can access all governorates with: `Governorate.entries`

---

## Beautiful GovernorateListScreen Design Suggestion

```
┌─────────────────────────────────┐
│      Egypt Tour               ⚙️  │  ← TopAppBar with Gold color
├─────────────────────────────────┤
│                                 │
│  ┌───────────────┐              │
│  │               │ Cairo        │  ← Card 1
│  │    [Image]    │ Tourist...   │
│  │               │              │
│  └───────────────┘              │
│                                 │
│  ┌───────────────┐              │
│  │               │ Luxor        │  ← Card 2
│  │    [Image]    │ Ancient...   │
│  │               │              │
│  └───────────────┘              │
│                                 │  ← Scroll...
│  ┌───────────────┐              │
│  │               │ Aswan        │  ← Card 3
│  │    [Image]    │ Nubian...    │
│  │               │              │
│  └───────────────┘              │
└─────────────────────────────────┘
```

---

## Key Components You'll Need

1. **GovernorateCard** - Composable to display a single governorate
   - Background image
   - Governorate name
   - Short description (optional)
   - Ripple effect on click

2. **LazyColumn or LazyVerticalGrid** - To display the list
   - `LazyColumn` for simple vertical list
   - `LazyVerticalGrid` for 2-column grid

3. **TopAppBar** - Using your Gold color (EgyptGold)

4. **Error Handling** - For image loading failures

---

## Colors to Use

From your `Color.kt`:
```kotlin
val EgyptGold = Color(0xFFE4B643)      // Main color for buttons/headers
val SoftPeach = Color(0xFFF8D8B6)      // Card backgrounds
val SoftWhite = Color(0xFFFAFAFA)      // Screen background
val TextBlack = Color(0xFF333333)      // Main text
```

---

## Ready to Build?

Once you fix the crash (which we just did), you can ask me to:

1. **Create GovernorateListScreen** - Beautiful list of governorates
2. **Create GovernorateCard** - Reusable card component
3. **Set up Navigation** - Handle navigation between screens
4. **Create LandmarkDetailScreen** - Show details for a landmark
5. **Add animations** - Smooth transitions between screens

Just let me know when you're ready! 🚀


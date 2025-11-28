# 🚀 Quick Start Guide - Egypt Tour App

## Prerequisites

- Android Studio Hedgehog or newer
- Android SDK 24+ (API level)
- Gradle 8.0+
- JDK 17

## Step 1: Build the Project

### From Terminal:
```bash
cd "/media/epic-dev/New Volume/TechTree/DEPI-GP/theProject/DEPI-Android-Project-Graduation"

# Clean and build
./gradlew clean build
```

### From Android Studio:
1. Open the project folder
2. Wait for Gradle sync (File → Sync Now)
3. Build → Clean Project
4. Build → Build Project

**Expected:** Build completes without errors

---

## Step 2: Run on Device/Emulator

### From Terminal:
```bash
# Install on connected device/emulator
./gradlew installDebug

# Or run directly
./gradlew runDebug
```

### From Android Studio:
1. Connect device or start emulator
2. Click green "Run" button (or press Shift+F10)
3. Select target device
4. Click "OK"

**Expected:** App installs and launches

---

## Step 3: Test the App

### Test Checklist:

#### 1. Home Screen
- [ ] App launches without crashing
- [ ] "🇪🇬 Egypt Tour" title visible
- [ ] "Discover Egypt's Governorates" text visible
- [ ] 5 governorate cards visible:
  - [ ] Cairo
  - [ ] Luxor
  - [ ] Aswan
  - [ ] Giza
  - [ ] Alexandria
- [ ] Cards have images
- [ ] Cards are clickable (ripple effect on tap)

#### 2. Governorate Card Details
- [ ] Governorate name visible
- [ ] "Tap to explore" text visible
- [ ] Gold accent bar at top

#### 3. Navigation to Landmarks
- [ ] Tap "Cairo" card
- [ ] App navigates to LandmarkListScreen
- [ ] Landmark title shows "Cairo"
- [ ] Back button appears in TopAppBar
- [ ] Landmarks load (may take 2-3 seconds)

#### 4. Landmarks Screen
- [ ] Search bar visible
- [ ] Landmarks display in list
- [ ] Scroll works smoothly
- [ ] Images load

#### 5. Search Functionality
- [ ] Type in search bar
- [ ] Results filter in real-time
- [ ] Clear button appears when typing
- [ ] Tap clear button → resets search

#### 6. Back Navigation
- [ ] Tap back button or system back
- [ ] Returns to home screen
- [ ] Scroll position preserved (if you scroll down)

#### 7. Multiple Governorates
- [ ] Tap "Luxor" card
- [ ] Landmarks change to Luxor
- [ ] Back button works
- [ ] Back to home
- [ ] Tap "Alexandria"
- [ ] Different landmarks appear

---

## Troubleshooting

### Issue: Build fails with Kotlin version error
**Solution:**
```bash
# Clean gradle cache
./gradlew clean

# Delete build folder
rm -rf app/build

# Rebuild
./gradlew build
```

### Issue: "Cannot create instance of ViewModel"
**Solution:**
- Make sure EgyptTourApp.kt exists
- Make sure DataModule.kt exists
- Check AndroidManifest.xml has `android:name=".EgyptTourApp"`
- Clean build and reinstall

### Issue: App crashes when tapping card
**Solution:**
- Check Logcat for detailed error
- Verify you have internet connection (API calls)
- Check governorate ID is correct

### Issue: Images don't load
**Solution:**
- Check internet connection
- Check Coil configuration
- Try clearing app cache: Settings → Apps → Egypt Tour → Clear Cache

### Issue: Slow performance
**Solution:**
- Use a faster device or emulator
- Check RAM usage
- Disable animations in developer options for testing

---

## Performance Testing

### Memory Profiler:
1. Android Studio → View → Tool Windows → Profiler
2. Run app
3. Monitor memory usage
4. Tap cards and navigate
5. Check for memory leaks

### Layout Inspector:
1. Android Studio → View → Tool Windows → Layout Inspector
2. Run app
3. Inspect UI hierarchy
4. Check spacing and alignment

### Network Profiler:
1. Android Studio → Profiler
2. Check Network tab
3. Monitor API calls
4. Check response times

---

## Debugging Tips

### Enable Logcat Filtering:
```
Filter: "egypt" or "hilt" or "landmark"
```

### Key Log Messages to Look For:
```
D/egypttour: Restoring landmarks for Cairo
D/egypttour: Loading landmarks for Cairo
D/egypttour: Fetching landmarks for Cairo
```

### Check State:
1. Add breakpoints in ViewModel
2. Step through code
3. Inspect StateFlow values
4. Check navigation arguments

---

## Common Commands

```bash
# Full clean build
./gradlew clean build

# Build without tests
./gradlew build -x test

# Install and run
./gradlew installDebug

# View all tasks
./gradlew tasks

# Run tests
./gradlew test

# Generate APK
./gradlew assembleDebug

# Run lint
./gradlew lint
```

---

## File Locations for Quick Reference

```
Project Root:
/media/epic-dev/New Volume/TechTree/DEPI-GP/theProject/DEPI-Android-Project-Graduation

Source Files:
app/src/main/java/com/hfad/egypttour/
├── MainActivity.kt
├── EgyptTourApp.kt
├── data/
├── ui/
└── ...

Resources:
app/src/main/res/
├── drawable/
├── layout/
└── values/

Manifest:
app/src/main/AndroidManifest.xml

Build Config:
app/build.gradle.kts
build.gradle.kts
gradle.properties

Documentation:
CRASH_FIX_HILT.md
HOME_SCREEN_IMPLEMENTATION.md
ARCHITECTURE_GUIDE.md
PROJECT_STATUS.md
```

---

## Next Features to Implement

### Short Term (Easy):
1. Create LandmarkDetailScreen
2. Add favorite button
3. Add share functionality

### Medium Term (Medium):
1. Add offline support
2. Implement proper error screens
3. Add animations

### Long Term (Hard):
1. User authentication
2. Backend integration
3. AR features

---

## Getting Help

### Documentation Files:
- `CRASH_FIX_HILT.md` - Explains the Hilt setup
- `HOME_SCREEN_IMPLEMENTATION.md` - UI implementation details
- `ARCHITECTURE_GUIDE.md` - System architecture
- `PROJECT_STATUS.md` - Current status

### Code Comments:
- Check source files for inline comments
- Look for TODO comments for future work

### Common Issues:
1. **Crash on startup** → Check Hilt setup
2. **Navigation not working** → Check AppNavigation routes
3. **Images not loading** → Check internet connection
4. **Slow performance** → Check memory profiler

---

## Success Criteria

✅ You'll know it's working when:
- App launches without crashing
- Home screen shows all 5 governorates
- Tapping a card shows landmarks
- Back button returns to home
- Search filters landmarks
- No force close/crash dialogs

---

## Tips for Development

1. **Use Layout Inspector** to debug UI issues
2. **Enable Strict Mode** to catch performance problems
3. **Use Logcat** to debug logic
4. **Test on real device** for best experience
5. **Clear app cache** if something seems off

---

## Testing Devices

### Recommended:
- Pixel 3/4/5 (virtual or real)
- Samsung Galaxy (real)
- Any device with API 24+

### Emulator Settings:
- Resolution: 1080 x 1920
- RAM: 2GB minimum
- Storage: 100MB free

---

## Performance Targets

- **Startup time:** < 3 seconds
- **Navigation:** Instant
- **Image loading:** < 1 second
- **Search:** Real-time filtering
- **Memory:** < 150MB

---

## Final Checklist

Before considering app "ready":
- [ ] Builds without errors
- [ ] Installs successfully
- [ ] Launches without crash
- [ ] All UI elements visible
- [ ] Navigation works
- [ ] Search works
- [ ] Back button works
- [ ] Images load
- [ ] No memory leaks
- [ ] Smooth scrolling

---

## Enjoy! 🎉

Your Egypt Tour app is ready to explore!

Questions? Check the documentation files or examine the source code comments.

Happy developing! 🚀


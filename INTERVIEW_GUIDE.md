# 🎤 Namma-Shaale — Interview Guide & Project Summary

---

## SECTION 1: PROJECT SUMMARY (30-second elevator pitch)

> "I built Namma-Shaale Inventory, a full-stack Android application for school asset management.
> The app lets school staff add assets with photos, track their condition, log issues, and
> generate summary reports. It uses Clean Architecture with MVVM, Jetpack Compose for UI,
> Room for local persistence, CameraX for photo capture, and integrates with the OpenAI API
> to auto-generate issue descriptions and suggest repairs using AI. It has full offline support
> with an AI mock fallback when no internet is available."

---

## SECTION 2: ARCHITECTURE DEEP DIVE

### Q: Why did you choose Clean Architecture?

**Answer:**
"Clean Architecture separates concerns into three layers — Presentation, Domain, and Data.
This gives three key benefits:

1. **Testability** — The domain layer has zero Android dependencies. I can unit test all
   business logic (UseCases) with plain JUnit, no Android emulator needed.

2. **Replaceability** — My ViewModels depend on repository *interfaces*, not Room. If I
   switch from Room to SQLDelight tomorrow, only the data layer changes — not the ViewModels.

3. **Scalability** — Adding a new feature (like export to PDF) is just adding a new UseCase
   class and a new Screen. No existing code needs to change."

---

### Q: Explain your MVVM pattern.

**Answer:**
"Each screen has exactly one ViewModel. The ViewModel:
- Exposes state as **StateFlow** (immutable from the UI perspective)
- Calls use cases for business operations
- Never references Android Context directly (except via Hilt's @ApplicationContext)
- Survives configuration changes automatically

The Screen:
- Collects StateFlow using `collectAsState()`
- Is completely stateless — all state lives in the ViewModel
- Calls ViewModel functions on user events (button clicks, etc.)

This means if you rotate the screen, the data is preserved because it lives in the
ViewModel, which survives configuration changes."

---

### Q: How does your Room database work?

**Answer:**
"I have two entities: `AssetEntity` and `IssueLogEntity` with a foreign key relationship.
When an asset is deleted, all its issue logs are cascade-deleted automatically.

Each entity has a `toDomain()` method and a `fromDomain()` companion factory method.
This is the Mapper Pattern — the DAO only works with entities, the repository converts
them to/from domain models. The ViewModel and UseCases never see the entity classes.

All DAO read operations return `Flow<T>`, which means the UI automatically updates
whenever data changes — this is reactive programming with Room."

---

### Q: How did you implement Search?

**Answer:**
"I used a combination of database search and in-memory filtering.
The `SearchAssetsUseCase` takes a query string and an optional condition filter.
It uses `getAllAssets()` from the repository (which returns a Flow), then uses
Kotlin's `.map { }` operator to filter the list in-memory.

In the ViewModel, I used `debounce(300L)` on the search query StateFlow.
This means the search doesn't fire on every single keystroke — it waits 300ms after
the user stops typing, reducing unnecessary database queries significantly."

---

### Q: Explain your CameraX integration.

**Answer:**
"CameraX requires three use cases bound together:
1. **Preview** — shows the live viewfinder
2. **ImageCapture** — actually takes the photo

I bind these to the Activity lifecycle via `ProcessCameraProvider.bindToLifecycle()`.
When the user taps the shutter, `takePicture()` is called on the ImageCapture use case.
The captured image is first saved to a temp cache file, then moved to permanent app
storage using `filesDir`. I use a `FileProvider` to create content URIs safely,
as direct file URIs are blocked on Android 7+ for security reasons."

---

### Q: How does your OpenAI integration work?

**Answer:**
"I use Retrofit2 to call the OpenAI Chat Completions API (`v1/chat/completions`).
The request contains a system prompt that establishes context ('You are a school
maintenance advisor…') and a user prompt with the specific asset name and condition.

I built a mock fallback system — if the API key is 'MOCK_KEY' (the default when
no key is configured), the repository returns realistic pre-written responses instead
of making a network call. This means the app is fully functional even without an
API key or internet connection, which is important for offline-first school environments.

The API key is stored in `local.properties` (never committed to Git) and injected
into the binary at build time via `buildConfigField` in Gradle."

---

### Q: How is Hilt (Dependency Injection) set up?

**Answer:**
"I have three Hilt modules:

1. **DatabaseModule** — Provides the Room database and DAOs as singletons
2. **NetworkModule** — Provides OkHttp, Retrofit, and OpenAiService as singletons
3. **RepositoryModule** — Binds interface types to their implementations
   (e.g., `AssetRepository` → `AssetRepositoryImpl`)

The key insight is `RepositoryModule` uses `@Binds` (abstract functions) instead
of `@Provides`. This is more efficient because Hilt doesn't need to instantiate the
module class to call the binding — it's resolved entirely at compile time."

---

### Q: What is StateFlow and why did you use it over LiveData?

**Answer:**
"StateFlow is Kotlin-native (no Android lifecycle dependency), always has a value
(unlike MutableLiveData), is thread-safe, and integrates perfectly with Coroutines.

Key differences from LiveData:
- StateFlow is pure Kotlin — testable without Android instrumentation
- StateFlow is cold by default but can be made hot via `stateIn()`
- StateFlow doesn't deliver events to already-finished observers (no sticky events issue)
- Compose's `collectAsState()` works perfectly with StateFlow

I use `SharingStarted.WhileSubscribed(5000)` on shared flows — this means the
upstream (Room flow) is active only when there's at least one subscriber, and
stays active for 5 seconds after the last subscriber disappears. This handles
screen rotation gracefully."

---

## SECTION 3: TECHNICAL QUESTIONS

### Q: What was the hardest part of this project?

**Answer (pick one that applies):**
"The hardest part was getting the photo flow working end-to-end. The challenge was:
1. CameraX runs on the main thread but file I/O should be on a background thread
2. FileProvider needs to be configured in AndroidManifest to share file URIs
3. The photo path needs to survive navigation back from Camera to AddAsset

I solved this using the Navigation back stack's SavedStateHandle. The CameraScreen
puts the photo path into the previous screen's SavedStateHandle. MainActivity
observes this and updates a `capturedPhotoPath` state that AddAssetScreen reads.
This is the recommended navigation pattern for returning data from a screen."

---

### Q: How would you scale this for a district with 500 schools?

**Answer:**
"For multi-school scale, I would:
1. Replace Room with a cloud database (Firebase Firestore or Supabase)
2. Add authentication (Firebase Auth or Auth0) for per-school access control
3. Implement offline-first sync using WorkManager for background sync
4. Add a backend API (Spring Boot or Node.js) for centralized reporting
5. Use Paging3 for large asset lists instead of loading everything in memory
6. Add push notifications (FCM) for repair request alerts"

---

### Q: How do you handle errors?

**Answer:**
"I use Kotlin's `Result<T>` type in use cases. Every use case that can fail returns
`Result<T>` — the ViewModel calls `.fold(onSuccess = {}, onFailure = {})` to handle
both paths.

In the UI, errors are shown as Snackbar messages using `SnackbarHostState`.
The ViewModel holds the error as a nullable String in its UiState — when not null,
the Screen shows the Snackbar. After showing, the Screen calls `clearError()` on
the ViewModel to reset the state, preventing the error from showing again on recomposition."

---

## SECTION 4: ATS-OPTIMIZED RESUME DESCRIPTION

### Option A — Short (1 line for skills section)
```
Namma-Shaale Inventory | Kotlin, Jetpack Compose, MVVM, Room, Hilt, CameraX, OpenAI API
```

### Option B — Medium (2-3 lines for experience/projects section)
```
Namma-Shaale Inventory — Android Asset Management App                    [2024]
• Production-grade Android app built with Kotlin, Jetpack Compose, and Clean Architecture
  (MVVM + UseCases + Repository pattern) for school physical asset auditing
• Integrated CameraX for photo capture, Room DB with Flow for reactive persistence,
  and OpenAI API for AI-powered issue descriptions and repair suggestions
• Implemented full-text search with debouncing, conditional filtering, monthly health
  check system, and text report generation with clipboard/share functionality
```

### Option C — Detailed (3-5 lines, most ATS keywords)
```
Namma-Shaale Inventory — Digital Asset Auditor for Schools               [2024]
Android | Kotlin | Jetpack Compose | MVVM | Clean Architecture | Hilt | Room | CameraX | OpenAI

• Architected a production Android application using Clean Architecture (Presentation /
  Domain / Data layers) with MVVM pattern, ensuring full separation of concerns and
  testability without Android framework dependencies in the domain layer
• Built a reactive data layer with Room Database + Kotlin Flow, exposing StateFlow to
  ViewModels for automatic UI updates; used Hilt for dependency injection across all layers
• Integrated CameraX with ImageCapture use case for in-app photo capture; implemented
  FileProvider for secure URI sharing across processes on Android API 26+
• Connected OpenAI gpt-3.5-turbo API via Retrofit2 + OkHttp3 for GenAI-powered issue
  description generation and repair action suggestions, with offline mock fallback
• Implemented debounced real-time search with Kotlin's Flow debounce operator and
  multi-condition filtering; built a text report generator with clipboard and Intent sharing
• Wrote unit tests for all UseCases using Fake repositories and kotlinx-coroutines-test,
  achieving test coverage without Android emulator dependencies
```

---

## SECTION 5: QUICK REFERENCE CHEATSHEET

| Concept | File to Reference |
|---|---|
| Room Entity | `AssetEntity.kt` |
| Room DAO | `AssetDao.kt` |
| Repository Pattern | `AssetRepositoryImpl.kt` |
| Clean Architecture | `domain/usecase/` folder |
| Hilt DI Setup | `DatabaseModule.kt`, `RepositoryModule.kt` |
| StateFlow pattern | `DashboardViewModel.kt` |
| CameraX integration | `CameraScreen.kt` |
| OpenAI API call | `AiRepositoryImpl.kt` |
| Search + Debounce | `AssetListViewModel.kt` |
| Navigation | `NavGraph.kt`, `Screen.kt` |
| Unit Testing | `AddAssetUseCaseTest.kt` |

---

## SECTION 6: THINGS TO MENTION PROACTIVELY

1. **"I used Clean Architecture so that my UseCase tests run in pure JVM — no Android emulator"**
2. **"FileProvider is required on API 24+ for sharing file URIs — I handle this in the manifest"**
3. **"I store the API key in `local.properties` which is git-ignored — never committed to source control"**
4. **"The AI integration has an automatic mock fallback for offline environments"**
5. **"I used `SharingStarted.WhileSubscribed(5000)` to handle screen rotation efficiently"**
6. **"Cascade delete in Room ensures orphaned issue logs are automatically cleaned up"**

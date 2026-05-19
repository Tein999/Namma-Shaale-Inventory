# 📚 Namma-Shaale Inventory
### Digital Asset Auditor for Schools

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-purple.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM_Clean-orange.svg)](https://developer.android.com/topic/architecture)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📸 Screenshots

> *Dashboard | Asset List | Add Asset | Issue Log | Report*

---

## 🎯 Overview

**Namma-Shaale Inventory** is a production-grade Android application designed to help schools digitally audit, track, and manage physical assets like computers, projectors, chairs, lab equipment, and more.

Built entirely with modern Android stack — Kotlin, Jetpack Compose, Room, Hilt, CameraX, and an OpenAI integration for AI-powered issue descriptions and repair suggestions.

---

## ✨ Features

| Feature | Description |
|---|---|
| 📦 **Asset Management** | Add, view, update, and delete school assets |
| 📷 **Photo Capture** | Take photos of assets using CameraX |
| 🏷️ **Condition Tracking** | Working / Needs Repair / Broken with color coding |
| 📊 **Dashboard** | Real-time stats: total, working, repair needed, broken |
| 🔍 **Search + Filter** | Full-text search + filter by condition |
| 📋 **Issue Log** | Log issues per asset with date and AI suggestion |
| 🏥 **Monthly Health Check** | Mark all assets as checked in one tap |
| 🤖 **AI Integration** | OpenAI-powered issue descriptions & repair suggestions |
| 📄 **Report Generation** | Text-based summary report (copy + share) |
| 🌙 **Material3 UI** | Professional, clean, card-based interface |

---

## 🏗️ Architecture

This project follows **Clean Architecture** with **MVVM** presentation pattern:

```
┌─────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                     │
│   Compose Screens ──► ViewModels ──► UseCases           │
├─────────────────────────────────────────────────────────┤
│                     DOMAIN LAYER                         │
│   Models │ Repository Interfaces │ Use Cases            │
├─────────────────────────────────────────────────────────┤
│                      DATA LAYER                          │
│   Room DB │ Retrofit (OpenAI) │ Repository Impls        │
└─────────────────────────────────────────────────────────┘
```

**Data flow:** `Screen` ← collects StateFlow ← `ViewModel` ← calls `UseCase` ← reads `Repository` ← reads `Room DB`

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0 |
| **UI** | Jetpack Compose + Material3 |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt (Dagger) |
| **Database** | Room with Flow |
| **State** | StateFlow + collectAsState |
| **Camera** | CameraX (Preview + ImageCapture) |
| **Networking** | Retrofit2 + OkHttp3 + Gson |
| **Image Loading** | Coil Compose |
| **Permissions** | Accompanist Permissions |
| **Navigation** | Navigation Compose |
| **AI** | OpenAI Chat Completions API (gpt-3.5-turbo) |
| **Async** | Kotlin Coroutines |
| **Testing** | JUnit4 + Kotlin Coroutines Test |

---

## 📁 Project Structure

```
app/src/main/java/com/nammashale/inventory/
│
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAOs (AssetDao, IssueLogDao)
│   │   ├── entity/           # Room Entities (AssetEntity, IssueLogEntity)
│   │   └── AppDatabase.kt    # Room Database
│   ├── remote/
│   │   ├── dto/              # OpenAI Request/Response DTOs
│   │   └── OpenAiService.kt  # Retrofit interface
│   └── repository/
│       ├── AssetRepositoryImpl.kt
│       └── AiRepositoryImpl.kt
│
├── domain/
│   ├── model/                # Pure Kotlin data classes
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Business logic use cases
│
├── presentation/
│   ├── navigation/           # NavGraph + Screen routes
│   ├── theme/                # Color, Typography, Theme
│   ├── components/           # Reusable Composables
│   ├── dashboard/            # Dashboard screen + VM
│   ├── addasset/             # Add Asset screen + VM
│   ├── assetlist/            # Asset list + search screen + VM
│   ├── assetdetail/          # Asset detail + condition update + VM
│   ├── issuelog/             # Issue log screen + VM
│   ├── camera/               # CameraX screen
│   └── report/               # Report generation screen + VM
│
├── di/                       # Hilt DI modules
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
└── utils/                    # DateUtils, FileUtils, Constants
```

---

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK API 26+
- Android Emulator or physical device

### Step 1 — Clone the Repository
```bash
git clone https://github.com/YOUR_USERNAME/NammaShaaleInventory.git
cd NammaShaaleInventory
```

### Step 2 — Configure local.properties
```properties
# In local.properties (create in project root):
sdk.dir=/path/to/your/Android/sdk

# Optional: Add your OpenAI API key (app works in mock mode without it)
OPENAI_API_KEY=sk-proj-your-key-here
```

### Step 3 — Open in Android Studio
- File → Open → Select the project root folder
- Wait for Gradle sync to complete

### Step 4 — Run
- Select a device (emulator or physical)
- Click Run ▶ or press `Shift+F10`

---

## 🤖 AI Integration (OpenAI)

### With Real API Key
1. Get an API key from [platform.openai.com](https://platform.openai.com)
2. Add to `local.properties`: `OPENAI_API_KEY=sk-proj-...`
3. Rebuild the project

### Mock Mode (Default)
If no API key is configured, the app automatically uses built-in mock responses that are:
- Realistic and context-aware
- Work fully offline
- Safe for demos and testing

---

## 🎨 Color Scheme

| Condition | Color | Hex |
|---|---|---|
| ✅ Working | Green | `#4CAF50` |
| ⚠️ Needs Repair | Amber | `#FFC107` |
| ❌ Broken | Red | `#F44336` |
| 🔵 Primary | Blue | `#1976D2` |

---

## 🧪 Running Tests

```bash
# Unit tests
./gradlew test

# Tests with coverage
./gradlew testDebugUnitTest

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

---

## 📱 Navigation Map

```
Dashboard
├── → Asset List
│     └── → Asset Detail
│           └── → Issue Log
├── → Add Asset
│     └── → Camera (CameraX)
├── → All Issue Logs
└── → Report
```

---

## 🏫 Sample Data

After install, populate the app with sample data from **Settings → Load Demo Data** *(or add manually)*:

| Asset | Serial | Location | Condition |
|---|---|---|---|
| Dell Laptop | LAP-2024-001 | Computer Lab | Working |
| Epson Projector | PROJ-2023-005 | Room 101 | Needs Repair |
| School Bell | BELL-2022-001 | Principal's Office | Broken |
| Microscope | LAB-2024-011 | Science Lab | Working |

---

## ⚠️ Common Issues & Fixes

| Issue | Fix |
|---|---|
| Camera not working on emulator | Use Extended Controls → Camera → Webcam |
| Hilt build error | Ensure `@HiltAndroidApp` on Application class, `@AndroidEntryPoint` on Activity |
| Room schema not found | Add `exportSchema = true` and `schemaLocation` to Room annotation processor args |
| Coil image not loading | Check file path exists with `File(path).exists()` before loading |
| OpenAI 429 error | Rate limited — wait a minute or switch to mock mode |
| Build fails on kapt | Add `kapt { correctErrorTypes = true }` in `app/build.gradle.kts` |

---

## 📄 License

```
MIT License — Free to use for educational and commercial purposes.
```

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -m 'feat: add awesome feature'`
4. Push: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 👨‍💻 Author

Built with ❤️ for Indian schools — *Namma Shaale* (ನಮ್ಮ ಶಾಲೆ) means *Our School* in Kannada.
# Namma-Shaale-Inventory

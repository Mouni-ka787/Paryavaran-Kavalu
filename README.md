# 🌿 Paryavaran-Kavalu

<div align="center">

**REPORT · TAG · CLEAN · PROTECT**

*Together for a Cleaner Tomorrow* 🌱

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Language](https://img.shields.io/badge/Language-Java%2FKotlin-orange.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/Maps-Google%20Maps%20API-blue.svg)](https://developers.google.com/maps)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-yellow.svg)](https://firebase.google.com)
[![MindMatrix](https://img.shields.io/badge/MindMatrix-VTU%20Internship-purple.svg)](https://mindmatrix.io)

</div>

---

## 📋 Project Overview

**Paryavaran-Kavalu** (meaning "Environment Guard" in Kannada) is a geo-tagging Android application built for community cleanliness. It empowers citizens to report environmental hazards instantly, helping local cleanup volunteers (Swachh Bharat units) identify and act on "Waste Blackspots" — illegal garbage dumping sites in open spaces.

> 🏆 **MindMatrix VTU Internship Program** — Project Title #80

---

## 🚨 The Problem

Illegal garbage dumping in open spaces, known as **"Waste Blackspots,"** poses a significant health risk. Local cleanup volunteers often miss these spots because they lack a centralized reporting system with precise location data.

---

## 💡 The Solution

Paryavaran-Kavalu acts as a real-time bridge between citizens and cleanup volunteers by:

- Letting users instantly **report** waste blackspots with geo-tagged photos
- Displaying all reports on a **Cleanliness Map** with status pins
- Rewarding active contributors with **Eco-Karma** points
- Providing an **Admin panel** to manage reports and track progress

---

## 📱 App Screenshots

| Login | Map | Report | History | Profile |
|-------|-----|--------|---------|---------|
| Sign in / Sign up with email | Cleanliness Map with report pins | New Waste Report form | Report History with filter | Eco-Karma & Badges |

---

## ✨ Features

### 👤 User App
| Feature | Description |
|--------|-------------|
| 🔐 **Authentication** | Email/password login & sign-up via Firebase Auth |
| 🗺️ **Cleanliness Map** | Google Maps view with red (Pending) and green (Cleaned) pins |
| 📸 **Quick Report** | Attach photo (camera or gallery), select waste category, auto-capture GPS coordinates |
| 📍 **Geo-Tagging** | Automatic GPS coordinate logging using `FusedLocationProviderClient` |
| 📂 **Waste Categories** | Plastic, Organic, Mixed, Medical, and more |
| 📜 **Report History** | Filter by All / Pending / Cleaned status |
| ⭐ **Eco-Karma Points** | +10 points per verified report |
| 🏅 **Badges & Ranks** | Sapling → Tree → Forest Guardian progression |
| ✅ **Mark as Cleaned** | Volunteers can mark spots as cleaned |

### 🛡️ Admin Panel
| Feature | Description |
|--------|-------------|
| 📋 **All Reports** | View all submitted waste reports with location & status |
| 👥 **User Management** | View registered users |
| 🗺️ **Reports Map** | Geo-visualize all reports on a map |
| 👤 **Admin Profile** | Admin account management & logout |

---

## 🛠️ Technical Implementation

### Architecture
- **Pattern**: Clean separation of UI and Business Logic (MVVM / MVC)
- **Language**: Java / Kotlin
- **Min SDK**: Android 7.0 (API 24+)

### Key Libraries & APIs

| Technology | Purpose |
|-----------|---------|
| Google Maps SDK | MapView with custom markers |
| FusedLocationProviderClient | GPS coordinate capture |
| Firebase Authentication | User login & registration |
| Firebase Firestore | Real-time report data storage |
| Firebase Storage | Photo upload & retrieval |
| Image Compression | Photos compressed to stay under 500KB |

### App Flow

```
Launch
  └── Login / Sign Up
        └── Main App (Bottom Nav)
              ├── Map          → Cleanliness Map with colored pins
              ├── Report (+)   → New Waste Report Form
              │     ├── Auto GPS capture
              │     ├── Photo (Camera / Gallery)
              │     ├── Waste Category selection
              │     └── Submit → +10 Eco-Karma
              ├── History      → Filter: All / Pending / Cleaned
              └── Profile      → Points, Rank, Badges, Logout
```

---

## 🏆 Eco-Karma & Gamification

| Rank | Points Required | Badge |
|------|----------------|-------|
| 🌱 Sapling | 0 – 49 | First report submitted |
| 🌳 Tree | 50 – 199 | 10+ reports submitted |
| 🌲 Forest Guardian | 200+ | First cleanup verified |

**Achievements Unlocked:**
- 🌱 **First Report** — Submitted your very first waste spot report
- 🔒 **10 Reports** — Reported 10 or more waste spots
- ✅ **First Cleanup** — Marked your first spot as cleaned

---

## 🎯 Success Criteria

- [x] App captures and displays user's current Latitude/Longitude
- [x] Map markers change color based on status (Reported → Red, Cleaned → Green)
- [x] Photo upload compressed to stay under 500KB
- [x] Clean separation between UI and Business logic
- [x] Admin panel with report management

---

## 🌍 Impact Goals

- **Swachh Bharat 2.0** — Leveraging technology to maintain a garbage-free India
- **Environmental Stewardship** — Encouraging youth to take ownership of their surroundings
- **Public Health** — Reducing disease vectors by clearing illegal dump sites

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest stable)
- Google Maps API Key
- Firebase project (Auth + Firestore + Storage enabled)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/paryavaran-kavalu.git
   cd paryavaran-kavalu
   ```

2. **Add your `google-services.json`**  
   Download from your Firebase Console and place it in the `app/` directory.

3. **Add your Google Maps API Key**  
   In `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE"/>
   ```

4. **Build and Run**  
   Open in Android Studio → Sync Gradle → Run on device/emulator

---

## 📁 Project Structure

```
app/
├── ui/
│   ├── auth/          # Login & Sign Up screens
│   ├── map/           # Cleanliness Map fragment
│   ├── report/        # New Waste Report screen
│   ├── history/       # Report History screen
│   ├── profile/       # User Profile & Eco-Karma
│   └── admin/         # Admin panel (Reports, Users, Map)
├── data/
│   ├── model/         # Data models (Report, User, etc.)
│   └── repository/    # Firebase data access layer
└── utils/             # Location, image compression helpers
```

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.

---

## 📄 License

This project was developed as part of the **MindMatrix VTU Internship Program**.

---

<div align="center">
  Made with 💚 for a cleaner India &nbsp;|&nbsp; <b>Paryavaran-Kavalu</b>
</div>

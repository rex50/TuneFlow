# 🎵 TuneFlow - Smart Driving Volume Control

> 🚗💨 Your music, perfectly tuned to your speed!

TuneFlow is an intelligent Android application that automatically adjusts your media volume based on driving speed. Perfect for maintaining optimal music volume as driving conditions change – because your favorite song should sound great whether you're cruising or accelerating! 🎶

## ✨ Features

### 🎯 Clean UI/UX
- 🏠 Intuitive main screen with profiles and essential controls
- 📊 Real-time speedometer gauge visualization
- 🔘 Expandable FAB that collapses when scrolling through profiles
- 🎨 Material Design 3 with dynamic theming
- ✨ Smooth animations and transitions

### 👤 Multiple Profiles
- 📋 Create and manage multiple driving profiles
- 🎨 Customize each profile with unique colors
- 🔄 Switch between profiles for different driving scenarios
- ✏️ Edit or delete profiles as needed
- 🛣️ Default profiles included: City Drive, Highway Drive, Cycling and Running

### ⚡ Quick Settings Tile
- 🔘 Toggle service directly from Android Quick Settings panel
- 🎯 One-tap access without opening the app
- 📱 Real-time status indication

### 📡 Background Speed Monitoring
- 🔄 Runs as a foreground service to continuously monitor driving speed
- 📱 Uses GPS location to detect speed changes
- 🎯 Applies smoothing algorithms to reduce jitter and provide stable volume adjustments
- 🔋 Low battery impact with optimized location polling

### 🔊 Customizable Volume Range
- 🔉 Set minimum volume level (0% - 100%)
- 🔊 Set maximum volume level (0% - 100%)
- 📊 Volume automatically maps between these values based on speed
- 🎚️ Percentage-based controls for intuitive adjustment

### ⚡ Configurable Speed Range
- 🐌 Define minimum speed threshold (5 km/h or 3 mph)
- 🚀 Define maximum speed threshold (up to 100 km/h or 62 mph)
- 🎛️ Fine-tune sensitivity to match your driving style
- 🌍 Support for multiple units: **km/h** and **mph**

### 🎵 Smart Volume Mapping
- 📈 Linear mapping between speed and volume
- ⬇️ At minimum speed → minimum volume
- ⬆️ At maximum speed → maximum volume
- 🌊 Smooth transitions between values
- 📊 Real-time speedometer display with gauge visualization

## 🛠️ How It Works

1. **🎬 Enable the Service**: Toggle the service switch to start monitoring
2. **👤 Select or Create a Profile**: Choose an existing driving profile or create a new one with custom settings
3. **🎚️ Configure Volume Range**: Set your preferred minimum and maximum media volume levels (in %)
4. **🏎️ Set Speed Range**: Define the speed thresholds that should trigger volume changes
5. **🚗 Drive**: The app automatically adjusts volume as your speed changes, based on the selected profile
6. **📊 Monitor**: Watch the real-time speedometer gauge to see your current speed and enjoy your ride 

### 🧮 Volume Calculation

```
normalized_speed = (current_speed - min_speed) / (max_speed - min_speed)
target_volume = min_volume + (volume_range × normalized_speed)
```

## 🔐 Permissions

- 📍 **ACCESS_FINE_LOCATION**: To obtain accurate speed data via GPS
- 🔧 **FOREGROUND_SERVICE**: Required to run the monitoring service in background
- 🔔 **POST_NOTIFICATIONS**: For displaying service status notifications (Android 13+)
- 🔊 **MODIFY_AUDIO_SETTINGS**: To control media volume
- 📱 **Accelerometer**: Hardware sensor for detecting device movement
- 🔋 **REQUEST_IGNORE_BATTERY_OPTIMIZATIONS**: To ensure uninterrupted background operation

## 🔧 Technical Details

- 📱 **Min SDK**: Android 7.0 (API 24)
- 🎯 **Target SDK**: Android 16 (API 36)
- 📦 **Version**: 1.0.2 (versionCode 3)
- 🏗️ **Architecture**: MVVM with Clean Architecture
- 💉 **Dependency Injection**: Hilt
- 🎨 **UI Framework**: Jetpack Compose with Material 3
- 💾 **Data Storage**: Room Database & DataStore Preferences
- ⚙️ **Background Processing**: Foreground Service with Coroutines
- 🎭 **State Management**: StateFlow & ViewModel
- 🧭 **Navigation**: Navigation Compose with Kotlin Serialization
- 🧩 **Quick Settings Tile**: Toggle service from notification panel
- 📍 **Location**: Google Play Services Location API

## ⚠️ Safety Notice

⚠️ **Important**: This app is designed to enhance your driving experience by automatically adjusting volume. However:
- 🛡️ Always prioritize safe driving
- 🚑 Ensure you can hear emergency vehicles and traffic sounds
- ⚖️ Comply with local laws regarding audio device usage while driving
- 🧪 Test settings in a safe environment before using while driving
- 👂 Never set volume levels that impair your awareness of surroundings

## 🏗️ Building the Project

1. 📥 Clone the repository
   ```bash
   git clone https://github.com/yourusername/TuneFlow.git
   ```
2. 🚀 Open in Android Studio
3. 🔄 Sync Gradle dependencies
4. ▶️ Build and run on your device

```bash
./gradlew assembleDebug
```

Or simply click the ▶️ **Run** button in Android Studio!

## 📦 Dependencies

- 🎨 **Jetpack Compose** - Modern declarative UI toolkit
- 💾 **Room Database** - Local data persistence for profiles
- ⚡ **Kotlin Coroutines** - Asynchronous operations
- 💉 **Dagger Hilt** - Dependency injection
- 🏗️ **ViewModel & StateFlow** - State management
- 🎯 **Navigation Compose** - Seamless navigation with type-safe arguments
- 🔔 **Notification API** - Service status updates
- 📡 **Google Play Services Location** - GPS speed tracking
- 📦 **Kotlin Serialization** - Type-safe navigation arguments

## 🎯 Key Highlights

- ✅ Clean Architecture with MVVM pattern
- ✅ Reactive UI with Jetpack Compose
- ✅ Real-time speedometer gauge visualization
- ✅ Multiple profiles for different driving scenarios
- ✅ Expandable FAB with scroll-aware behavior
- ✅ Quick Settings Tile for one-tap toggle
- ✅ Percentage-based volume controls
- ✅ Multiple speed unit support (km/h, mph)
- ✅ Smooth volume transitions
- ✅ Low battery consumption
- ✅ Material Design 3 with dynamic theming

## 📸 Screenshots

*Coming soon...*

## 🤝 Contributing

Contributions are welcome! Feel free to:
- 🐛 Report bugs
- 💡 Suggest new features
- 🔧 Submit pull requests

---

**Made with ❤️ for safer and more enjoyable drives**

🎵 *Drive safe, listen well!* 🚗💨

## Future Enhancements

- [x] Multiple profiles for different driving scenarios
- [x] Quick Settings Tile for one-tap service toggle
- [ ] Support for Cancellation of service via notification
- [ ] Support for different volume curves (logarithmic, exponential)
- [ ] Home screen widget for quick service toggle
- [ ] Statistics and driving analytics

### 🔖 License

```
MIT License

Copyright (c) 2025 Pavitra Raut

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.


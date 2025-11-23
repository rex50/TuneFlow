# 🎵 TuneFlow - Smart Driving Volume Control

> 🚗💨 Your music, perfectly tuned to your speed!

TuneFlow is an intelligent Android application that automatically adjusts your media volume based on device acceleration while driving. Perfect for maintaining optimal music volume as driving conditions change – because your favorite song should sound great whether you're cruising or accelerating! 🎶

## ✨ Features

### 📡 Background Acceleration Monitoring
- 🔄 Runs as a foreground service to continuously monitor device acceleration
- 📱 Uses the accelerometer sensor to detect movement changes
- 🎯 Applies smoothing algorithms to reduce jitter and provide stable volume adjustments
- 🔋 Low battery impact with optimized sensor polling

### 🔊 Customizable Volume Range
- 🔉 Set minimum volume level (20% - 100%)
- 🔊 Set maximum volume level (20% - 100%)
- 📊 Volume automatically maps between these values based on acceleration
- 🎚️ Percentage-based controls for intuitive adjustment

### ⚡ Configurable Speed Range
- 🐌 Define minimum speed threshold (5 km/h)
- 🚀 Define maximum speed threshold (up to 100 km/h)
- 🎛️ Fine-tune sensitivity to match your driving style
- 🌍 Support for multiple units: **km/h**, **mph**, and **m/s²**

### 🎵 Smart Volume Mapping
- 📈 Linear mapping between acceleration and volume
- ⬇️ At minimum acceleration → minimum volume
- ⬆️ At maximum acceleration → maximum volume
- 🌊 Smooth transitions between values
- 📊 Real-time speedometer display with gauge visualization

## 🛠️ How It Works

1. **🎬 Enable the Service**: Toggle the service switch to start monitoring
2. **🎚️ Configure Volume Range**: Set your preferred minimum and maximum media volume levels (in %)
3. **🏎️ Set Speed Range**: Define the speed thresholds that should trigger volume changes
4. **🚗 Drive**: The app automatically adjusts volume as your speed changes
5. **📊 Monitor**: Watch the real-time speedometer gauge to see your current speed

### 🧮 Volume Calculation

```
normalized_speed = (current_speed - min_speed) / (max_speed - min_speed)
target_volume = min_volume + (volume_range × normalized_speed)
```

## 🔐 Permissions

- 🔧 **FOREGROUND_SERVICE**: Required to run the monitoring service in background
- 🔔 **POST_NOTIFICATIONS**: For displaying service status notifications (Android 13+)
- 🔊 **MODIFY_AUDIO_SETTINGS**: To control media volume
- 📱 **Accelerometer**: Hardware sensor for detecting device movement

## 🔧 Technical Details

- 📱 **Min SDK**: Android 7.0 (API 24)
- 🎯 **Target SDK**: Android 15 (API 36)
- 🏗️ **Architecture**: MVVM with Clean Architecture
- 💉 **Dependency Injection**: Hilt
- 🎨 **UI Framework**: Jetpack Compose
- 💾 **Data Storage**: DataStore Preferences
- ⚙️ **Background Processing**: Foreground Service with Coroutines
- 🎭 **State Management**: StateFlow & ViewModel

## 💡 Usage Tips

### 🏙️ For City Driving
- 🐌 Min Speed: 5 km/h
- 🚙 Max Speed: 40 km/h
- 🔉 Min Volume: 20%
- 🔊 Max Volume: 50%

### 🛣️ For Highway Driving
- 🚗 Min Speed: 20 km/h
- 🏎️ Max Speed: 100 km/h
- 🔉 Min Volume: 30%
- 🔊 Max Volume: 70%

### 🌄 For Rough Roads
- 🛻 Min Speed: 10 km/h
- 🚜 Max Speed: 60 km/h
- 🔉 Min Volume: 25%
- 🔊 Max Volume: 65%

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
- 💾 **DataStore Preferences** - For settings persistence
- ⚡ **Kotlin Coroutines** - Asynchronous operations
- 💉 **Dagger Hilt** - Dependency injection
- 🏗️ **ViewModel & StateFlow** - State management
- 🎯 **Navigation Compose** - Seamless navigation
- 🔔 **Notification API** - Service status updates

## 🎯 Key Highlights

- ✅ Clean Architecture with MVVM pattern
- ✅ Reactive UI with Jetpack Compose
- ✅ Real-time speedometer gauge visualization
- ✅ Percentage-based volume controls
- ✅ Multiple speed unit support (km/h, mph, m/s²)
- ✅ Smooth volume transitions
- ✅ Low battery consumption
- ✅ Material Design 3

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
- Material 3 - Design system

## Future Enhancements

- [ ] Support for Cancellation of service via notification
- [ ] Support for different volume curves (logarithmic, exponential)
- [ ] Multiple profiles for different driving scenarios
- [ ] Widget for quick service toggle
- [ ] Statistics and driving analytics

### 🔖 License

```
MIT License

Copyright (c) 2023 Pavitra Raut

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


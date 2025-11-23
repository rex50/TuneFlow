# TuneFlow - Driving Volume Control App

TuneFlow is an Android application that automatically adjusts your media volume based on device acceleration while driving. Perfect for maintaining optimal music volume as driving conditions change.

## Features

### 1. Background Acceleration Monitoring
- Runs as a foreground service to continuously monitor device acceleration
- Uses the accelerometer sensor to detect movement changes
- Applies smoothing algorithms to reduce jitter and provide stable volume adjustments
- Low battery impact with optimized sensor polling

### 2. Customizable Volume Range
- Set minimum volume level (0-15)
- Set maximum volume level (0-15)
- Volume automatically maps between these values based on acceleration

### 3. Configurable Acceleration Range
- Define minimum acceleration threshold (0-5 m/s²)
- Define maximum acceleration threshold (5-20 m/s²)
- Fine-tune sensitivity to match your driving style

### 4. Acceleration to Volume Mapping
- Linear mapping between acceleration and volume
- At minimum acceleration → minimum volume
- At maximum acceleration → maximum volume
- Smooth transitions between values

## How It Works

1. **Enable the Service**: Toggle the service switch to start monitoring
2. **Configure Volume Range**: Set your preferred minimum and maximum media volume levels
3. **Set Acceleration Range**: Define the acceleration thresholds that should trigger volume changes
4. **Drive**: The app automatically adjusts volume as acceleration changes

### Volume Calculation

```
normalized_acceleration = (current_acceleration - min_acceleration) / (max_acceleration - min_acceleration)
target_volume = min_volume + (volume_range × normalized_acceleration)
```

## Permissions

- **FOREGROUND_SERVICE**: Required to run the monitoring service in background
- **POST_NOTIFICATIONS**: For displaying service status notifications (Android 13+)
- **MODIFY_AUDIO_SETTINGS**: To control media volume
- **Accelerometer**: Hardware sensor for detecting device movement

## Technical Details

- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 15 (API 36)
- **Architecture**: MVVM with Jetpack Compose
- **Data Storage**: DataStore Preferences
- **Foreground Service**: For reliable background operation

## Usage Tips

### For City Driving
- Min Acceleration: 0.5 m/s²
- Max Acceleration: 3.0 m/s²
- Min Volume: 8
- Max Volume: 12

### For Highway Driving
- Min Acceleration: 1.0 m/s²
- Max Acceleration: 8.0 m/s²
- Min Volume: 10
- Max Volume: 15

### For Rough Roads
- Min Acceleration: 2.0 m/s²
- Max Acceleration: 10.0 m/s²
- Min Volume: 8
- Max Volume: 14

## Safety Notice

⚠️ **Important**: This app is designed to enhance your driving experience by automatically adjusting volume. However:
- Always prioritize safe driving
- Ensure you can hear emergency vehicles and traffic sounds
- Comply with local laws regarding audio device usage while driving
- Test settings in a safe environment before using while driving

## Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Build and run on your device

```bash
./gradlew assembleDebug
```

## Dependencies

- Jetpack Compose - Modern UI toolkit
- DataStore Preferences - For settings persistence
- Kotlin Coroutines - Asynchronous operations
- Material 3 - Design system

## Future Enhancements

- [ ] Support for different volume curves (logarithmic, exponential)
- [ ] GPS speed-based volume control
- [ ] Multiple profiles for different driving scenarios
- [ ] Integration with music apps
- [ ] Widget for quick service toggle
- [ ] Statistics and driving analytics

## License

This project is open source. Feel free to modify and use as needed.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.


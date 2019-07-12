# step_counter

Flutter plugin. Pedometer in ObjC and Java.

## Usage
To use this plugin, add `step_counter` as a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).


## Getting Started
#### Android
[Enable Fitness API](https://developers.google.com/fit/android/get-started) and obtain an OAuth 2.0 client ID.

#### iOS
[Enable HealthKit](https://developer.apple.com/documentation/healthkit/setting_up_healthkit) and add NSHealthShareUsageDescription key to the Info.plist file.

## Sample Usage

```dart
readAll() {
    Future<String> stepCountToday = StepCounter.getStepsToday();
    Future<String> stepCountInIntervales = StepCounter.getStepsInIntervals(int startTimeMilliseconds, int endTimeMilliseconds, int timeInterval);
    Future<String> getStepsDuringTimePeriod = StepCounter.getStepsDuringTime(int startTimeMilliseconds, int endTimeMilliseconds);
}
```
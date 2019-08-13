# step_counter

Flutter plugin that acts as a Pedometer in ObjC and Java using HealthKit and the Google Fitness Store.

## Usage
To use this plugin, add `step_counter` as a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).

## Getting Started
#### Android
[Enable Fitness API](https://developers.google.com/fit/android/get-started) and obtain an OAuth 2.0 client ID.

#### iOS
[Enable HealthKit](https://developer.apple.com/documentation/healthkit/setting_up_healthkit) and add NSHealthShareUsageDescription key to the Info.plist file.

## Template

```dart
readAll() {
    Future<String> stepCountToday = StepCounter.getStepsToday();
    Future<String> stepCountInIntervales = StepCounter.getStepsInIntervals(int startTimeMilliseconds, int endTimeMilliseconds, int intervalQuantity, String intervalUnit);
    Future<String> getStepsDuringTimePeriod = StepCounter.getStepsDuringTime(int startTimeMilliseconds, int endTimeMilliseconds);
}
```

## Sample Usage

```dart
readAll() {
    String results = "";
    
    //Get today's date and set a date to the desired start date of the query.
    var now  = new DateTime.now();
    var past = now.subtract(new Duration(hours: 12));

    //Convert both dates to milliseconds since the "Unix epoch" 1970-01-01T00:00:00Z (UTC).
    int end   = now.millisecondsSinceEpoch;
    int start = past.millisecondsSinceEpoch;

    //Set the length and unit of intervals to be queried within the range of dates previously defined. Current
    //options are 'minutes', 'days', and 'hours'.
    int intervalLength = 20;
    String intervalUnit = 'minutes';

    //Query HealthKit (on iOS) or the Google Fitness Store (on Android) through StepCounter.

    //Get the total number of steps between the start and end date in intervals.
    //Returns key-value pairs of the start of the interval in milliseconds since the "Unix epoch" and the total
    //number of steps in that interval.
    Future<String> stepCount = StepCounter.getStepsInIntervals(start, end, intervalLength, intervalUnit);

    //Get the total number of steps between the start date and end date.
    //Returns an int.
    Future<int> stepCount = StepCounter.getStepsDuringTime(start, end);

    //Get the total number of steps since midnight today.
    //Returns an int.
    Future<int> stepCount = StepCounter.getStepsToday();
}
```
import 'dart:async';

import 'package:flutter/services.dart';

class StepCounter {
  static const MethodChannel _channel =
      const MethodChannel('step_counter');

  static Future<String> authenticateUser() async {
    final String status = await _channel.invokeMethod('authenticateUser');
    return status;
  }

  static Future<Map<int, int>> getStepsInIntervals(int startTime, int endTime, int intervalQuantity, String intervalUnit) async {
    final Map<int, int> stepsInIntervals = await _channel.invokeMethod('getStepsInIntervals', {'startTime' : startTime, 'endTime' : endTime, 'intervalQuantity' : intervalQuantity, 'intervalUnit' : intervalUnit });
    return stepsInIntervals;
  }

  static Future<Map<int, int>> getStepsDuringTime(int startTime, int endTime) async {
    final Map<int, int> stepsDuringTime = await _channel.invokeMethod('getStepsDuringTime', {'startTime' : startTime, 'endTime' : endTime});
    return stepsDuringTime;
  }

  static Future<Map<int, int>> getStepsToday() async {
    final Map<int, int> stepsToday = await _channel.invokeMethod('getStepsToday');
    return stepsToday;
  }
}

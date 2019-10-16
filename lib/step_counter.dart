import 'dart:async';

import 'package:flutter/services.dart';

class StepCounter {
  static const MethodChannel _channel =
      const MethodChannel('step_counter');

  static Future<String> authenticateUser() async {
    final String status = await _channel.invokeMethod('authenticateUser');
    return status;
  }

  static Future<String> getBackgroundPermission() async {
    final String status = await _channel.invokeMethod('askBackgroundPermission');
    return status;
  }

  static Future<Map<dynamic, dynamic>> getStepsInIntervals(int startTime, int endTime, int intervalQuantity, String intervalUnit) async {
    final Map<dynamic, dynamic> stepsInIntervals = await _channel.invokeMethod('getStepsInIntervals', {'startTime' : startTime, 'endTime' : endTime, 'intervalQuantity' : intervalQuantity, 'intervalUnit' : intervalUnit });
    return stepsInIntervals;
  }

  static Future<int> getStepsDuringTime(int startTime, int endTime) async {
    final int stepsDuringTime = await _channel.invokeMethod('getStepsDuringTime', {'startTime' : startTime, 'endTime' : endTime});
    return stepsDuringTime;
  }

  static Future<int> getStepsToday() async {
    final int stepsToday = await _channel.invokeMethod('getStepsToday');
    return stepsToday;
  }
}

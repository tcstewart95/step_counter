import 'dart:async';

import 'package:flutter/services.dart';

class StepCounter {
  static const MethodChannel _channel =
      const MethodChannel('step_counter');

  static Future<bool> authenticateUser() async {
    final bool status = await _channel.invokeMethod('authenticateUser');
    return status;
  }

  static Future<int> getStepsInIntervals() async {
    final int stepsInIntervals = await _channel.invokeMethod('getStepsInIntervals');
    return getStepsInIntervals();
  }

  static Future<int> getStepsDuringTime() async {
    final int stepsDuringTime = await _channel.invokeMethod('getStepsDuringTime');
    return stepsDuringTime;
  }

  static Future<int> getStepsToday() async {
    final int stepsToday = await _channel.invokeMethod('getStepsToday');
    return stepsToday;
  }
}

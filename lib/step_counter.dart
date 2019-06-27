import 'dart:async';

import 'package:flutter/services.dart';

class StepCounter {
  static const MethodChannel _channel =
      const MethodChannel('step_counter');

  static Future<String> authenticateUser(String token) async {
    final String status = await _channel.invokeMethod('authUserWithToken', token);
    return status;
  }

  static Future<String> authenticateUserNoToken() async {
    final String status = await _channel.invokeMethod('authUserNoToken');
    return status;
  }
}

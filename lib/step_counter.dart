import 'dart:async';

import 'package:flutter/services.dart';

class StepCounter {
  static const MethodChannel _channel =
      const MethodChannel('step_counter');

  static Future<String> authenticateUser() async {
    final String status = await _channel.invokeMethod('authenticateUser');
    return status;
  }
}

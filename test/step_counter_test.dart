import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:step_counter/step_counter.dart';

void main() {
  const MethodChannel channel = MethodChannel('step_counter');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });
}

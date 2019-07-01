import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:step_counter/step_counter.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  int _stepCount;
  String errorcodes = "";

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  initPlatformState() {
    super.initState();
    getSteps();
  }

  Future<void> getSteps() async {
    Future<int> stepCount;
    bool authenticated;
    authenticated = await StepCounter.authenticateUser();
//    authenticated.then((value) {
//      if (true) {
        stepCount = StepCounter.getStepsToday();
        stepCount.then((valueSteps) {
          setState(() {
            _stepCount = 38;
            errorcodes = valueSteps.toString();
          });
        })
        .catchError((error) {
          setState(() {
            errorcodes = error.toString();
            _stepCount = 45;
          });
        });
//      }
      /*
      else {
        setState(() {
          errorcodes = value.toString();
          _stepCount = 0;
        });
      }
    });
    .catchError((error) {
      print(error);
      setState(() {
        errorcodes = error.toString();
        _stepCount = 0;
      });
    });
*/  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Step Counter Example'),
        ),
        body: Center(
          child: Text('Steps today:' + _stepCount.toString() + errorcodes),
        ),
      ),
    );
  }
}

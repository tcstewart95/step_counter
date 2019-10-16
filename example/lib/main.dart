import 'package:flutter/material.dart';
import 'package:step_counter/step_counter.dart';


void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: AccessSteps(),
    );
  }
}

class AccessSteps extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _StepCounterState();
}

class _StepCounterState extends State<AccessSteps> {
  String _results = '';

  @override
  void initState() {
    super.initState();
    readSteps();
  }

  readSteps() async {
    String results = "";
    String authenticationStatus = await StepCounter.authenticateUser();
    String backgroundPermissionStatus = await StepCounter.getBackgroundPermission();
    Future<int> stepCount = StepCounter.getStepsToday();
    stepCount.then((value) {
      results = value.toString();
      setState(() {
        _results = results;
      });
    }).catchError((error) {
      results = 'Failed to read all values. $error';
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Add Caravan'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'Total Steps for Today: $_results'
            ),
            FlatButton(
              onPressed: () => readSteps(),
              child: Text("Refresh"),
            ),
          ],
        ),
      )
    );
  }
}
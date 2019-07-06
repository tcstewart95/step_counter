#import "StepCounterPlugin.h"

@implementation StepCounterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"step_counter"
            binaryMessenger:[registrar messenger]];
  StepCounterPlugin* instance = [[StepCounterPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"authenticateUser" isEqualToString:call.method]) {
    result(authUser());
  }
  if ([@"getStepsInIntervals" isEqualToString:call.method]) 
  {
    result(getStepsInIntervals());
  }
  else if ([@"getStepsDuringTime" isEqualToString:call.method])
  {
    result(getStepsDuringTime());
  }
  else if ([@"getStepsToday" isEqualToString:call.method])
  {
    result(getStepsToday());
  }
  else
  {
    result(FlutterMethodNotImplemented);
  }
}

- (String)authUser:() {
  
}

- (String)getStepsInIntervals:(int*)startTime :(int*)endTime :(int*)intervals {

}

- (String)getStepsDuringTime:(int*)startTime :(int*)endTime {

}

- (String)getStepsToday:() {

}
@end

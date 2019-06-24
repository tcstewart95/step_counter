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
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"authUserWithToken" isEqualToString:call.method]) {

  } else if ([@"authUserNoToken" isEqualToString:call.method]) {

  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end

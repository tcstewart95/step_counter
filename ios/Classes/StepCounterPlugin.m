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
    result([self authUser]);
  }
  if ([@"getStepsInIntervals" isEqualToString:call.method]) 
  {
    result([self getStepsInIntervals :(int) call.arguments[0] :(int) call.arguments[1] :(int) call.arguments[2]]);
  }
  else if ([@"getStepsDuringTime" isEqualToString:call.method])
  {
    result([self getStepsDuringTime :(int) call.arguments[0] :(int) call.arguments[1]]);
  }
  else if ([@"getStepsToday" isEqualToString:call.method])
  {
    result([self getStepsToday]);
  }
  else
  {
    result(FlutterMethodNotImplemented);
  }
}

- (NSString *) authUser {
  return @"authenticated";
}


- (NSString *)getStepsInIntervals :(int)startTime :(int)endTime :(int)intervals {
  //[NSDate dateWithTimeIntervalSince1970:(endTime / 1000.0)];
  NSDate *start = [NSDate dateWithTimeIntervalSince1970:(startTime / 1000.0)];
  NSDate *end = [NSDate dateWithTimeIntervalSince1970:(endTime / 1000.0)];
  return [self executeQuery :start :end];
}


- (NSString *)getStepsDuringTime :(int)startTime :(int)endTime {
  NSDate *start = [NSDate dateWithTimeIntervalSince1970:(startTime / 1000.0)];
  NSDate *end = [NSDate dateWithTimeIntervalSince1970:(endTime / 1000.0)];
  return [self executeQuery :start :end];
}


- (NSString *)getStepsToday {
  NSDate *const date = NSDate.date;
  NSCalendar *const calendar = NSCalendar.currentCalendar;
  NSCalendarUnit const preservedComponents = (NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay);
  NSDateComponents *const components = [calendar components:preservedComponents fromDate:date];
  NSDate *const normalizedDate = [calendar dateFromComponents:components];
  return [self executeQuery :NSDate.date :normalizedDate];
}



- (NSString *)executeQuery :(NSDate*)sTime :(NSDate*)eTime {
  NSCalendar *calendar = [NSCalendar currentCalendar];
  NSDateComponents *interval = [[NSDateComponents alloc] init];
  interval.day = 1;

  NSDateComponents *anchorComponents = [calendar components:NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear
    fromDate:[NSDate date]];
  anchorComponents.hour = 0;
  NSDate *anchorDate = [calendar dateFromComponents:anchorComponents];
  HKQuantityType *quantityType = [HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierStepCount];

  // Create the query
  HKStatisticsCollectionQuery *query = [[HKStatisticsCollectionQuery alloc] initWithQuantityType:quantityType
    quantitySamplePredicate:nil
    options:HKStatisticsOptionCumulativeSum
    anchorDate:anchorDate
    intervalComponents:interval];

  // Set the results handler
  __block double value = 0;
  query.initialResultsHandler = ^(HKStatisticsCollectionQuery *query, HKStatisticsCollection *results, NSError *error) {

    // Plot the daily step counts over the past 7 days
    [results enumerateStatisticsFromDate:sTime
      toDate:eTime
      withBlock:^(HKStatistics *result, BOOL *stop) {
        HKQuantity *quantity = result.sumQuantity;
        if (quantity) {
            NSDate *date = result.startDate;
            value = [quantity doubleValueForUnit:[HKUnit countUnit]];
        } else {
          NSLog(@"No Connection");
        }
    }];
  };

//[self.healthStore executeQuery:query];
  NSNumber *myDoubleNumber = [NSNumber numberWithDouble:value];
  NSString *returnValue = [myDoubleNumber stringValue];
  return returnValue;
}
@end

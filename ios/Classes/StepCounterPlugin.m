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

- (NSString)authUser:() {
  
}

- (NSString)getStepsInIntervals:(int)startTime :(int)endTime :(int)intervals {

}

- (NSString)getStepsDuringTime:(int)startTime :(int)endTime {

}

- (NSString)getStepsToday:() {

}

- (NSString)executeQuery:(NSDate)startTime :(NSDate)endTime {
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
  query.initialResultsHandler = ^(HKStatisticsCollectionQuery *query, HKStatisticsCollection *results, NSError *error) {
    if (error) {
        // Perform proper error handling here
        NSLog(@"*** An error occurred while calculating the statistics: %@ ***",error.localizedDescription);
    }

    // Plot the daily step counts over the past 7 days
    [results enumerateStatisticsFromDate:startDate
      toDate:endDate
      withBlock:^(HKStatistics *result, BOOL *stop) {
        HKQuantity *quantity = result.sumQuantity;
        if (quantity) {
            NSDate *date = result.startDate;
            double value = [quantity doubleValueForUnit:[HKUnit countUnit]];
            NSLog(@"%@: %f", date, value);
        }
    }];
  };

  [self.healthStore executeQuery:query];

  return @"stuff";
}
@end

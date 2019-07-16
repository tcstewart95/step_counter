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
    [self getStepsInIntervals :(int) call.arguments[0] :(int) call.arguments[1] :(int) call.arguments[2] :result];
  }
  else if ([@"getStepsDuringTime" isEqualToString:call.method])
  {
    [self getStepsDuringTime :(int) call.arguments[0] :(int) call.arguments[1] :result];
  }
  else if ([@"getStepsToday" isEqualToString:call.method])
  {
    [self getStepsToday :result];
  }
  else
  {
    result(FlutterMethodNotImplemented);
  }
}

- (NSString *) authUser {
  return @"authenticated";
}


- (void)getStepsInIntervals :(int)startTime :(int)endTime :(int)intervals :(FlutterResult)result{
  NSDate *start = [NSDate dateWithTimeIntervalSince1970:(startTime / 1000.0)];
  NSDate *end = [NSDate dateWithTimeIntervalSince1970:(endTime / 1000.0)];
  [self executeQuery :start :end :result];
}


- (void)getStepsDuringTime :(int)startTime :(int)endTime :(FlutterResult)result{
  NSDate *start = [NSDate dateWithTimeIntervalSince1970:(startTime / 1000.0)];
  NSDate *end = [NSDate dateWithTimeIntervalSince1970:(endTime / 1000.0)];
  [self executeQuery :start :end :result];
}


- (void)getStepsToday :(FlutterResult)result{
  NSDate *const date = NSDate.date;
  NSCalendar *const calendar = NSCalendar.currentCalendar;
  [calendar setTimeZone:[NSTimeZone timeZoneWithName:@"MST"]];
  NSCalendarUnit const preservedComponents = (NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay | NSCalendarUnitHour | NSCalendarUnitMinute | NSCalendarUnitSecond);
  NSDateComponents *components = [calendar components:preservedComponents fromDate:date];
  NSDate *const normalizedDate = [calendar dateFromComponents:components];
    
    
  NSCalendarUnit const preservedComponentsMidnight = (NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay | NSCalendarUnitHour | NSCalendarUnitMinute | NSCalendarUnitSecond);
  NSDateComponents *componentsMidnight = [calendar components:preservedComponentsMidnight fromDate:date];
  componentsMidnight.hour = 0;
  componentsMidnight.minute = 0;
  componentsMidnight.second = 0;
  NSDate *const midnightDate = [calendar dateFromComponents:componentsMidnight];
  [self executeQuery :midnightDate :normalizedDate :result];
}


- (void)executeQuery :(NSDate*)sTime :(NSDate*)eTime :(FlutterResult)resultHandler{
    HKHealthStore *healthStore = [[HKHealthStore alloc] init];
    __block double stepsCount = 0.0;
    
    HKQuantityType *quantityType = [HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierStepCount];
    
    NSSet *stepsType =[NSSet setWithObject:[HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierStepCount]];
    
    
    [healthStore requestAuthorizationToShareTypes:nil readTypes:stepsType completion:^(BOOL success, NSError * _Nullable error) {
        if (success) {
            
            NSCalendar *calendar = [NSCalendar currentCalendar];
            NSDateComponents *anchorComponents = [calendar components:NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear fromDate:[NSDate date]];
            anchorComponents.hour = 0;
            NSDate *anchorDate = [calendar dateFromComponents:anchorComponents];
            
            NSDateComponents *interval = [[NSDateComponents alloc] init];
            interval.day = 1;
            HKStatisticsCollectionQuery *query = [[HKStatisticsCollectionQuery alloc]   initWithQuantityType:quantityType
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
                [results enumerateStatisticsFromDate:sTime
                                              toDate:eTime
               withBlock:^(HKStatistics *result, BOOL *stop) {
                   HKQuantity *quantity = result.sumQuantity;
                   if (quantity) {
                       stepsCount = [quantity doubleValueForUnit:[HKUnit countUnit]];
                       NSNumber *myDoubleNumber = [NSNumber numberWithDouble:stepsCount];
                       NSString *returnValue = [myDoubleNumber stringValue];
                       [self returnResult:returnValue :resultHandler];
                   }
                   
               }];
            };
            [healthStore executeQuery:query];
        }
    }];
}

-(void)returnResult :(NSString *)stepCount :(FlutterResult)result{
    result(stepCount);
}

@end

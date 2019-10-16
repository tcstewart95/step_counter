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
  else if ([@"askBackgroundPermission"] isEqualToString:call.method]) {
    result("Already Granted on iOS with Authentication");
  }
  else if ([@"getStepsInIntervals" isEqualToString:call.method]) 
  {
    [self getStepsInIntervals :[call.arguments[@"startTime"] longValue] :[call.arguments[@"endTime"] longValue] :[call.arguments[@"intervalQuantity"] intValue] :call.arguments[@"intervalUnit"] :result];
  }
  else if ([@"getStepsDuringTime" isEqualToString:call.method])
  {
      [self getStepsDuringTime :[call.arguments[@"startTime"] longValue] :[call.arguments[@"endTime"] longValue] :result];
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


- (void)getStepsInIntervals :(long)startTime :(long)endTime :(int)intervalQuantity :(NSString*)intervalUnit :(FlutterResult)result{
  NSDate *start = [NSDate dateWithTimeIntervalSince1970:(startTime / 1000)];
  NSDate *end = [NSDate dateWithTimeIntervalSince1970:(endTime / 1000)];
  [self executeQuery :start :end :intervalQuantity :intervalUnit :result :0];
}


- (void)getStepsDuringTime :(long)startTime :(long)endTime :(FlutterResult)result{
  NSDate *start = [NSDate dateWithTimeIntervalSince1970:(startTime / 1000.0)];
  NSDate *end = [NSDate dateWithTimeIntervalSince1970:(endTime / 1000.0)];
  int intervalQuantity = 1;
  NSString *intervalUnit = @"minute";
  [self executeQuery :start :end :intervalQuantity :intervalUnit :result :1];
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
  int intervalQuantity = 1;
  NSString *intervalUnit = @"day";
    [self executeQuery :midnightDate :normalizedDate :intervalQuantity :intervalUnit :result :2];
}


- (void)executeQuery :(NSDate*)sTime :(NSDate*)eTime :(int)intervalQuantity :(NSString*)intervalUnit :(FlutterResult)resultHandler :(int)option {
    HKHealthStore *healthStore = [[HKHealthStore alloc] init];
    __block NSMutableDictionary *resultCollection =  [NSMutableDictionary new];
    
    HKQuantityType *quantityType = [HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierStepCount];
    
    NSSet *stepsType =[NSSet setWithObject:[HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierStepCount]];
    
    
    [healthStore requestAuthorizationToShareTypes:nil readTypes:stepsType completion:^(BOOL success, NSError * _Nullable error) {
        if (success) {
            
            NSCalendar *calendar = [NSCalendar currentCalendar];
            NSDateComponents *anchorComponents = [calendar components:NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear fromDate:[NSDate date]];
            anchorComponents.hour = 0;
            NSDate *anchorDate = [calendar dateFromComponents:anchorComponents];
            
            NSDateComponents *interval = [[NSDateComponents alloc] init];
            if ([intervalUnit isEqual:@"day"]) {
              interval.day = intervalQuantity;
            } else if ([intervalUnit isEqual:@"hour"]) {
              interval.hour = intervalQuantity;
            } else if ([intervalUnit isEqual:@"minute"]) {
              interval.minute = intervalQuantity;
            } else {
              resultHandler(@"Interval Unit Not Supported");
              return;
            }
            HKStatisticsCollectionQuery *query = [[HKStatisticsCollectionQuery alloc]   initWithQuantityType:quantityType
                quantitySamplePredicate:nil
                options:HKStatisticsOptionCumulativeSum
                anchorDate:anchorDate
                intervalComponents:interval];
            
            // Set the results handler
            query.initialResultsHandler = ^(HKStatisticsCollectionQuery *query, HKStatisticsCollection *results, NSError *error) {
                if (error) {
                    // Perform proper error handling here
                    resultHandler(@"Access Denied, Add Permissions In HealthKit");
                }
                [results enumerateStatisticsFromDate:sTime
                                              toDate:eTime
                withBlock:^(HKStatistics *result, BOOL *stop) {
                   HKQuantity *quantity = result.sumQuantity;
                   double returnValue = 0;
                   if (quantity) {
                       returnValue = [quantity doubleValueForUnit:[HKUnit countUnit]];
                   }
                   NSTimeInterval seconds = [result.startDate timeIntervalSince1970];
                   NSInteger milliseconds = seconds*1000;
                   resultCollection[@(milliseconds)] = [NSNumber numberWithDouble:returnValue];
                   if ([result.endDate compare: eTime] == NSOrderedDescending || [result.endDate compare: eTime] == NSOrderedSame) {
                       [self returnResult :resultCollection :resultHandler :option];
                   }
               }];
            };
            [healthStore executeQuery:query];
        }
    }];
}

-(void)returnResult :(NSMutableDictionary*) resultCollection :(FlutterResult)result :(int)option {
    long total = 0;
    switch (option) {
        case 0:
            result(resultCollection);
            break;
        case 1:
            for (id key in resultCollection) {
                total += [[resultCollection objectForKey:key] longValue];
            }
            result([NSNumber numberWithLong:total]);
            break;
        case 2:
            for (id key in resultCollection) {
                total += [[resultCollection objectForKey:key] longValue];
            }
            result([NSNumber numberWithLong:total]);
            break;
        default:
            break;
    }
}

@end

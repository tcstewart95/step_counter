package senda.step_counter;

import android.app.Application;
import android.os.Bundle;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.GoogleApiClient;
import io.flutter.plugin.common.MethodChannel.Result;

import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class Pedometer extends Application {

    private GoogleApiClient mClient = null;
    private Integer GET_INTERVALS_OPTION = 0;
    private Integer GET_RECENT_OPTION = 1;
    private Integer GET_TODAY_OPTION = 1;

    protected void getStepsInIntervals(Result result, long startTime, long endTime, int intervalQuantity, String intervalUnit, Context context) {
        TimeUnit _timeUnit; 
        switch(intervalUnit) {
            case "days":
                _timeUnit = TimeUnit.DAYS;
                break;
            case "hours":
                _timeUnit = TimeUnit.HOURS;
                break;
            case "minutes":
                _timeUnit = TimeUnit.MINUTES;
                break;
            default:
                result.success(null);
                return;
        }
        
        final DataReadRequest req = new DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(intervalQuantity, _timeUnit)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();

        readGoogleResults(result, context, req, GET_INTERVALS_OPTION);
    }



    protected void getStepsDuringTime(Result result, long startTime, long endTime, Context context) {
        final DataReadRequest req = new DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();

        readGoogleResults(result, context, req, GET_RECENT_OPTION);
    }


    protected void getStepsToday(Result result, Context context) {
        long millis;
        long millisMidnight;
        try {
            Date today = Calendar.getInstance().getTime();
            millis = today.getTime();
        } catch (Exception e) {
            result.success(null);
            return;
        }

        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND,0);
            Date todayMidnight = cal.getTime();
            millisMidnight = todayMidnight.getTime();
        } catch (Exception e) {
            result.success(null);
            return;
        }
      
        final DataReadRequest req = new DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(millisMidnight, millis, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();

        readGoogleResults(result, context, req, GET_TODAY_OPTION);
    }

    private void readGoogleResults(final Result result, final Context context, final DataReadRequest request, final Integer option) {
        mClient = new GoogleApiClient.Builder(context)
            .addApi(Fitness.HISTORY_API)
            .addApi(Fitness.CONFIG_API)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                public void onConnected(Bundle bundle) {
                    //Async call to Google API
                    List<Object> list = new ArrayList<>();
                    try {
                        list.add(mClient);
                        list.add(request);
                        new requestHistory(new requestHistory.AsyncResponse() {
                            @Override
                            public void processFinish(DataReadResult output) {
                                //If there is any error retrieving steps, catch it
                                if (output.equals("java.lang.IndexOutOfBoundsException: Index: 0, Size: 0")) {
                                    result.success("99999");
                                    return;
                                //If the output is not null, proceed
                                } else if (!output.getBuckets().isEmpty()){
                                    returnResults(result, output, option);
                                }
                            }
                        }).execute(list);
                    } catch (Exception e) {
                        //result.success(null);
                        return;
                    }
                }
                public void onConnectionSuspended(int i) {
                    //result.success(null);
                    return;
                }
            }).build();
        mClient.connect();
    }

    private void returnResults(Result result, DataReadResult input, Integer option) {
        //Parse the results, returning a map
        Map<Long, Long> stepsIntervals = new HashMap<>();
        for (Bucket bucket : input.getBuckets()) {
            List<DataSet> dataSets = bucket.getDataSets();
            for (DataSet dataSet : dataSets) {
                for (int i = 0; i < dataSet.getDataPoints().size(); i++) {
                    //Add Data Points to the map
                    stepsIntervals.put(dataSet.getDataPoints().get(i).getStartTime(TimeUnit.MILLISECONDS), Long.valueOf(dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt()));
                }
            }
        }
        //Sort and Send the completed map up the channel
        TreeMap<Long, Long> sortedStepIntervals = new TreeMap<Long, Long>(stepsIntervals);
        switch (option) {
            case 0:
                result.success(sortedStepIntervals);
                break;
            case 1:
                Long steps = Long.valueOf(0);
                for(Map.Entry<Long, Long> entry : sortedStepIntervals.entrySet()) {
                    steps += entry.getValue();
                }
                result.success(steps);
                break;
            default:
                result.success(null);
        }
    }
}

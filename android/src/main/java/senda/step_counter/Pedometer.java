package senda.step_counter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.net.URL;


public class Pedometer{


    public static String steps = "";
    private static GoogleApiClient mClient = null;

    protected String getStepsInIntervals(long startTime, long endTime, int intervalQuantity, String intervalUnit,  Context context) {
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
                return "Interval unit not supported";
        }
        
        final DataReadRequest req = new DataReadRequest.Builder()
            .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(intervalQuantity, _timeUnit)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();

        readGoogleResults(context, req);

        return steps;
    }



    protected String getStepsDuringTime(long startTime, long endTime, Context context) {
        final DataReadRequest req = new DataReadRequest.Builder()
            .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();
            
        readGoogleResults(context, req);

        return steps;
    }



    protected String getStepsToday(Context context) {
        long millis = 0;
        long millisMidnight = 0;
        try {
            Date today = Calendar.getInstance().getTime();
            millis = today.getTime();
        } catch (Exception e) {
            return e.toString();
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
            return e.toString();
        }
      
        final DataReadRequest req = new DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(millisMidnight, millis, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();

        readGoogleResults(context, req);

        return steps;
    }



    private void readGoogleResults(Context context, final DataReadRequest request) {
        mClient = new GoogleApiClient.Builder(context)
            .addApi(Fitness.HISTORY_API)
            .addApi(Fitness.CONFIG_API)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                public void onConnected(Bundle bundle) {
                    //Async call to DB
                    List<Object> list = new ArrayList<Object>();
                    try {
                        list.add(mClient);
                        list.add(request);
                        new requestHistory(new requestHistory.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                if (output != null) {
                                    steps = output;
                                } else {
                                    steps = "No Pedometer Connected";
                                }
                            }
                        }).execute(list);
                    } catch (Exception e) {
                        steps = "Unable to Connect";
                    }
                }

                public void onConnectionSuspended(int i) {
                    steps = "Network Failure";
                }
            }).build();
        mClient.connect();
    }
}

package senda.step_counter;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
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
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.net.URL;


public class Pedometer{


    private static GoogleApiClient mClient = null;
    StepResults stepResults;

    protected Map<Integer, Integer> getStepsInIntervals(long startTime, long endTime, int intervalQuantity, String intervalUnit, Context context) {
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
                return null;
        }
        
        final DataReadRequest req = new DataReadRequest.Builder()
            .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(intervalQuantity, _timeUnit)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();

        readGoogleResults(context, req);

        return stepResults.getData();
    }



    protected String getStepsDuringTime(long startTime, long endTime, Context context) {
        final DataReadRequest req = new DataReadRequest.Builder()
            .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();

        readGoogleResults(context, req);

        return stepResults.getSteps();
    }



    protected String getStepsToday(Context context) {
        long millis = 0;
        long millisMidnight = 0;
        try {
            Date today = Calendar.getInstance().getTime();
            millis = today.getTime();
        } catch (Exception e) {
            return "0";
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
            return "0";
        }
      
        final DataReadRequest req = new DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(millisMidnight, millis, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build();

        readGoogleResults(context, req);

        return stepResults.getSteps();
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
                                if (output.equals("java.lang.IndexOutOfBoundsException: Index: 0, Size: 0")) {
                                    stepResults = new StepResults("0");
                                } else {
                                    stepResults = new StepResults(output);
                                }
                            }
                        }).execute(list);
                    } catch (Exception e) {
                        stepResults = new StepResults("0");
                    }
                }
                public void onConnectionSuspended(int i) {
                    stepResults = new StepResults("0");
                }
            }).build();
        mClient.connect();
    }
}

class StepResults {
    private String steps = null;
    private List<DataSet> results = null;
    private Map<Integer, Integer> data = null;
    private int start = 0;
    private int end = 0;

    StepResults(String newSteps) {
        steps = newSteps;
    }

    StepResults(List<DataSet> newData) {
        results = newData;
    }

    void setSteps(String newSteps) {
        steps = newSteps;
    }

    void setData(Map<Integer, Integer> newData) {
        data.putAll(newData);
    }

    String getSteps() {
        return steps;
    }

    Map<Integer,Integer> getData() {

        Iterator iterator = results.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            Iterator iterator1 = results.get(i).getDataPoints().listIterator();
            int j = 0;
            while (iterator1.hasNext()) {
                data.put(1000, results.get(i).getDataPoints().get(j).getValue(Field.FIELD_STEPS).asInt());
                j++;
            }
            i++;
        }
        return data;
    }
}

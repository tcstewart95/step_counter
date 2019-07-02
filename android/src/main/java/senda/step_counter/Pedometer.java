package senda.step_counter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
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
import java.util.concurrent.TimeUnit;


public class Pedometer {



    static String steps = "";
    private DataSource ds;


    public Pedometer () {
        this.ds = new DataSource.Builder()
            .setAppPackageName("senda.step_counter")
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .build();
    } 


    protected String getStepsInIntervals(long startTime, long endTime, int intervals, Context context) {
      
        final DataReadRequest req = new DataReadRequest.Builder()
        .aggregate(this.ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(intervals, TimeUnit.MINUTES)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build();

        readGoogleResults(context, req);

        return steps;
    }



    protected String getStepsDuringTime(long startTime, long endTime, Context context) {
      
        final DataReadRequest req = new DataReadRequest.Builder()
        .aggregate(this.ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
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
        .aggregate(this.ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(millisMidnight, millis, TimeUnit.MILLISECONDS)
            .build();

        readGoogleResults(context, req);

        return steps;
    }



    private void readGoogleResults(Context context, DataReadRequest request) {
        final Task<DataReadResponse> response = Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
        .readData(request)
        .addOnSuccessListener(
            new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    if (dataReadResponse.getBuckets().size() > 0) {
                        for (Bucket bucket : dataReadResponse.getBuckets()) {
                            List<DataSet> dataSets = bucket.getDataSets();
                            for (DataSet dataSet : dataSets) {
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    steps += dp.toString();
                                }
                            }
                        }
                    } else if (dataReadResponse.getDataSets().size() > 0) {
                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            steps += dataSet.toString();
                        }
                    }
                }
            }
        )
        .addOnFailureListener(
            new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    steps = e.toString();
                }
            }
        );
    }
}

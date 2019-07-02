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

    String steps = "";

    protected int getStepsInIntervals(long startTime, long endTime, int intervals) {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return 10;
    }

    protected int getStepsDuringTime(long startTime, long endTime) {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return 50000;
    }

    protected String getStepsToday(Context context) {
        long millis = 0;
        long millisMidnight = 0;
        //final pedometer =;
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

        final DataSource ds = new DataSource.Builder()
        .setAppPackageName("senda.step_counter")
        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .setType(DataSource.TYPE_DERIVED)
        .setStreamName("estimated_steps")
        .build();
      
        final DataReadRequest req = new DataReadRequest.Builder()
        .aggregate(ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(millisMidnight, millis, TimeUnit.MILLISECONDS)
            .build();

        final Task<DataReadResponse> response = Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
        .readData(req)
        .addOnSuccessListener(
            new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    if (dataReadResponse.getBuckets().size() > 0) {
                        Log.i(
                            "TAG", "Number of returned buckets of DataSets is: " + dataReadResponse.getBuckets().size());
                        for (Bucket bucket : dataReadResponse.getBuckets()) {
                          List<DataSet> dataSets = bucket.getDataSets();
                          for (DataSet dataSet : dataSets) {
                                Log.i("TAG", dataSet.toString());
                                Pedometer.this.steps = dataSet.toString();
                            }
                        }
                    } else if (dataReadResponse.getDataSets().size() > 0) {
                        Log.i("TAG", "Number of returned DataSets is: " + dataReadResponse.getDataSets().size());
                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            Log.i("TAG", dataSet.toString());
                            Pedometer.this.steps = dataSet.toString();
                        }
                    }
                }
            }
        )
        .addOnFailureListener(
            new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Pedometer.this.steps = e.toString();
                }
            }
        );

        return steps;
    }

}

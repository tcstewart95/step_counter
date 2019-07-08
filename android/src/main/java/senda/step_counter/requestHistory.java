package senda.step_counter;

import android.os.Bundle;
import android.os.AsyncTask;

import com.google.android.gms.fitness.data.Bucket;
import java.util.concurrent.TimeUnit;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
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
import java.util.concurrent.TimeUnit;

public class requestHistory extends AsyncTask<List<Object>, Void, String> {
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public requestHistory(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    
    private String steps;


    protected String doInBackground(List<Object>... params) {
        long total = 0;
        try {
            Object mClientObj = params[0].get(0);
            GoogleApiClient mClient = (GoogleApiClient)mClientObj;

            Object requestObj = params[0].get(1);
            DataReadRequest request = (DataReadRequest)requestObj;
            DataReadResult totalResult = Fitness.HistoryApi.readData(mClient, request).await(1, TimeUnit.MINUTES);
            if (totalResult.getStatus().isSuccess()) {
                //Used for aggregated data
                if (totalResult.getBuckets().size() > 0) {
                    for (Bucket bucket : totalResult.getBuckets()) {
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            steps = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).toString();
                        }
                    }
                }            
                //Used for non-aggregated data
                else if (totalResult.getDataSets().size() > 0) {
                    for (DataSet dataSet : totalResult.getDataSets()) {
                        steps = dataSet.toString();
                    }
                }
                return steps;
            }
            else {
                return "Network Error";
            }
        } catch (Exception e) {
            steps = e.toString();
            return steps;
        }
    }

    protected void onPostExecute(String r) {
        delegate.processFinish(steps);
    }

}
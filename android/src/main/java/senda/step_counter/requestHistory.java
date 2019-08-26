package senda.step_counter;

import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.List;

import com.google.android.gms.fitness.request.DataReadRequest;

public class requestHistory extends AsyncTask<List<Object>, Void, DataReadResult> {
    public interface AsyncResponse {
        void processFinish(DataReadResult output);
    }

    public AsyncResponse delegate = null;

    public requestHistory(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    protected DataReadResult doInBackground(List<Object>... params) {
        try {
            Object mClientObj = params[0].get(0);
            GoogleApiClient mClient = (GoogleApiClient)mClientObj;
            Object requestObj = params[0].get(1);
            DataReadRequest request = (DataReadRequest)requestObj;
            DataReadResult totalResult = Fitness.HistoryApi.readData(mClient, request).await(1, TimeUnit.MINUTES);
            if (totalResult.getStatus().isSuccess()) {
                return totalResult;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    protected void onPostExecute(DataReadResult r) {
        delegate.processFinish(r);
    }

}
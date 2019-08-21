package senda.step_counter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Authenticator extends Pedometer{
    private Context context;
    private Activity activity;
    private String LOG_TAG = "authReport";
    private int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 80085;
    private int postAction;
    private int startTime;
    private int endTime;
    private int intervalQuantity;
    private String intervalUnit;

    public String stepCount = "still launching";
    public Map<Integer, Integer> stepCountIntervals;

    Authenticator(Context context, Activity activity, int startTime, int endTime, int intervalQuantity, String intervalUnit) {
        this.context = context;
        this.activity = activity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalQuantity = intervalQuantity;
        this.intervalUnit = intervalUnit;
    }

    Authenticator(Context context, Activity activity, int startTime, int endTime) {
        this.context = context;
        this.activity = activity;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    Authenticator(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    private void setPostAction(int action) {
        this.postAction = action;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                switch (this.postAction) {
                    case 1:
                        //stepCountIntervals.putAll(getStepsInIntervals(startTime, endTime, intervalQuantity, intervalUnit, context));
                        stepCountIntervals.putAll(getStepsInIntervals(startTime, endTime, intervalQuantity, intervalUnit, context));
                        break;
                    case 2:
                        stepCount = getStepsDuringTime(startTime, endTime, context);
                        break;
                    case 3:
                        stepCount = getStepsToday(context);
                        break;
                    default:
                        stepCount = "0";
                        break;
                }
            }
        } else {
            stepCount += "not authenticated";
        }
    }

    public void authenticate(int postAction) {

        setPostAction(postAction);
        
        FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(context),
                fitnessOptions);
        } else {
            stepCount = getStepsToday(context);
        }
    }
}
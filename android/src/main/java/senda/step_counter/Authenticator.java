package senda.step_counter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import io.flutter.plugin.common.MethodChannel.Result;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;


public class Authenticator extends Pedometer {
    private Activity activity;
    private Context context;
    private Result result;
    private int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 80085;
    private int postAction;
    private long startTime;
    private long endTime;
    private int intervalQuantity;
    private String intervalUnit;

    Authenticator(Result result, Activity activity, long startTime, long endTime, int intervalQuantity, String intervalUnit, Context context) {
        this.activity = activity;
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalQuantity = intervalQuantity;
        this.intervalUnit = intervalUnit;
        this.result = result;
    }

    Authenticator(Result result, Activity activity, long startTime, long endTime, Context context) {
        this.activity = activity;
        this.context = context;
        this.result = result;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    Authenticator(Result result, Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        this.result = result;
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
            postAuthenticate();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                postAuthenticate();
            }
        } else {
            result.success(null);
        }
    }

    private void setPostAction(int action) {
        this.postAction = action;
    }

    protected void postAuthenticate() {
        switch (this.postAction) {
            case 1:
                getStepsInIntervals(result, startTime, endTime, intervalQuantity, intervalUnit, context);
                break;
            case 2:
                getStepsDuringTime(result, startTime, endTime, context);
                break;
            case 3:
                getStepsToday(result, context);
                break;
            case 4:
                getBackgroundPermission(result, context);
                break;
            default:
                 result.success("8888888");
                break;
        }
    }

}
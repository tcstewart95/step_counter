package senda.step_counter;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** StepCounterPlugin */
public class StepCounterPlugin implements MethodCallHandler {

    private Activity activity;
    private Context context;

    private int ON_POST_DO_NOTHING = 0;
    private int ON_POST_GET_STEPS_IN_INTERVALS = 1;
    private int ON_POST_GET_STEPS_DURING_TIME = 2;
    private int ON_POST_GET_STEPS_TODAY = 3;

    /** Plugin registration. */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "step_counter");
        channel.setMethodCallHandler(new StepCounterPlugin(registrar.activity(), registrar.context()));
    }

    private StepCounterPlugin(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        if (call.method.equals("authenticateUser"))
        {
            authUser(result);
        }
        else if(call.method.equals("getStepsInIntervals"))
        {
            Number startTime = call.argument("startTime");
            Number endTime = call.argument("endTime");
            Number intervalQuantity = call.argument("intervalQuantity");
            getStepsInIntervals(result, startTime.longValue(), endTime.longValue(), intervalQuantity.intValue(), (String) call.argument("intervalUnit"));
        }
        else if(call.method.equals("getStepsDuringTime"))
        {
            Number startTime = call.argument("startTime");
            Number endTime = call.argument("endTime");
            getStepsDuringTime(result, startTime.longValue(), endTime.longValue());
        }
        else if(call.method.equals("getStepsToday"))
        {
            getStepsToday(result);
        }
        else
        {
            result.notImplemented();
        }
    }

    private void authUser(Result result) {
        Authenticator authy = new Authenticator(result, activity, context);
        authy.authenticate(ON_POST_DO_NOTHING);
    }

    private void getStepsInIntervals(Result result, long startTime, long endTime, int intervalQuantity, String intervalUnit) {
        Authenticator authy = new Authenticator(result, activity, startTime, endTime, intervalQuantity, intervalUnit, context);
        authy.authenticate(ON_POST_GET_STEPS_IN_INTERVALS);
    }

    private void getStepsDuringTime(Result result, long startTime, long endTime) {
        Authenticator authy = new Authenticator(result, activity, startTime, endTime, context);
        authy.authenticate(ON_POST_GET_STEPS_DURING_TIME);
    }

    private void getStepsToday(Result result) {
        Authenticator authy = new Authenticator(result, activity, context);
        authy.authenticate(ON_POST_GET_STEPS_TODAY);
    }
}

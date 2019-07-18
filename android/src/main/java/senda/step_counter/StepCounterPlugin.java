package senda.step_counter;

import android.app.Activity;
import android.content.Context;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** StepCounterPlugin */
public class StepCounterPlugin implements MethodCallHandler {

  Activity activity;
  Context context;

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
      result.success(authUser());
    }
    else if(call.method.equals("getStepsInIntervals"))
    {
      result.success(getStepsInIntervals((int) call.argument("startTime"), (int) call.argument("endTime"), (int) call.argument("intervalQuantity"), (String) call.argument("intervalUnit")));
    }
    else if(call.method.equals("getStepsDuringTime"))
    {
      result.success(getStepsDuringTime((int) call.argument("startTime"), (int) call.argument("endTime")));
    }
    else if(call.method.equals("getStepsToday"))
    {
      result.success(getStepsToday());
    }
    else
    {
      result.notImplemented();
    }
  }

  private String authUser() {
    Authenticator authy = new Authenticator(context, activity);
    authy.authenticate(ON_POST_DO_NOTHING);
    return authy.stepCount;
  }

  private String getStepsInIntervals(int startTime, int endTime, int intervalQuantity, String intervalUnit) {
    Authenticator authy = new Authenticator(context, activity, startTime, endTime, intervalQuantity, intervalUnit);
    authy.authenticate(ON_POST_GET_STEPS_IN_INTERVALS);
    return authy.stepCount;
  }

  private String getStepsDuringTime(int startTime, int endTime) {
    Authenticator authy = new Authenticator(context, activity, startTime, endTime);
    authy.authenticate(ON_POST_GET_STEPS_DURING_TIME);
    return authy.stepCount;
  }

  private String getStepsToday() {
    Authenticator authy = new Authenticator(context, activity);
    authy.authenticate(ON_POST_GET_STEPS_TODAY);
    return authy.stepCount;
  }
}

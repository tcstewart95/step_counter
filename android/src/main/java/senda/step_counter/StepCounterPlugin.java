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
      result.success(getStepsInIntervals());
    }
    else if(call.method.equals("getStepsDuringTime"))
    {
      result.success(getStepsDuringTime());
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

  private boolean authUser() {
    // Authenticator authenticator = new Authenticator(context, activity);
    // authenticator.Authenticate();
    // if(authenticator.isAuthenticated) {
    //   return true;
    // } else {
    //   return false;
    // }
    return false;
  }

  private int getStepsInIntervals() {
    //Pedometer pedometer = new Pedometer();
    //return pedometer.getStepsInIntervals(1,1,5);
    return 0;
  }

  private int getStepsDuringTime() {
    //Pedometer pedometer = new Pedometer();
    //return pedometer.getStepsDuringTime(1,1);
    return 0;
  }

  private String getStepsToday() {
    Authenticator authy = new Authenticator(context, activity);
    authy.authenticate();
    return authy.stepCount;
  }
}

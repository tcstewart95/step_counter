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
    if (call.method.equals("authenticateUser")) {
      result.success(authUser());
    } else {
      result.notImplemented();
    }
  }

  public String authUser() {
    boolean amIIn = false;
    Authenticator authy = new Authenticator("stuff", context, activity);
    amIIn = authy.Authenticate();
    return "no token given";
  }
}

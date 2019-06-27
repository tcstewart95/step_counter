package senda.step_counter;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** StepCounterPlugin */
public class StepCounterPlugin implements MethodCallHandler {
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "step_counter");
    channel.setMethodCallHandler(new StepCounterPlugin());
  }



  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("authUserWithToken")) {
      result.success(authUserWithToken((call.arguments()).toString()));
    } else if (call.method.equals("authUserNoToken")) {
      result.success(authUserNoToken());
    } else {
      result.notImplemented();
    }
  }


  public String authUserWithToken(String token) {
    return token;
  }



  public String authUserNoToken() {
    return "no token given";
  }
}

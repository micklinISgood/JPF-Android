package com.android.internal.policy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

/**
 * {@hide}
 */

public final class PolicyManager {
  private static final String TAG = "PhonePolicy";
  // private static final IPolicy sPolicy;

  // static {
  // // Pull in the actual implementation of the policy at run-time
  // try {
  // Class policyClass = Class.forName(POLICY_IMPL_CLASS_NAME);
  // sPolicy = (IPolicy)policyClass.newInstance();
  // } catch (ClassNotFoundException ex) {
  // throw new RuntimeException(
  // POLICY_IMPL_CLASS_NAME + " could not be loaded", ex);
  // } catch (InstantiationException ex) {
  // throw new RuntimeException(
  // POLICY_IMPL_CLASS_NAME + " could not be instantiated", ex);
  // } catch (IllegalAccessException ex) {
  // throw new RuntimeException(
  // POLICY_IMPL_CLASS_NAME + " could not be instantiated", ex);
  // }
  // }

  // Cannot instantiate this class
  private PolicyManager() {
  }

  // The static methods to spawn new policy-specific objects
  public static Window makeNewWindow(Context context) {
    return new Window(context);
  }

  //
  public static LayoutInflater makeNewLayoutInflater(Context context) {
    return new LayoutInflater(context);
  }

  //
  public static WindowManager makeNewWindowManager() {
    return new WindowManager();
  }
  //
  // public static FallbackEventHandler makeNewFallbackEventHandler(Context context) {
  // return sPolicy.makeNewFallbackEventHandler(context);
  // }
}
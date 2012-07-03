package android.app;

import android.content.Context;

import com.android.server.am.ActivityManagerService;

final class ActivityStack {
	static final boolean DEBUG_SWITCH = true;
	static final String TAG = "ActivityStack";

	// How long between activity launches that we consider safe to not warn
	// the user about an unexpected activity being launched on top.
	static final long START_WARN_TIME = 5 * 1000;

	enum ActivityState {
		INITIALIZING, RESUMED, PAUSING, PAUSED, STOPPING, STOPPED, FINISHING, DESTROYING, DESTROYED
	}

	final ActivityManagerService mService = null;
	final boolean mMainStack = false;

	final Context mContext = null;
	//
	// /**
	// * The back history of all previous (and possibly still running)
	// activities.
	// * It contains HistoryRecord objects.
	// */
	// final ArrayList<ActivityRecord> mHistory = new
	// ArrayList<ActivityRecord>();
	//
	// /**
	// * Used for validating app tokens with window manager.
	// */
	// // final ArrayList<IBinder> mValidateAppTokens = new
	// ArrayList<IBinder>();
	//
	// /**
	// * List of running activities, sorted by recent usage. The first entry in
	// * the list is the least recently used. It contains HistoryRecord objects.
	// */
	// final ArrayList<ActivityRecord> mLRUActivities = new
	// ArrayList<ActivityRecord>();
	//
	// /**
	// * List of activities that are waiting for a new activity to become
	// visible
	// * before completing whatever operation they are supposed to do.
	// */
	// final ArrayList<ActivityRecord> mWaitingVisibleActivities = new
	// ArrayList<ActivityRecord>();
	//
	// /**
	// * List of activities that are ready to be stopped, but waiting for the
	// next
	// * activity to settle down before doing so. It contains HistoryRecord
	// * objects.
	// */
	// final ArrayList<ActivityRecord> mStoppingActivities = new
	// ArrayList<ActivityRecord>();
	//
	// /**
	// * List of activities that are in the process of going to sleep.
	// */
	// final ArrayList<ActivityRecord> mGoingToSleepActivities = new
	// ArrayList<ActivityRecord>();
	//
	// /**
	// * Animations that for the current transition have requested not to be
	// * considered for the transition animation.
	// */
	// final ArrayList<ActivityRecord> mNoAnimActivities = new
	// ArrayList<ActivityRecord>();
	//
	// /**
	// * List of activities that are ready to be finished, but waiting for the
	// * previous activity to settle down before doing so. It contains
	// * HistoryRecord objects.
	// */
	// final ArrayList<ActivityRecord> mFinishingActivities = new
	// ArrayList<ActivityRecord>();
	//
	// /**
	// * List of people waiting to find out about the next launched activity.
	// */
	// final ArrayList<IActivityManager.WaitResult> mWaitingActivityLaunched =
	// new ArrayList<IActivityManager.WaitResult>();
	//
	// /**
	// * List of people waiting to find out about the next visible activity.
	// */
	// final ArrayList<IActivityManager.WaitResult> mWaitingActivityVisible =
	// new ArrayList<IActivityManager.WaitResult>();
	//
	// /**
	// * Set when the system is going to sleep, until we have successfully
	// paused
	// * the current activity and released our wake lock. At that point the
	// system
	// * is allowed to actually sleep.
	// */
	// final PowerManager.WakeLock mGoingToSleep;
	//
	// /**
	// * When we are in the process of pausing an activity, before starting the
	// * next one, this variable holds the activity that is currently being
	// * paused.
	// */
	// ActivityRecord mPausingActivity = null;
	//
	// /**
	// * This is the last activity that we put into the paused state. This is
	// used
	// * to determine if we need to do an activity transition while sleeping,
	// when
	// * we normally hold the top activity paused.
	// */
	// ActivityRecord mLastPausedActivity = null;
	//
	// /**
	// * Current activity that is resumed, or null if there is none.
	// */
	// ActivityRecord mResumedActivity = null;
	//
	// /**
	// * This is the last activity that has been started. It is only used to
	// * identify when multiple activities are started at once so that the user
	// * can be warned they may not be in the activity they think they are.
	// */
	// ActivityRecord mLastStartedActivity = null;
	//
	// final ActivityRecord topRunningActivityLocked(ActivityRecord notTop) {
	// int i = mHistory.size() - 1;
	// while (i >= 0) {
	// ActivityRecord r = mHistory.get(i);
	// if (!r.finishing && r != notTop) {
	// return r;
	// }
	// i--;
	// }
	// return null;
	// }
	//
	// /**
	// * Ensure that the top activity in the stack is resumed.
	// *
	// * @param prev
	// * The previously resumed activity, for when in the process of
	// * pausing; can be null to call from elsewhere.
	// *
	// * @return Returns true if something is being resumed, or false if nothing
	// * happened.
	// */
	// final boolean resumeTopActivityLocked(ActivityRecord prev) {
	// // Find the first activity that is not finishing.
	// ActivityRecord next = topRunningActivityLocked(null);
	//
	// // Remember how we'll process this pause/resume situation, and ensure
	// // that the state is reset however we wind up proceeding.
	// final boolean userLeaving = mUserLeaving;
	// mUserLeaving = false;
	//
	// if (next == null) {
	// // There are no more activities! Let's just start up the
	// // Launcher...
	// if (mMainStack) {
	// return mService.startHomeActivityLocked();
	// }
	// }
	//
	// next.delayedResume = false;
	//
	// // If the top activity is the resumed one, nothing to do.
	// if (mResumedActivity == next && next.state == ActivityState.RESUMED) {
	// // Make sure we have executed any pending transitions, since there
	// // should be nothing left to do at this point.
	// // mService.mWindowManager.executeAppTransition();
	// // mNoAnimActivities.clear();
	// return false;
	// }
	//
	// // If we are sleeping, and there is no resumed activity, and the top
	// // activity is paused, well that is the state we want.
	// if ((mService.mSleeping || mService.mShuttingDown)
	// && mLastPausedActivity == next
	// && next.state == ActivityState.PAUSED) {
	// // Make sure we have executed any pending transitions, since there
	// // should be nothing left to do at this point.
	// // mService.mWindowManager.executeAppTransition();
	// // mNoAnimActivities.clear();
	// return false;
	// }
	//
	// // The activity may be waiting for stop, but that is no longer
	// // appropriate for it.
	// mStoppingActivities.remove(next);
	// mGoingToSleepActivities.remove(next);
	// next.sleeping = false;
	// mWaitingVisibleActivities.remove(next);
	//
	// if (DEBUG_SWITCH)
	// Slog.v(TAG, "Resuming " + next);
	//
	// // If we are currently pausing an activity, then don't do anything
	// // until that is done.
	// if (mPausingActivity != null) {
	// if (DEBUG_SWITCH)
	// Slog.v(TAG, "Skip resume: pausing=" + mPausingActivity);
	// return false;
	// }
	//
	// // Okay we are now going to start a switch, to 'next'. We may first
	// // have to pause the current activity, but this is an important point
	// // where we have decided to go to 'next' so keep track of that.
	// // XXX "App Redirected" dialog is getting too many false positives
	// // at this point, so turn off for now.
	// if (false) {
	// if (mLastStartedActivity != null && !mLastStartedActivity.finishing) {
	// long now = SystemClock.uptimeMillis();
	// //final boolean inTime = mLastStartedActivity.startTime != 0
	// // && (mLastStartedActivity.startTime + START_WARN_TIME) >= now;
	// final int lastUid = mLastStartedActivity.info.applicationInfo.uid;
	// final int nextUid = next.info.applicationInfo.uid;
	// if (lastUid != nextUid
	// && lastUid != next.launchedFromUid
	// && true){ // assume permissions set
	// //TODO mService.checkPermission(
	// // android.Manifest.permission.STOP_APP_SWITCHES,
	// //-1, next.launchedFromUid) != PackageManager.PERMISSION_GRANTED) {
	// mService.showLaunchWarningLocked(mLastStartedActivity, next);
	// } else {
	// next.startTime = now;
	// mLastStartedActivity = next;
	// }
	// } else {
	// next.startTime = SystemClock.uptimeMillis();
	// mLastStartedActivity = next;
	// }
	// }
	//
	// // We need to start pausing the current activity so the top one
	// // can be resumed...
	// if (mResumedActivity != null) {
	// if (DEBUG_SWITCH)
	// Slog.v(TAG, "Skip resume: need to start pausing");
	// startPausingLocked(userLeaving, false);
	// return true;
	// }
	//
	// if (prev != null && prev != next) {
	// if (!prev.waitingVisible && next != null && !next.nowVisible) {
	// prev.waitingVisible = true;
	// mWaitingVisibleActivities.add(prev);
	// if (DEBUG_SWITCH)
	// Slog.v(TAG, "Resuming top, waiting visible to hide: "
	// + prev);
	// } else {
	// // The next activity is already visible, so hide the previous
	// // activity's windows right now so we can show the new one ASAP.
	// // We only do this if the previous is finishing, which should
	// // mean
	// // it is on top of the one being resumed so hiding it quickly
	// // is good. Otherwise, we want to do the normal route of
	// // allowing
	// // the resumed activity to be shown so we can decide if the
	// // previous should actually be hidden depending on whether the
	// // new one is found to be full-screen or not.
	// if (prev.finishing) {
	// mService.mWindowManager.setAppVisibility(prev.appToken,
	// false);
	// if (DEBUG_SWITCH)
	// Slog.v(TAG, "Not waiting for visible to hide: " + prev
	// + ", waitingVisible="
	// + (prev != null ? prev.waitingVisible : null)
	// + ", nowVisible=" + next.nowVisible);
	// } else {
	// if (DEBUG_SWITCH)
	// Slog.v(TAG,
	// "Previous already visible but still waiting to hide: "
	// + prev
	// + ", waitingVisible="
	// + (prev != null ? prev.waitingVisible
	// : null) + ", nowVisible="
	// + next.nowVisible);
	// }
	// }
	// }
	//
	// // Launching this app's activity, make sure the app is no longer
	// // considered stopped.
	// try {
	// AppGlobals.getPackageManager().setPackageStoppedState(
	// next.packageName, false);
	// } catch (RemoteException e1) {
	// } catch (IllegalArgumentException e) {
	// Slog.w(TAG, "Failed trying to unstop package " + next.packageName
	// + ": " + e);
	// }
	//
	// // We are starting up the next activity, so tell the window manager
	// // that the previous one will be hidden soon. This way it can know
	// // to ignore it when computing the desired screen orientation.
	// if (prev != null) {
	// if (prev.finishing) {
	// if (DEBUG_TRANSITION)
	// Slog.v(TAG, "Prepare close transition: prev=" + prev);
	// if (mNoAnimActivities.contains(prev)) {
	// mService.mWindowManager.prepareAppTransition(
	// WindowManagerPolicy.TRANSIT_NONE, false);
	// } else {
	// mService.mWindowManager
	// .prepareAppTransition(
	// prev.task == next.task ? WindowManagerPolicy.TRANSIT_ACTIVITY_CLOSE
	// : WindowManagerPolicy.TRANSIT_TASK_CLOSE,
	// false);
	// }
	// mService.mWindowManager.setAppWillBeHidden(prev.appToken);
	// mService.mWindowManager.setAppVisibility(prev.appToken, false);
	// } else {
	// if (DEBUG_TRANSITION)
	// Slog.v(TAG, "Prepare open transition: prev=" + prev);
	// if (mNoAnimActivities.contains(next)) {
	// mService.mWindowManager.prepareAppTransition(
	// WindowManagerPolicy.TRANSIT_NONE, false);
	// } else {
	// mService.mWindowManager
	// .prepareAppTransition(
	// prev.task == next.task ? WindowManagerPolicy.TRANSIT_ACTIVITY_OPEN
	// : WindowManagerPolicy.TRANSIT_TASK_OPEN,
	// false);
	// }
	// }
	// if (false) {
	// mService.mWindowManager.setAppWillBeHidden(prev.appToken);
	// mService.mWindowManager.setAppVisibility(prev.appToken, false);
	// }
	// } else if (mHistory.size() > 1) {
	// if (DEBUG_TRANSITION)
	// Slog.v(TAG, "Prepare open transition: no previous");
	// if (mNoAnimActivities.contains(next)) {
	// mService.mWindowManager.prepareAppTransition(
	// WindowManagerPolicy.TRANSIT_NONE, false);
	// } else {
	// mService.mWindowManager.prepareAppTransition(
	// WindowManagerPolicy.TRANSIT_ACTIVITY_OPEN, false);
	// }
	// }
	//
	// if (next.app != null && next.using the current activity so the top
	// oneapp.thread != null) {
	// if (DEBUG_SWITCH)
	// Slog.v(TAG, "Resume running: " + next);
	//
	// // This activity is now becoming visible.
	// mService.mWindowManager.setAppVisibility(next.appToken, true);
	//
	// // schedule launch ticks to collect information about slow apps.
	// next.startLaunchTickingLocked();
	//
	// ActivityRecord lastResumedActivity = mResumedActivity;
	// ActivityState lastState = next.state;
	//
	// mService.updateCpuStats();
	//
	// if (DEBUG_STATES)
	// Slog.v(TAG, "Moving to RESUMED: " + next + " (in existing)");
	// next.state = ActivityState.RESUMED;
	// mResumedActivity = next;
	// next.task.touchActiveTime();
	// if (mMainStack) {
	// mService.addRecentTaskLocked(next.task);
	// }
	// mService.updateLruProcessLocked(next.app, true, true);
	// updateLRUListLocked(next);
	//
	// // Have the window manager re-evaluate the orientation of
	// // the screen based on the new activity order.
	// boolean updated = false;
	// if (mMainStack) {
	// synchronized (mService) {
	// Configuration config = mService.mWindowManager
	// .updateOrientationFromAppTokens(
	// mService.mConfiguration,
	// next.mayFreezeScreenLocked(next.app) ? next.appToken
	// : null);
	// if (config != null) {
	// next.frozenBeforeDestroy = true;
	// }
	// updated = mService.updateConfigurationLocked(config, next,
	// false, false);
	// }
	// }
	// if (!updated) {
	// // The configuration update wasn't able to keep the existing
	// // instance of the activity, and instead started a new one.
	// // We should be all done, but let's just make sure our activity
	// // is still at the top and schedule another run if something
	// // weird happened.
	// ActivityRecord nextNext = topRunningActivityLocked(null);
	// if (DEBUG_SWITCH)
	// Slog.i(TAG, "Activity config changed during resume: "
	// + next + ", new next: " + nextNext);
	// if (nextNext != next) {
	// // Do over!
	// mHandler.sendEmptyMessage(RESUME_TOP_ACTIVITY_MSG);
	// }
	// if (mMainStack) {
	// mService.setFocusedActivityLocked(next);
	// }
	// ensureActivitiesVisibleLocked(null, 0);
	// mService.mWindowManager.executeAppTransition();
	// mNoAnimActivities.clear();
	// return true;
	// }
	//
	// try {
	// // Deliver all pending results.
	// ArrayList a = next.results;
	// if (a != null) {
	// final int N = a.size();
	// if (!next.finishing && N > 0) {
	// if (DEBUG_RESULTS)
	// Slog.v(TAG, "Delivering results to " + next + ": "
	// + a);
	// next.app.thread.scheduleSendResult(next.appToken, a);
	// }
	// }
	//
	// if (next.newIntents != null) {
	// next.app.thread.scheduleNewIntent(next.newIntents,
	// next.appToken);
	// }
	//
	// EventLog.writeEvent(EventLogTags.AM_RESUME_ACTIVITY,
	// System.identityHashCode(next), next.task.taskId,
	// next.shortComponentName);
	//
	// next.sleeping = false;
	// showAskCompatModeDialogLocked(next);
	// next.app.pendingUiClean = true;
	// next.app.thread.scheduleResumeActivity(next.appToken,
	// mService.isNextTransitionForward());
	//
	// checkReadyForSleepLocked();
	//
	// } catch (Exception e) {
	// // Whoops, need to restart this activity!
	// if (DEBUG_STATES)
	// Slog.v(TAG, "Resume failed; resetting state to "
	// + lastState + ": " + next);
	// next.state = lastState;
	// mResumedActivity = lastResumedActivity;
	// Slog.i(TAG, "Restarting because process died: " + next);
	// if (!next.hasBeenLaunched) {
	// next.hasBeenLaunched = true;
	// } else {
	// if (SHOW_APP_STARTING_PREVIEW && mMainStack) {
	// mService.mWindowManager
	// .setAppStartingWindow(
	// next.appToken,
	// next.packageName,
	// next.theme,
	// mService.compatibilityInfoForPackageLocked(next.info.applicationInfo),
	// next.nonLocalizedLabel, next.labelRes,
	// next.icon, next.windowFlags, null, true);
	// }
	// }
	// startSpecificActivityLocked(next, true, false);
	// return true;
	// }
	//
	// // From this point on, if something goes wrong there is no way
	// // to recover the activity.
	// try {
	// next.visible = true;
	// completeResumeLocked(next);
	// } catch (Exception e) {
	// // If any exception gets thrown, toss away this
	// // activity and try the next one.
	// Slog.w(TAG, "Exception thrown during resume of " + next, e);
	// requestFinishActivityLocked(next.appToken,
	// Activity.RESULT_CANCELED, null, "resume-exception");
	// return true;
	// }
	//
	// // Didn't need to use the icicle, and it is now out of date.
	// if (DEBUG_SAVED_STATE)
	// Slog.i(TAG, "Resumed activity; didn't need icicle of: " + next);
	// next.icicle = null;
	// next.haveState = false;
	// next.stopped = false;
	//
	// } else {
	// // Whoops, need to restart this activity!
	// if (!next.hasBeenLaunched) {
	// next.hasBeenLaunched = true;
	// } else {
	// if (SHOW_APP_STARTING_PREVIEW) {
	// mService.mWindowManager
	// .setAppStartingWindow(
	// next.appToken,
	// next.packageName,
	// next.theme,
	// mService.compatibilityInfoForPackageLocked(next.info.applicationInfo),
	// next.nonLocalizedLabel, next.labelRes,
	// next.icon, next.windowFlags, null, true);
	// }
	// if (DEBUG_SWITCH)
	// Slog.v(TAG, "Restarting: " + next);
	// }
	// startSpecificActivityLocked(next, true, true);
	// }
	//
	// return true;
	// }
	//

}
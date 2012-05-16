package android.app;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.JPFLogger;

import java.util.HashMap;
import java.util.logging.Level;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewRootImpl;

/**
 * This manages the execution of the main thread in an application process,
 * scheduling and executing activities, broadcasts, and other operations on it
 * as the activity manager requests.
 * 
 * {@hide}
 */
public final class ActivityThread {
	public static final String TAG = "ActivityThread";
	public static final boolean DEBUG_MESSAGES = true;
	static JPFLogger logger = JPF.getLogger(TAG);

	final ApplicationThread mAppThread = new ApplicationThread();
	final ViewRootImpl windowModel = new ViewRootImpl(new ContextImpl());
	final Looper mLooper = Looper.myLooper();
	final H mH = new H();
	static final ThreadLocal<ActivityThread> sThreadLocal = new ThreadLocal<ActivityThread>();

	HashMap<String, Activity> Activities;

	Activity currentActivity;

	// if the thread hasn't started yet, we don't have the handler, so just
	// save the messages until we're ready.
	private void queueOrSendMessage(int what, Object obj) {
		queueOrSendMessage(what, obj, 0, 0);
	}

	private void queueOrSendMessage(int what, Object obj, int arg1) {
		queueOrSendMessage(what, obj, arg1, 0);
	}

	private void queueOrSendMessage(int what, Object obj, int arg1, int arg2) {
		synchronized (this) {
			if (DEBUG_MESSAGES)
				logger.log(Level.INFO,
						TAG + "SCHEDULE " + what + " " + mH.codeToString(what)
								+ ": " + arg1 + " / " + obj);
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = obj;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			mH.sendMessage(msg);
		}
	}

	private class ApplicationThread {// extends ApplicationThreadNative {
		// private static final String HEAP_COLUMN =
		// "%13s %8s %8s %8s %8s %8s %8s";
		// private static final String ONE_COUNT_COLUMN = "%21s %8d";
		// private static final String TWO_COUNT_COLUMNS = "%21s %8d %21s %8d";
		// private static final String TWO_COUNT_COLUMNS_DB =
		// "%21s %8d %21s %8d";
		// private static final String DB_INFO_FORMAT =
		// "  %8s %8s %14s %14s  %s";

		// Formatting for checkin service - update version if row format changes
		// private static final int ACTIVITY_THREAD_CHECKIN_VERSION = 1;

		// private void updatePendingConfiguration(Configuration config) {
		// synchronized (mPackages) {
		// if (mPendingConfiguration == null ||
		// mPendingConfiguration.isOtherSeqNewer(config)) {
		// mPendingConfiguration = config;
		// }
		// }
		// }
		//
		// public final void schedulePauseActivity(IBinder token, boolean
		// finished,
		// boolean userLeaving, int configChanges) {
		// queueOrSendMessage(
		// finished ? H.PAUSE_ACTIVITY_FINISHING : H.PAUSE_ACTIVITY,
		// token,
		// (userLeaving ? 1 : 0),
		// configChanges);
		// }
		//
		// public final void scheduleStopActivity(IBinder token, boolean
		// showWindow,
		// int configChanges) {
		// queueOrSendMessage(
		// showWindow ? H.STOP_ACTIVITY_SHOW : H.STOP_ACTIVITY_HIDE,
		// token, 0, configChanges);
		// }
		//
		// public final void scheduleWindowVisibility(IBinder token, boolean
		// showWindow) {
		// queueOrSendMessage(
		// showWindow ? H.SHOW_WINDOW : H.HIDE_WINDOW,
		// token);
		// }
		//
		// public final void scheduleSleeping(IBinder token, boolean sleeping) {
		// queueOrSendMessage(H.SLEEPING, token, sleeping ? 1 : 0);
		// }
		//
		// public final void scheduleResumeActivity(IBinder token, boolean
		// isForward) {
		// queueOrSendMessage(H.RESUME_ACTIVITY, token, isForward ? 1 : 0);
		// }
		//
		// public final void scheduleSendResult(IBinder token, List<ResultInfo>
		// results) {
		// ResultData res = new ResultData();
		// res.token = token;
		// res.results = results;
		// queueOrSendMessage(H.SEND_RESULT, res);
		// }

		// we use token to identify this activity without having to send the
		// activity itself back to the activity manager. (matters more with ipc)
		public final void scheduleLaunchActivity(String activity) {// Intent
																	// intent,
																	// IBinder
																	// token,
																	// int
																	// ident,
			// ActivityInfo info, Configuration curConfig, CompatibilityInfo
			// compatInfo,
			// Bundle state, List<ResultInfo> pendingResults,
			// List<Intent> pendingNewIntents, boolean notResumed, boolean
			// isForward,
			// String profileName, ParcelFileDescriptor profileFd, boolean
			// autoStopProfiler) {
			// ActivityClientRecord r = new ActivityClientRecord();
			//
			// r.token = token;
			// r.ident = ident;
			// r.intent = intent;
			// r.activityInfo = info;
			// r.compatInfo = compatInfo;
			// r.state = state;
			//
			// r.pendingResults = pendingResults;
			// r.pendingIntents = pendingNewIntents;
			//
			// r.startsNotResumed = notResumed;
			// r.isForward = isForward;
			//
			// r.profileFile = profileName;
			// r.profileFd = profileFd;
			// r.autoStopProfiler = autoStopProfiler;
			//
			// updatePendingConfiguration(curConfig);

			queueOrSendMessage(H.LAUNCH_ACTIVITY, activity);
		}

		// public final void scheduleRelaunchActivity(IBinder token,
		// List<ResultInfo> pendingResults, List<Intent> pendingNewIntents,
		// int configChanges, boolean notResumed, Configuration config) {
		// requestRelaunchActivity(token, pendingResults, pendingNewIntents,
		// configChanges, notResumed, config, true);
		// }
		//
		// public final void scheduleNewIntent(List<Intent> intents, IBinder
		// token) {
		// NewIntentData data = new NewIntentData();
		// data.intents = intents;
		// data.token = token;
		//
		// queueOrSendMessage(H.NEW_INTENT, data);
		// }
		//
		// public final void scheduleDestroyActivity(IBinder token, boolean
		// finishing,
		// int configChanges) {
		// queueOrSendMessage(H.DESTROY_ACTIVITY, token, finishing ? 1 : 0,
		// configChanges);
		// }
		//
		// public final void scheduleReceiver(Intent intent, ActivityInfo info,
		// CompatibilityInfo compatInfo, int resultCode, String data, Bundle
		// extras,
		// boolean sync) {
		// ReceiverData r = new ReceiverData(intent, resultCode, data, extras,
		// sync, false, mAppThread.asBinder());
		// r.info = info;
		// r.compatInfo = compatInfo;
		// queueOrSendMessage(H.RECEIVER, r);
		// }
		//
		// public final void scheduleCreateBackupAgent(ApplicationInfo app,
		// CompatibilityInfo compatInfo, int backupMode) {
		// CreateBackupAgentData d = new CreateBackupAgentData();
		// d.appInfo = app;
		// d.compatInfo = compatInfo;
		// d.backupMode = backupMode;
		//
		// queueOrSendMessage(H.CREATE_BACKUP_AGENT, d);
		// }
		//
		// public final void scheduleDestroyBackupAgent(ApplicationInfo app,
		// CompatibilityInfo compatInfo) {
		// CreateBackupAgentData d = new CreateBackupAgentData();
		// d.appInfo = app;
		// d.compatInfo = compatInfo;
		//
		// queueOrSendMessage(H.DESTROY_BACKUP_AGENT, d);
		// }
		//
		// public final void scheduleCreateService(IBinder token,
		// ServiceInfo info, CompatibilityInfo compatInfo) {
		// CreateServiceData s = new CreateServiceData();
		// s.token = token;
		// s.info = info;
		// s.compatInfo = compatInfo;
		//
		// queueOrSendMessage(H.CREATE_SERVICE, s);
		// }
		//
		// public final void scheduleBindService(IBinder token, Intent intent,
		// boolean rebind) {
		// BindServiceData s = new BindServiceData();
		// s.token = token;
		// s.intent = intent;
		// s.rebind = rebind;
		//
		// queueOrSendMessage(H.BIND_SERVICE, s);
		// }
		//
		// public final void scheduleUnbindService(IBinder token, Intent intent)
		// {
		// BindServiceData s = new BindServiceData();
		// s.token = token;
		// s.intent = intent;
		//
		// queueOrSendMessage(H.UNBIND_SERVICE, s);
		// }
		//
		// public final void scheduleServiceArgs(IBinder token, boolean
		// taskRemoved, int startId,
		// int flags ,Intent args) {
		// ServiceArgsData s = new ServiceArgsData();
		// s.token = token;
		// s.taskRemoved = taskRemoved;
		// s.startId = startId;
		// s.flags = flags;
		// s.args = args;
		//
		// queueOrSendMessage(H.SERVICE_ARGS, s);
		// }
		//
		// public final void scheduleStopService(IBinder token) {
		// queueOrSendMessage(H.STOP_SERVICE, token);
		// }

		// public final void bindApplication(String processName,
		// ApplicationInfo appInfo, List<ProviderInfo> providers,
		// ComponentName instrumentationName, String profileFile,
		// ParcelFileDescriptor profileFd, boolean autoStopProfiler,
		// Bundle instrumentationArgs, IInstrumentationWatcher
		// instrumentationWatcher,
		// int debugMode, boolean isRestrictedBackupMode, boolean persistent,
		// Configuration config, CompatibilityInfo compatInfo,
		// Map<String, IBinder> services, Bundle coreSettings) {
		//
		// if (services != null) {
		// // Setup the service cache in the ServiceManager
		// ServiceManager.initServiceCache(services);
		// }
		//
		// setCoreSettings(coreSettings);
		//
		// AppBindData data = new AppBindData();
		// data.processName = processName;
		// data.appInfo = appInfo;
		// data.providers = providers;
		// data.instrumentationName = instrumentationName;
		// data.instrumentationArgs = instrumentationArgs;
		// data.instrumentationWatcher = instrumentationWatcher;
		// data.debugMode = debugMode;
		// data.restrictedBackupMode = isRestrictedBackupMode;
		// data.persistent = persistent;
		// data.config = config;
		// data.compatInfo = compatInfo;
		// data.initProfileFile = profileFile;
		// data.initProfileFd = profileFd;
		// data.initAutoStopProfiler = false;
		// queueOrSendMessage(H.BIND_APPLICATION, data);
		// }

		// public final void scheduleExit() {
		// queueOrSendMessage(H.EXIT_APPLICATION, null);
		// }
		//
		// public final void scheduleSuicide() {
		// queueOrSendMessage(H.SUICIDE, null);
		// }
		//
		// public void requestThumbnail(IBinder token) {
		// queueOrSendMessage(H.REQUEST_THUMBNAIL, token);
		// }
		//
		// public void scheduleConfigurationChanged(Configuration config) {
		// updatePendingConfiguration(config);
		// queueOrSendMessage(H.CONFIGURATION_CHANGED, config);
		// }
		//
		// public void updateTimeZone() {
		// TimeZone.setDefault(null);
		// }
		//
		// public void clearDnsCache() {
		// // a non-standard API to get this to libcore
		// InetAddress.clearDnsCache();
		// }
		//
		// public void setHttpProxy(String host, String port, String exclList) {
		// Proxy.setHttpProxySystemProperty(host, port, exclList);
		// }
		//
		// public void processInBackground() {
		// mH.removeMessages(H.GC_WHEN_IDLE);
		// mH.sendMessage(mH.obtainMessage(H.GC_WHEN_IDLE));
		// }
		//
		// public void dumpService(FileDescriptor fd, IBinder servicetoken,
		// String[] args) {
		// DumpComponentInfo data = new DumpComponentInfo();
		// try {
		// data.fd = ParcelFileDescriptor.dup(fd);
		// data.token = servicetoken;
		// data.args = args;
		// queueOrSendMessage(H.DUMP_SERVICE, data);
		// } catch (IOException e) {
		// Slog.w(TAG, "dumpService failed", e);
		// }
		// }
		//
		// // This function exists to make sure all receiver dispatching is
		// // correctly ordered, since these are one-way calls and the binder
		// driver
		// // applies transaction ordering per object for such calls.
		// public void scheduleRegisteredReceiver(IIntentReceiver receiver,
		// Intent intent,
		// int resultCode, String dataStr, Bundle extras, boolean ordered,
		// boolean sticky) throws RemoteException {
		// receiver.performReceive(intent, resultCode, dataStr, extras, ordered,
		// sticky);
		// }
		//
		// public void scheduleLowMemory() {
		// queueOrSendMessage(H.LOW_MEMORY, null);
		// }
		//
		// public void scheduleActivityConfigurationChanged(IBinder token) {
		// queueOrSendMessage(H.ACTIVITY_CONFIGURATION_CHANGED, token);
		// }
		//
		// public void profilerControl(boolean start, String path,
		// ParcelFileDescriptor fd,
		// int profileType) {
		// ProfilerControlData pcd = new ProfilerControlData();
		// pcd.path = path;
		// pcd.fd = fd;
		// queueOrSendMessage(H.PROFILER_CONTROL, pcd, start ? 1 : 0,
		// profileType);
		// }
		//
		// public void dumpHeap(boolean managed, String path,
		// ParcelFileDescriptor fd) {
		// DumpHeapData dhd = new DumpHeapData();
		// dhd.path = path;
		// dhd.fd = fd;
		// queueOrSendMessage(H.DUMP_HEAP, dhd, managed ? 1 : 0);
		// }
		//
		// public void setSchedulingGroup(int group) {
		// // Note: do this immediately, since going into the foreground
		// // should happen regardless of what pending work we have to do
		// // and the activity manager will wait for us to report back that
		// // we are done before sending us to the background.
		// try {
		// Process.setProcessGroup(Process.myPid(), group);
		// } catch (Exception e) {
		// Slog.w(TAG, "Failed setting process group to " + group, e);
		// }
		// }
		//
		// public void getMemoryInfo(Debug.MemoryInfo outInfo) {
		// Debug.getMemoryInfo(outInfo);
		// }
		//
		// public void dispatchPackageBroadcast(int cmd, String[] packages) {
		// queueOrSendMessage(H.DISPATCH_PACKAGE_BROADCAST, packages, cmd);
		// }
		//
		// public void scheduleCrash(String msg) {
		// queueOrSendMessage(H.SCHEDULE_CRASH, msg);
		// }
		//
		// public void dumpActivity(FileDescriptor fd, IBinder activitytoken,
		// String prefix, String[] args) {
		// DumpComponentInfo data = new DumpComponentInfo();
		// try {
		// data.fd = ParcelFileDescriptor.dup(fd);
		// data.token = activitytoken;
		// data.prefix = prefix;
		// data.args = args;
		// queueOrSendMessage(H.DUMP_ACTIVITY, data);
		// } catch (IOException e) {
		// Slog.w(TAG, "dumpActivity failed", e);
		// }
		// }
		//
		// @Override
		// public Debug.MemoryInfo dumpMemInfo(FileDescriptor fd, boolean
		// checkin,
		// boolean all, String[] args) {
		// FileOutputStream fout = new FileOutputStream(fd);
		// PrintWriter pw = new PrintWriter(fout);
		// try {
		// return dumpMemInfo(pw, checkin, all, args);
		// } finally {
		// pw.flush();
		// }
		// }
		//
		// private Debug.MemoryInfo dumpMemInfo(PrintWriter pw, boolean checkin,
		// boolean all,
		// String[] args) {
		// long nativeMax = Debug.getNativeHeapSize() / 1024;
		// long nativeAllocated = Debug.getNativeHeapAllocatedSize() / 1024;
		// long nativeFree = Debug.getNativeHeapFreeSize() / 1024;
		//
		// Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
		// Debug.getMemoryInfo(memInfo);
		//
		// if (!all) {
		// return memInfo;
		// }
		//
		// Runtime runtime = Runtime.getRuntime();
		//
		// long dalvikMax = runtime.totalMemory() / 1024;
		// long dalvikFree = runtime.freeMemory() / 1024;
		// long dalvikAllocated = dalvikMax - dalvikFree;
		// long viewInstanceCount = ViewDebug.getViewInstanceCount();
		// long viewRootInstanceCount = ViewDebug.getViewRootImplCount();
		// long appContextInstanceCount =
		// Debug.countInstancesOfClass(ContextImpl.class);
		// long activityInstanceCount =
		// Debug.countInstancesOfClass(Activity.class);
		// int globalAssetCount = AssetManager.getGlobalAssetCount();
		// int globalAssetManagerCount =
		// AssetManager.getGlobalAssetManagerCount();
		// int binderLocalObjectCount = Debug.getBinderLocalObjectCount();
		// int binderProxyObjectCount = Debug.getBinderProxyObjectCount();
		// int binderDeathObjectCount = Debug.getBinderDeathObjectCount();
		// long openSslSocketCount =
		// Debug.countInstancesOfClass(OpenSSLSocketImpl.class);
		// long sqliteAllocated = SQLiteDebug.getHeapAllocatedSize() / 1024;
		// SQLiteDebug.PagerStats stats = SQLiteDebug.getDatabaseInfo();
		//
		// // For checkin, we print one long comma-separated list of values
		// if (checkin) {
		// // NOTE: if you change anything significant below, also consider
		// changing
		// // ACTIVITY_THREAD_CHECKIN_VERSION.
		// String processName = (mBoundApplication != null)
		// ? mBoundApplication.processName : "unknown";
		//
		// // Header
		// pw.print(ACTIVITY_THREAD_CHECKIN_VERSION); pw.print(',');
		// pw.print(Process.myPid()); pw.print(',');
		// pw.print(processName); pw.print(',');
		//
		// // Heap info - max
		// pw.print(nativeMax); pw.print(',');
		// pw.print(dalvikMax); pw.print(',');
		// pw.print("N/A,");
		// pw.print(nativeMax + dalvikMax); pw.print(',');
		//
		// // Heap info - allocated
		// pw.print(nativeAllocated); pw.print(',');
		// pw.print(dalvikAllocated); pw.print(',');
		// pw.print("N/A,");
		// pw.print(nativeAllocated + dalvikAllocated); pw.print(',');
		//
		// // Heap info - free
		// pw.print(nativeFree); pw.print(',');
		// pw.print(dalvikFree); pw.print(',');
		// pw.print("N/A,");
		// pw.print(nativeFree + dalvikFree); pw.print(',');
		//
		// // Heap info - proportional set size
		// pw.print(memInfo.nativePss); pw.print(',');
		// pw.print(memInfo.dalvikPss); pw.print(',');
		// pw.print(memInfo.otherPss); pw.print(',');
		// pw.print(memInfo.nativePss + memInfo.dalvikPss + memInfo.otherPss);
		// pw.print(',');
		//
		// // Heap info - shared
		// pw.print(memInfo.nativeSharedDirty); pw.print(',');
		// pw.print(memInfo.dalvikSharedDirty); pw.print(',');
		// pw.print(memInfo.otherSharedDirty); pw.print(',');
		// pw.print(memInfo.nativeSharedDirty + memInfo.dalvikSharedDirty
		// + memInfo.otherSharedDirty); pw.print(',');
		//
		// // Heap info - private
		// pw.print(memInfo.nativePrivateDirty); pw.print(',');
		// pw.print(memInfo.dalvikPrivateDirty); pw.print(',');
		// pw.print(memInfo.otherPrivateDirty); pw.print(',');
		// pw.print(memInfo.nativePrivateDirty + memInfo.dalvikPrivateDirty
		// + memInfo.otherPrivateDirty); pw.print(',');
		//
		// // Object counts
		// pw.print(viewInstanceCount); pw.print(',');
		// pw.print(viewRootInstanceCount); pw.print(',');
		// pw.print(appContextInstanceCount); pw.print(',');
		// pw.print(activityInstanceCount); pw.print(',');
		//
		// pw.print(globalAssetCount); pw.print(',');
		// pw.print(globalAssetManagerCount); pw.print(',');
		// pw.print(binderLocalObjectCount); pw.print(',');
		// pw.print(binderProxyObjectCount); pw.print(',');
		//
		// pw.print(binderDeathObjectCount); pw.print(',');
		// pw.print(openSslSocketCount); pw.print(',');
		//
		// // SQL
		// pw.print(sqliteAllocated); pw.print(',');
		// pw.print(stats.memoryUsed / 1024); pw.print(',');
		// pw.print(stats.pageCacheOverflo / 1024); pw.print(',');
		// pw.print(stats.largestMemAlloc / 1024);
		// for (int i = 0; i < stats.dbStats.size(); i++) {
		// DbStats dbStats = stats.dbStats.get(i);
		// pw.print(','); pw.print(dbStats.dbName);
		// pw.print(','); pw.print(dbStats.pageSize);
		// pw.print(','); pw.print(dbStats.dbSize);
		// pw.print(','); pw.print(dbStats.lookaside);
		// pw.print(','); pw.print(dbStats.cache);
		// pw.print(','); pw.print(dbStats.cache);
		// }
		// pw.println();
		//
		// return memInfo;
		// }
		//
		// // otherwise, show human-readable format
		// printRow(pw, HEAP_COLUMN, "", "", "Shared", "Private", "Heap",
		// "Heap", "Heap");
		// printRow(pw, HEAP_COLUMN, "", "Pss", "Dirty", "Dirty", "Size",
		// "Alloc", "Free");
		// printRow(pw, HEAP_COLUMN, "", "------", "------", "------", "------",
		// "------",
		// "------");
		// printRow(pw, HEAP_COLUMN, "Native", memInfo.nativePss,
		// memInfo.nativeSharedDirty,
		// memInfo.nativePrivateDirty, nativeMax, nativeAllocated, nativeFree);
		// printRow(pw, HEAP_COLUMN, "Dalvik", memInfo.dalvikPss,
		// memInfo.dalvikSharedDirty,
		// memInfo.dalvikPrivateDirty, dalvikMax, dalvikAllocated, dalvikFree);
		//
		// int otherPss = memInfo.otherPss;
		// int otherSharedDirty = memInfo.otherSharedDirty;
		// int otherPrivateDirty = memInfo.otherPrivateDirty;
		//
		// for (int i=0; i<Debug.MemoryInfo.NUM_OTHER_STATS; i++) {
		// printRow(pw, HEAP_COLUMN, Debug.MemoryInfo.getOtherLabel(i),
		// memInfo.getOtherPss(i), memInfo.getOtherSharedDirty(i),
		// memInfo.getOtherPrivateDirty(i), "", "", "");
		// otherPss -= memInfo.getOtherPss(i);
		// otherSharedDirty -= memInfo.getOtherSharedDirty(i);
		// otherPrivateDirty -= memInfo.getOtherPrivateDirty(i);
		// }
		//
		// printRow(pw, HEAP_COLUMN, "Unknown", otherPss, otherSharedDirty,
		// otherPrivateDirty, "", "", "");
		// printRow(pw, HEAP_COLUMN, "TOTAL", memInfo.getTotalPss(),
		// memInfo.getTotalSharedDirty(), memInfo.getTotalPrivateDirty(),
		// nativeMax+dalvikMax, nativeAllocated+dalvikAllocated,
		// nativeFree+dalvikFree);
		//
		// pw.println(" ");
		// pw.println(" Objects");
		// printRow(pw, TWO_COUNT_COLUMNS, "Views:", viewInstanceCount,
		// "ViewRootImpl:",
		// viewRootInstanceCount);
		//
		// printRow(pw, TWO_COUNT_COLUMNS, "AppContexts:",
		// appContextInstanceCount,
		// "Activities:", activityInstanceCount);
		//
		// printRow(pw, TWO_COUNT_COLUMNS, "Assets:", globalAssetCount,
		// "AssetManagers:", globalAssetManagerCount);
		//
		// printRow(pw, TWO_COUNT_COLUMNS, "Local Binders:",
		// binderLocalObjectCount,
		// "Proxy Binders:", binderProxyObjectCount);
		// printRow(pw, ONE_COUNT_COLUMN, "Death Recipients:",
		// binderDeathObjectCount);
		//
		// printRow(pw, ONE_COUNT_COLUMN, "OpenSSL Sockets:",
		// openSslSocketCount);
		//
		// // SQLite mem info
		// pw.println(" ");
		// pw.println(" SQL");
		// printRow(pw, TWO_COUNT_COLUMNS_DB, "heap:", sqliteAllocated,
		// "MEMORY_USED:",
		// stats.memoryUsed / 1024);
		// printRow(pw, TWO_COUNT_COLUMNS_DB, "PAGECACHE_OVERFLOW:",
		// stats.pageCacheOverflo / 1024, "MALLOC_SIZE:", stats.largestMemAlloc
		// / 1024);
		// pw.println(" ");
		// int N = stats.dbStats.size();
		// if (N > 0) {
		// pw.println(" DATABASES");
		// printRow(pw, "  %8s %8s %14s %14s  %s", "pgsz", "dbsz",
		// "Lookaside(b)", "cache",
		// "Dbname");
		// for (int i = 0; i < N; i++) {
		// DbStats dbStats = stats.dbStats.get(i);
		// printRow(pw, DB_INFO_FORMAT,
		// (dbStats.pageSize > 0) ? String.valueOf(dbStats.pageSize) : " ",
		// (dbStats.dbSize > 0) ? String.valueOf(dbStats.dbSize) : " ",
		// (dbStats.lookaside > 0) ? String.valueOf(dbStats.lookaside) : " ",
		// dbStats.cache, dbStats.dbName);
		// }
		// }
		//
		// // Asset details.
		// String assetAlloc = AssetManager.getAssetAllocations();
		// if (assetAlloc != null) {
		// pw.println(" ");
		// pw.println(" Asset Allocations");
		// pw.print(assetAlloc);
		// }
		//
		// return memInfo;
		// }
		//
		// @Override
		// public void dumpGfxInfo(FileDescriptor fd, String[] args) {
		// dumpGraphicsInfo(fd);
		// WindowManagerImpl.getDefault().dumpGfxInfo(fd);
		// }
		//
		// private void printRow(PrintWriter pw, String format, Object...objs) {
		// pw.println(String.format(format, objs));
		// }
		//
		// public void setCoreSettings(Bundle coreSettings) {
		// queueOrSendMessage(H.SET_CORE_SETTINGS, coreSettings);
		// }
		//
		// public void updatePackageCompatibilityInfo(String pkg,
		// CompatibilityInfo info) {
		// UpdateCompatibilityData ucd = new UpdateCompatibilityData();
		// ucd.pkg = pkg;
		// ucd.info = info;
		// queueOrSendMessage(H.UPDATE_PACKAGE_COMPATIBILITY_INFO, ucd);
		// }
		//
		// public void scheduleTrimMemory(int level) {
		// queueOrSendMessage(H.TRIM_MEMORY, null, level);
		// }
	}

	private class H extends Handler {
		public static final int LAUNCH_ACTIVITY = 100;
		public static final int PAUSE_ACTIVITY = 101;
		public static final int PAUSE_ACTIVITY_FINISHING = 102;
		public static final int STOP_ACTIVITY_SHOW = 103;
		public static final int STOP_ACTIVITY_HIDE = 104;
		public static final int SHOW_WINDOW = 105;
		public static final int HIDE_WINDOW = 106;
		public static final int RESUME_ACTIVITY = 107;
		public static final int SEND_RESULT = 108;
		public static final int DESTROY_ACTIVITY = 109;
		public static final int BIND_APPLICATION = 110;
		public static final int EXIT_APPLICATION = 111;
		public static final int NEW_INTENT = 112;
		public static final int RECEIVER = 113;
		public static final int CREATE_SERVICE = 114;
		public static final int SERVICE_ARGS = 115;
		public static final int STOP_SERVICE = 116;
		public static final int REQUEST_THUMBNAIL = 117;
		public static final int CONFIGURATION_CHANGED = 118;
		public static final int CLEAN_UP_CONTEXT = 119;
		public static final int GC_WHEN_IDLE = 120;
		public static final int BIND_SERVICE = 121;
		public static final int UNBIND_SERVICE = 122;
		public static final int DUMP_SERVICE = 123;
		public static final int LOW_MEMORY = 124;
		public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
		public static final int RELAUNCH_ACTIVITY = 126;
		public static final int PROFILER_CONTROL = 127;
		public static final int CREATE_BACKUP_AGENT = 128;
		public static final int DESTROY_BACKUP_AGENT = 129;
		public static final int SUICIDE = 130;
		public static final int REMOVE_PROVIDER = 131;
		public static final int ENABLE_JIT = 132;
		public static final int DISPATCH_PACKAGE_BROADCAST = 133;
		public static final int SCHEDULE_CRASH = 134;
		public static final int DUMP_HEAP = 135;
		public static final int DUMP_ACTIVITY = 136;
		public static final int SLEEPING = 137;
		public static final int SET_CORE_SETTINGS = 138;
		public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;
		public static final int TRIM_MEMORY = 140;

		String codeToString(int code) {
			if (DEBUG_MESSAGES) {
				switch (code) {
				case LAUNCH_ACTIVITY:
					return "LAUNCH_ACTIVITY";
				case PAUSE_ACTIVITY:
					return "PAUSE_ACTIVITY";
				case PAUSE_ACTIVITY_FINISHING:
					return "PAUSE_ACTIVITY_FINISHING";
				case STOP_ACTIVITY_SHOW:
					return "STOP_ACTIVITY_SHOW";
				case STOP_ACTIVITY_HIDE:
					return "STOP_ACTIVITY_HIDE";
				case SHOW_WINDOW:
					return "SHOW_WINDOW";
				case HIDE_WINDOW:
					return "HIDE_WINDOW";
				case RESUME_ACTIVITY:
					return "RESUME_ACTIVITY";
				case SEND_RESULT:
					return "SEND_RESULT";
				case DESTROY_ACTIVITY:
					return "DESTROY_ACTIVITY";
				case BIND_APPLICATION:
					return "BIND_APPLICATION";
				case EXIT_APPLICATION:
					return "EXIT_APPLICATION";
				case NEW_INTENT:
					return "NEW_INTENT";
				case RECEIVER:
					return "RECEIVER";
				case CREATE_SERVICE:
					return "CREATE_SERVICE";
				case SERVICE_ARGS:
					return "SERVICE_ARGS";
				case STOP_SERVICE:
					return "STOP_SERVICE";
				case REQUEST_THUMBNAIL:
					return "REQUEST_THUMBNAIL";
				case CONFIGURATION_CHANGED:
					return "CONFIGURATION_CHANGED";
				case CLEAN_UP_CONTEXT:
					return "CLEAN_UP_CONTEXT";
				case GC_WHEN_IDLE:
					return "GC_WHEN_IDLE";
				case BIND_SERVICE:
					return "BIND_SERVICE";
				case UNBIND_SERVICE:
					return "UNBIND_SERVICE";
				case DUMP_SERVICE:
					return "DUMP_SERVICE";
				case LOW_MEMORY:
					return "LOW_MEMORY";
				case ACTIVITY_CONFIGURATION_CHANGED:
					return "ACTIVITY_CONFIGURATION_CHANGED";
				case RELAUNCH_ACTIVITY:
					return "RELAUNCH_ACTIVITY";
				case PROFILER_CONTROL:
					return "PROFILER_CONTROL";
				case CREATE_BACKUP_AGENT:
					return "CREATE_BACKUP_AGENT";
				case DESTROY_BACKUP_AGENT:
					return "DESTROY_BACKUP_AGENT";
				case SUICIDE:
					return "SUICIDE";
				case REMOVE_PROVIDER:
					return "REMOVE_PROVIDER";
				case ENABLE_JIT:
					return "ENABLE_JIT";
				case DISPATCH_PACKAGE_BROADCAST:
					return "DISPATCH_PACKAGE_BROADCAST";
				case SCHEDULE_CRASH:
					return "SCHEDULE_CRASH";
				case DUMP_HEAP:
					return "DUMP_HEAP";
				case DUMP_ACTIVITY:
					return "DUMP_ACTIVITY";
				case SLEEPING:
					return "SLEEPING";
				case SET_CORE_SETTINGS:
					return "SET_CORE_SETTINGS";
				case UPDATE_PACKAGE_COMPATIBILITY_INFO:
					return "UPDATE_PACKAGE_COMPATIBILITY_INFO";
				case TRIM_MEMORY:
					return "TRIM_MEMORY";
				}
			}
			return "(unknown)";
		}

		public void handleMessage(Message msg) {
			if (DEBUG_MESSAGES)
				logger.log(Level.INFO, TAG + ">>> handling: " + msg.what);
			switch (msg.what) {
			case LAUNCH_ACTIVITY: {
				// ActivityClientRecord r = (ActivityClientRecord) msg.obj;

				// r.packageInfo = getPackageInfoNoCheck(
				// r.activityInfo.applicationInfo, r.compatInfo);
				// TODO handleLaunchActivity(r, null);
			}
				break;
			// case RELAUNCH_ACTIVITY: {
			// ActivityClientRecord r = (ActivityClientRecord) msg.obj;
			// handleRelaunchActivity(r);
			// }
			// break;
			// case PAUSE_ACTIVITY:
			// handlePauseActivity((IBinder) msg.obj, false, msg.arg1 != 0,
			// msg.arg2);
			// maybeSnapshot();
			// break;
			// case PAUSE_ACTIVITY_FINISHING:
			// handlePauseActivity((IBinder) msg.obj, true, msg.arg1 != 0,
			// msg.arg2);
			// break;
			// case STOP_ACTIVITY_SHOW:
			// handleStopActivity((IBinder) msg.obj, true, msg.arg2);
			// break;
			// case STOP_ACTIVITY_HIDE:
			// handleStopActivity((IBinder) msg.obj, false, msg.arg2);
			// break;
			// case SHOW_WINDOW:
			// handleWindowVisibility((IBinder) msg.obj, true);
			// break;
			// case HIDE_WINDOW:
			// handleWindowVisibility((IBinder) msg.obj, false);
			// break;
			// case RESUME_ACTIVITY:
			// handleResumeActivity((IBinder) msg.obj, true, msg.arg1 != 0);
			// break;
			// case SEND_RESULT:
			// handleSendResult((ResultData) msg.obj);
			// break;
			// case DESTROY_ACTIVITY:
			// handleDestroyActivity((IBinder) msg.obj, msg.arg1 != 0,
			// msg.arg2, false);
			// break;
			// case BIND_APPLICATION:
			// AppBindData data = (AppBindData) msg.obj;
			// handleBindApplication(data);
			// break;
			// case EXIT_APPLICATION:
			// if (mInitialApplication != null) {
			// mInitialApplication.onTerminate();
			// }
			// Looper.myLooper().quit();
			// break;
			// case NEW_INTENT:
			// handleNewIntent((NewIntentData) msg.obj);
			// break;
			// case RECEIVER:
			// handleReceiver((ReceiverData) msg.obj);
			// maybeSnapshot();
			// break;
			// case CREATE_SERVICE:
			// handleCreateService((CreateServiceData) msg.obj);
			// break;
			// case BIND_SERVICE:
			// handleBindService((BindServiceData) msg.obj);
			// break;
			// case UNBIND_SERVICE:
			// handleUnbindService((BindServiceData) msg.obj);
			// break;
			// case SERVICE_ARGS:
			// handleServiceArgs((ServiceArgsData) msg.obj);
			// break;
			// case STOP_SERVICE:
			// handleStopService((IBinder) msg.obj);
			// maybeSnapshot();
			// break;
			// case REQUEST_THUMBNAIL:
			// handleRequestThumbnail((IBinder) msg.obj);
			// break;
			// case CONFIGURATION_CHANGED:
			// handleConfigurationChanged((Configuration) msg.obj, null);
			// break;
			// case CLEAN_UP_CONTEXT:
			// ContextCleanupInfo cci = (ContextCleanupInfo) msg.obj;
			// cci.context.performFinalCleanup(cci.who, cci.what);
			// break;
			// case GC_WHEN_IDLE:
			// scheduleGcIdler();
			// break;
			// case DUMP_SERVICE:
			// handleDumpService((DumpComponentInfo) msg.obj);
			// break;
			// case LOW_MEMORY:
			// handleLowMemory();
			// break;
			// case ACTIVITY_CONFIGURATION_CHANGED:
			// handleActivityConfigurationChanged((IBinder) msg.obj);
			// break;
			// case PROFILER_CONTROL:
			// handleProfilerControl(msg.arg1 != 0,
			// (ProfilerControlData) msg.obj, msg.arg2);
			// break;
			// case CREATE_BACKUP_AGENT:
			// handleCreateBackupAgent((CreateBackupAgentData) msg.obj);
			// break;
			// case DESTROY_BACKUP_AGENT:
			// handleDestroyBackupAgent((CreateBackupAgentData) msg.obj);
			// break;
			// case SUICIDE:
			// Process.killProcess(Process.myPid());
			// break;
			// case REMOVE_PROVIDER:
			// completeRemoveProvider((IContentProvider) msg.obj);
			// break;
			// case ENABLE_JIT:
			// ensureJitEnabled();
			// break;
			// case DISPATCH_PACKAGE_BROADCAST:
			// handleDispatchPackageBroadcast(msg.arg1, (String[]) msg.obj);
			// break;
			// case SCHEDULE_CRASH:
			// throw new RemoteServiceException((String) msg.obj);
			// case DUMP_HEAP:
			// handleDumpHeap(msg.arg1 != 0, (DumpHeapData) msg.obj);
			// break;
			// case DUMP_ACTIVITY:
			// handleDumpActivity((DumpComponentInfo) msg.obj);
			// break;
			// case SLEEPING:
			// handleSleeping((IBinder) msg.obj, msg.arg1 != 0);
			// break;
			// case SET_CORE_SETTINGS:
			// handleSetCoreSettings((Bundle) msg.obj);
			// break;
			// case UPDATE_PACKAGE_COMPATIBILITY_INFO:
			// handleUpdatePackageCompatibilityInfo((UpdateCompatibilityData)
			// msg.obj);
			// break;
			// case TRIM_MEMORY:
			// handleTrimMemory(msg.arg1);
			// break;
			}
			if (DEBUG_MESSAGES)
				logger.log(Level.INFO, TAG + "<<< done: " + msg.what);
		}
	}

	private Activity performLaunchActivity(ActivityClientRecord r,
			Intent customIntent) {
		return null;
		// return new DeadlockActivity();
	}

	private void handleLaunchActivity(ActivityClientRecord r,
			Intent customIntent) {
		// Activity a = performLaunchActivity(r, customIntent);
		//
		// handleResumeActivity(r.token, false, r.isForward);
		// System.out.println(Launching);

	}

	static final class ActivityClientRecord {
		// IBinder token;
		// int ident;
		// Intent intent;
		// Bundle state;
		// Activity activity;
		// Window window;
		// Activity parent;
		// String embeddedID;
		// Activity.NonConfigurationInstances lastNonConfigurationInstances;
		// boolean paused;
		// boolean stopped;
		// boolean hideForNow;
		// Configuration newConfig;
		// Configuration createdConfig;
		// ActivityClientRecord nextIdle;
		//
		// String profileFile;
		// ParcelFileDescriptor profileFd;
		// boolean autoStopProfiler;
		//
		// ActivityInfo activityInfo;
		// CompatibilityInfo compatInfo;
		// LoadedApk packageInfo;
		//
		// List<ResultInfo> pendingResults;
		// List<Intent> pendingIntents;
		//
		// boolean startsNotResumed;
		// boolean isForward;
		// int pendingConfigChanges;
		// boolean onlyLocalRequest;
		//
		// View mPendingRemoveWindow;
		// WindowManager mPendingRemoveWindowManager;

		ActivityClientRecord() {
			// parent = null;
			// embeddedID = null;
			// paused = false;
			// stopped = false;
			// hideForNow = false;
			// nextIdle = null;
		}

		public boolean isPreHoneycomb() {
			// if (activity != null) {
			// return activity.getApplicationInfo().targetSdkVersion
			// < android.os.Build.VERSION_CODES.HONEYCOMB;
			// }
			return false;
		}

		public String toString() {
			// ComponentName componentName = intent.getComponent();
			// return "ActivityRecord{"
			// + Integer.toHexString(System.identityHashCode(this))
			// + " token=" + token + " " + (componentName == null
			// ? "no component name" : componentName.toShortString())
			// + "}";
			return "";
		}
	}

	public static ActivityThread currentActivityThread() {
		return sThreadLocal.get();
	}

	ActivityThread(Activity activity) {
		this.currentActivity = activity;
		this.Activities.put(activity.getClass().getName(), activity);

	}

	private void attach() {
		sThreadLocal.set(this);

		// parse manifest.xml
		// Attach Application Handler
		// Attach ViewRootImpl Handler

	}

	public static void start(Activity activity) {
		Looper.prepare();
		ActivityThread thread = new ActivityThread(activity);
		thread.attach();
		// Handlers must be created here + first message added.

		thread.mAppThread.scheduleLaunchActivity("");
		Looper.loop();

		throw new RuntimeException("Main thread loop unexpectedly exited");
	}
}
package com.quick.liveswitcher.virtualscreen;

import static android.app.ActivityManager.RECENT_IGNORE_UNAVAILABLE;
import static android.app.ActivityTaskManager.getService;

import android.app.ActivityManager;
import android.app.IActivityTaskManager;
import android.app.StatusBarManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xtx on 2023/10/27.
 */
public class SystemApiUtil {
    private static final String TAG = SystemApiUtil.class.getSimpleName();
    public static final String ACTION_HIDE_NAVIGATION_BAR = "com.systemui.navigationbar.hide";
    public static final String ACTION_SHOW_NAVIGATION_BAR = "com.systemui.navigationbar.show";

    private static SystemApiUtil mInstance;
    private StatusBarManager mStatusBarManager;
    private ActivityManager mActivityManager;
    public static final String TASK_STATUS_TAG = "com.tripod.multiscreen.task";

    private IActivityTaskManager mActivityTaskManager;

    private SystemApiUtil() {

    }

    public static SystemApiUtil getInstance() {
        if (mInstance == null) {
            synchronized (SystemApiUtil.class) {
                if (mInstance == null) {
                    mInstance = new SystemApiUtil();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        mStatusBarManager = context.getSystemService(StatusBarManager.class);
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        IBinder b = ServiceManager.getService(Context.ACTIVITY_TASK_SERVICE);
        mActivityTaskManager = IActivityTaskManager.Stub.asInterface(b);
    }

    public void setNavigationBarStatus(Context context, boolean isShowNavigationBar) {
        try {
            Intent intent = new Intent(isShowNavigationBar ? ACTION_SHOW_NAVIGATION_BAR : ACTION_HIDE_NAVIGATION_BAR);
            context.sendBroadcast(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setStatusBarStatus(boolean isShowNavigationBar) {
        mStatusBarManager.disable(isShowNavigationBar ? StatusBarManager.DISABLE_NONE : StatusBarManager.DISABLE_EXPAND);
    }

    public void removeRecentTask(String packageName) {
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(50);
        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (packageName != null && task.baseActivity != null && task.baseActivity.getPackageName().equals(packageName)) {
                Log.i(TAG, "removeRecentTask packageName=" + packageName);
                removeTask(task.taskId);
                break;
            }
        }
    }

    public void removeTask(int taskId) {
        try {
            mActivityTaskManager.removeTask(taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeRecentTask(Context context) {
        List<String> packageNameList = SharedPreferencesUtil.getPackageNameList(context);
        if (packageNameList.size() == 0) {
            return;
        }

        try {
            ActivityManager  activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RecentTaskInfo> rawTasks = activityManager.getRecentTasks(ActivityManager.getMaxRecentTasksStatic(), RECENT_IGNORE_UNAVAILABLE);
            if (rawTasks == null || rawTasks.size() == 0) {
                Log.e(TAG, "tasks Empty");
                return;
            }

            List<ActivityManager.RecentTaskInfo> removeTaskList = new ArrayList<>();

            Iterator<ActivityManager.RecentTaskInfo> iter = rawTasks.iterator();
            while (iter.hasNext()) {
                ActivityManager.RecentTaskInfo t = iter.next();
                for (String s : packageNameList) {
                    if (s.equals(t.realActivity.getClassName()) || s.equals(t.realActivity.getPackageName())) {
                        removeTaskList.add(t);
                        Log.i(TAG, "package in save list, uid=" + t.id + " , package=" + s);
                        break;
                    }
                }
            }
            if (rawTasks.size() == 0) {
                Log.e(TAG, "tasks Empty");
                return;
            }

            if (removeTaskList.size() > 0) {
                Iterator<ActivityManager.RecentTaskInfo> iterator = removeTaskList.iterator();
                while (iterator.hasNext()) {
                    ActivityManager.RecentTaskInfo t = iterator.next();
                    getService().removeTask(t.taskId);
                    Log.i(TAG, "removeTask taskId: " + t.taskId + ", " + t.realActivity);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get recent tasks", e);
        }

        SharedPreferencesUtil.clearValues(context, packageNameList);

    }

    public void setSystemProperties(String status) {
        SystemProperties.set(TASK_STATUS_TAG, status);
    }
}

package com.quick.liveswitcher.virtualscreen;

import android.app.ActivityManager;
import android.app.IActivityTaskManager;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.hardware.display.VirtualDisplay;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.window.TaskSnapshot;

import java.util.List;


/**
 * Created by xtx on 2023/10/24.
 */
public class SystemApi {
    private static final String TAG = SystemApi.class.getSimpleName();
    private VirtualDisplay virtualDisplay;
    private ResolveInfo resolveInfo;
    private OnEventListener listener;
    private Context context;
    private ActivityManager mActivityManager;
    private TTaskStackListener taskStackListener;
    private IActivityTaskManager mActivityTaskManager;

    public SystemApi(Context context) {
        this.context = context;
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        IBinder b = ServiceManager.getService(Context.ACTIVITY_TASK_SERVICE);
        mActivityTaskManager = IActivityTaskManager.Stub.asInterface(b);
        try {
            taskStackListener = new TTaskStackListener();
            mActivityTaskManager.registerTaskStackListener(taskStackListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVirtualDisplay(VirtualDisplay virtualDisplay) {
        this.virtualDisplay = virtualDisplay;
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        this.resolveInfo = resolveInfo;
    }

    public void setOnEventListener(OnEventListener listener) {
        this.listener = listener;
    }

    public interface OnEventListener {
        void onDestroy();
    }

    public void removeRecentTask(String packageName) {
        SystemApiUtil.getInstance().removeRecentTask(packageName);
    }

    public void setDisplayId(MotionEvent motionEvent, int displayId) {
        motionEvent.setDisplayId(displayId);
    }

    public void injectInputEvent(MotionEvent newEvent) {
        InputManager.getInstance().injectInputEvent(newEvent, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    public void unregisterTaskStackListener() {
        try {
            mActivityTaskManager.unregisterTaskStackListener(taskStackListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class TTaskStackListener extends TaskStackListener {

        @Override
        public void onTaskStackChanged() throws RemoteException {
        }

        @Override
        public void onActivityPinned(String packageName, int userId, int taskId, int rootTaskId)
                throws RemoteException {
        }

        @Override
        public void onActivityUnpinned() throws RemoteException {
        }

        @Override
        public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo task, boolean homeTaskVisible,
                                             boolean clearedTask, boolean wasVisible) throws RemoteException {
        }

        @Override
        public void onActivityForcedResizable(String packageName, int taskId, int reason)
                throws RemoteException {
        }

        @Override
        public void onActivityDismissingDockedTask() throws RemoteException {
        }

        @Override
        public void onActivityLaunchOnSecondaryDisplayFailed(ActivityManager.RunningTaskInfo taskInfo,
                                                             int requestedDisplayId) throws RemoteException {
            onActivityLaunchOnSecondaryDisplayFailed();
        }

        @Override
        public void onActivityLaunchOnSecondaryDisplayFailed() throws RemoteException {
        }

        @Override
        public void onActivityLaunchOnSecondaryDisplayRerouted(ActivityManager.RunningTaskInfo taskInfo,
                                                               int requestedDisplayId) throws RemoteException {
        }

        @Override
        public void onTaskCreated(int taskId, ComponentName componentName) throws RemoteException {
            Log.i(TAG, "onTaskCreated taskId=" + taskId);
        }

        @Override
        public void onTaskRemoved(int taskId) throws RemoteException {
            Log.i(TAG, "onTaskRemoved taskId=" + taskId);
        }

        @Override
        public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo)
                throws RemoteException {
            Log.i(TAG, "onTaskMovedToFront");
            onTaskMovedToFront(taskInfo.taskId);
        }

        @Override
        public void onTaskMovedToFront(int taskId) throws RemoteException {
            Log.i(TAG, "onTaskMovedToFront taskId=" + taskId);
        }

        @Override
        public void onTaskRemovalStarted(ActivityManager.RunningTaskInfo taskInfo)
                throws RemoteException {
            Log.i(TAG, "onTaskRemovalStarted");
            onTaskRemovalStarted(taskInfo.taskId);
            if (String.valueOf(taskInfo).contains("displayId=0")) {
                return;
            }
            ComponentName component = taskInfo.baseIntent.getComponent();
            if (component != null && resolveInfo != null && component.getPackageName().equals(resolveInfo.activityInfo.packageName)) {
                if (listener != null) {
                    listener.onDestroy();
                }
            }
        }

        @Override
        public void onTaskRemovalStarted(int taskId) throws RemoteException {
            Log.i(TAG, "onTaskMovedToFront taskId=" + taskId);
        }

        @Override
        public void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo taskInfo)
                throws RemoteException {
            onTaskDescriptionChanged(taskInfo.taskId, taskInfo.taskDescription);
        }

        @Override
        public void onTaskDescriptionChanged(int taskId, ActivityManager.TaskDescription td)
                throws RemoteException {
        }

        @Override
        public void onActivityRequestedOrientationChanged(int taskId, int requestedOrientation)
                throws RemoteException {
        }

        @Override
        public void onTaskProfileLocked(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
            super.onTaskProfileLocked(taskInfo);
        }

        @Override
        public void onTaskSnapshotChanged(int taskId, TaskSnapshot snapshot) throws RemoteException {
            if (Binder.getCallingPid() != android.os.Process.myPid()
                    && snapshot != null && snapshot.getHardwareBuffer() != null) {
                // Preemptively clear any reference to the buffer
                snapshot.getHardwareBuffer().close();
            }
        }

        @Override
        public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo)
                throws RemoteException {
        }

        @Override
        public void onTaskDisplayChanged(int taskId, int newDisplayId) throws RemoteException {
            Log.i(TAG, "onTaskDisplayChanged taskId=" + taskId + ",newDisplayId=" + newDisplayId);
            if (newDisplayId == 0) {
                List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(50);
                for (ActivityManager.RunningTaskInfo task : tasks) {
                    if (taskId == task.taskId && virtualDisplay != null && task.baseActivity != null && resolveInfo != null
                            && task.baseActivity.getPackageName().equals(resolveInfo.activityInfo.packageName)) {
                        try {
                            Log.i(TAG, "onTaskDisplayChanged virtualDisplay DisplayId=" + virtualDisplay.getDisplay().getDisplayId());
                            mActivityTaskManager.moveRootTaskToDisplay(taskId, virtualDisplay.getDisplay().getDisplayId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        }

        @Override
        public void onRecentTaskListUpdated() throws RemoteException {
        }

        @Override
        public void onRecentTaskListFrozenChanged(boolean frozen) {
        }

        @Override
        public void onTaskFocusChanged(int taskId, boolean focused) {
            Log.i(TAG, "onTaskFocusChanged taskId=" + taskId + ",focused=" + focused);
        }

        @Override
        public void onTaskRequestedOrientationChanged(int taskId, int requestedOrientation) {
        }

        @Override
        public void onActivityRotation(int displayId) {
        }

        @Override
        public void onTaskMovedToBack(ActivityManager.RunningTaskInfo taskInfo) {
        }

        @Override
        public void onLockTaskModeChanged(int mode) {
        }
    }

}

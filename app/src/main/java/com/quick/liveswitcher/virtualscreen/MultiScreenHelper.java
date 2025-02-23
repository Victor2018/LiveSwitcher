package com.quick.liveswitcher.virtualscreen;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;

public class MultiScreenHelper {
    private static final String TAG = MultiScreenHelper.class.getSimpleName();
    private final Context context;
    private final ActivityManager mActivityManager;
    private DisplayManager mDisplayManager;
    private RemoveCallback mRemoveCallback;
    private ResolveInfo resolveInfo;
    private VirtualDisplay virtualDisplay;
    private SystemApi mSystemApi;
    private int mDisplayId;
    private int displayWidth;
    private int displayHeight;
    private Surface surface;
    private int mVirtualSurfaceWidth = 540;
    private int mVirtualSurfaceHeight = 960;
    private Size mPhysicsSurfaceViewSize = new Size(0, 0);
    private float baseWidthEventRatio = 1;
    private float baseHeightEventRatio = 1;
    private int mPhysicsSurfaceWidth = 540;
    private int mPhysicsSurfaceHeight = 960;

    public MultiScreenHelper(Context context, ResolveInfo resolveInfo, Surface surface, Size virtualSurfaceSize,Size physicsSurfaceSize, RemoveCallback removeCallback) {
        this.context = context;
        this.resolveInfo = resolveInfo;
        this.surface = surface;
        this.mRemoveCallback = removeCallback;
        this.mDisplayManager = context.getSystemService(DisplayManager.class);
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mSystemApi = new SystemApi(context);
        mSystemApi.setResolveInfo(resolveInfo);
        this.mVirtualSurfaceWidth = virtualSurfaceSize.getWidth();
        this.mVirtualSurfaceHeight = virtualSurfaceSize.getHeight();
        mPhysicsSurfaceViewSize = physicsSurfaceSize;
        mPhysicsSurfaceWidth = mPhysicsSurfaceViewSize.getWidth();
        mPhysicsSurfaceHeight = mPhysicsSurfaceViewSize.getHeight();
        //基础的点击事件映射 因为实际点击在屋里屏幕上面，需要映射到虚拟屏幕。
        baseWidthEventRatio = (float) mVirtualSurfaceWidth / mPhysicsSurfaceWidth;
        baseHeightEventRatio = (float) mVirtualSurfaceHeight / mPhysicsSurfaceHeight;
        initVirtualDisplay();
    }

    private void initVirtualDisplay() {
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE
                | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                | DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                | 1 << 7
                | 1 << 8
                | 1 << 6
                | 1 << 10
                | 1 << 11;
        virtualDisplay = mDisplayManager.createVirtualDisplay(
                "tripod@" + this.resolveInfo.activityInfo.packageName + "@0",
                mVirtualSurfaceWidth, mVirtualSurfaceHeight, this.context.getResources().getDisplayMetrics().densityDpi,
                null,
                flags);
        mSystemApi.setVirtualDisplay(virtualDisplay);
        mDisplayId = virtualDisplay.getDisplay().getDisplayId();
        try {
            virtualDisplay.setSurface(surface);
        } catch (Exception e) {
            MultiScreenHelper.this.destroy();
        }
        startActivity(context, resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name, virtualDisplay);
    }

    public void startActivity(Context context, String packageName, String name, VirtualDisplay virtualDisplay) {
        Log.d(TAG, "startActivity");
        Intent intent = new Intent();
        intent.setClassName(packageName, name);
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        intent.setFlags(402653184);
        makeBasic.setLaunchDisplayId(virtualDisplay.getDisplay().getDisplayId());
        context.startActivity(intent, makeBasic.toBundle());
    }

    public void handleTouch(MotionEvent motionEvent, RectF rect, RectF surfaceRect) {
        MotionEvent.PointerCoords[] pointerCoordsArr = new MotionEvent.PointerCoords[motionEvent.getPointerCount()];
        MotionEvent.PointerProperties[] pointerPropertiesArr = new MotionEvent.PointerProperties[motionEvent.getPointerCount()];

        int pointerCount = motionEvent.getPointerCount();

        final float[] xArray = new float[pointerCount];
        final float[] yArray = new float[pointerCount];

        Matrix2 m = new Matrix2();

        displayWidth = mVirtualSurfaceWidth;
        displayHeight = mVirtualSurfaceHeight;

        m.initTranslate(rect.left * displayWidth, rect.top * displayHeight);

        for (int i = 0; i < pointerCount; i++) {
            MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
            motionEvent.getPointerCoords(i, coords);
            double[] doubles = m.globalToLocal(coords.x, coords.y);

            xArray[i] = (float) doubles[0];
            yArray[i] = (float) doubles[1];
        }

        for (int i = 0; i < pointerCount; i++) {
            MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
            MotionEvent.PointerProperties pointerProperties = new MotionEvent.PointerProperties();
            motionEvent.getPointerCoords(i, pointerCoords);
            motionEvent.getPointerProperties(i, pointerProperties);
            pointerCoordsArr[i] = pointerCoords;
            MotionEvent.PointerCoords pointerCoords2 = pointerCoordsArr[i];

            Log.d("tag","surfaceRect:"+surfaceRect);
            Log.d("tag","xArray[i]:" + xArray[i]);

            //xArray[i] yArray[i]手点击的点与物理surface的坐标
            //xArray[i]* baseWidthEventRatio yArray[i]* baseHeightEventRatio 手点击的点与物理surface的坐标对应映射的坐标
            //必须先计算出点对应应用内边框的比例
            float appX =  mPhysicsSurfaceWidth * surfaceRect.left;
            float appY =  mPhysicsSurfaceHeight * surfaceRect.top;
            float appWidth =  mPhysicsSurfaceWidth * surfaceRect.right - mPhysicsSurfaceWidth * surfaceRect.left;
            float appHeight = mPhysicsSurfaceHeight * surfaceRect.bottom - mPhysicsSurfaceHeight * surfaceRect.top;
            float touchAppXRatio = ((xArray[i]-appX)/appWidth);
            float touchAppYRatio = ((yArray[i]-appY)/appHeight);

            //通过比例映射坐标点
            pointerCoords2.x = mVirtualSurfaceWidth * touchAppXRatio;
            pointerCoords2.y = mVirtualSurfaceHeight * touchAppYRatio;
            pointerPropertiesArr[i] = pointerProperties;
        }

        MotionEvent newEvent = MotionEvent.obtain(motionEvent.getDownTime(),
                motionEvent.getEventTime(), motionEvent.getAction(),
                motionEvent.getPointerCount(), pointerPropertiesArr, pointerCoordsArr,
                motionEvent.getMetaState(), motionEvent.getButtonState(),
                motionEvent.getXPrecision(), motionEvent.getYPrecision(),
                motionEvent.getDeviceId(), motionEvent.getEdgeFlags(),
                motionEvent.getSource(), motionEvent.getFlags());

        mSystemApi.setDisplayId(newEvent, mDisplayId);
        mSystemApi.injectInputEvent(newEvent);
        newEvent.recycle();
    }

    public void destroy() {
        Log.d(TAG, "destroy");
        if (resolveInfo != null) {
            String packageName = resolveInfo.activityInfo.packageName;
            mActivityManager.killBackgroundProcesses(packageName);
            mActivityManager.forceStopPackage(packageName);
            mSystemApi.removeRecentTask(packageName);
        }

        if (virtualDisplay != null) {
            if (virtualDisplay.getSurface() != null) {
                virtualDisplay.getSurface().release();
            }
            virtualDisplay.release();
        }

        if (mRemoveCallback != null) {
            mRemoveCallback.onRemove();
        }
        mSystemApi.unregisterTaskStackListener();
    }

    public interface RemoveCallback {
        void onRemove();
    }

}

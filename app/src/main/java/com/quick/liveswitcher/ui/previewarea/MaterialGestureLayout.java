package com.quick.liveswitcher.ui.previewarea;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.quick.liveswitcher.R;
import com.quick.liveswitcher.constants.AppConfig;
import com.tripod.daobotai.virtualscreen.VirtualScreenHelper;

import sdk.smartx.director.layer.IRenderLayer;


public class MaterialGestureLayout extends ControlLayout {
    private static final String TAG = "MaterialControlLayout";

    private int scaleState = SCALE_STATE_READY;
    public static final int SCALE_STATE_START = 0;
    public static final int SCALE_STATE_END = 1;
    public static final int SCALE_STATE_READY = 2;

    private float scaleRatioX, scaleRatioY;

    private int currentLayerId = -1;
    private int currentMode = COMMON_MODE;
    private static final int COMMON_MODE = 0;
    private static final int EDIT_MODE = 1;

    private int touchAction;
    public static final int TOUCH_SCALE_TOP_LEFT = ControlOrientation.TOP_LEFT.index;
    public static final int TOUCH_SCALE_TOP_RIGHT = ControlOrientation.TOP_RIGHT.index;
    public static final int TOUCH_SCALE_BOTTOM_LEFT = ControlOrientation.BOTTOM_LEFT.index;
    public static final int TOUCH_SCALE_BOTTOM_RIGHT = ControlOrientation.BOTTOM_RIGHT.index;
    public static final int TOUCH_SCALE_BOTTOM_CENTER = ControlOrientation.BOTTOM_CENTER.index;
    public static final int TOUCH_SCALE_TOP_CENTER = ControlOrientation.TOP_CENTER.index;
    public static final int TOUCH_SCALE_LEFT_CENTER = ControlOrientation.CENTER_LEFT.index;
    public static final int TOUCH_SCALE_RIGHT_CENTER = ControlOrientation.CENTER_RIGHT.index;
    public static final int TOUCH_DONE = 1;
    public static final int TOUCH_STARTED = 2;
    public static final int TOUCH_MOVE = 6;
    public static final int TOUCH_OUTSIDE_CANCEL = 7;
    public static final int TOUCH_INVALID = 8;
    public static final int TOUCH_OTHER = 9;

    private int mGap = 5;
    private int touchSlop;
    private float pointRadius = 8f;
    private int minSize = 50;
    private Paint pointPaint;
    private Paint borderPaint;
    private RectF touchableRect = new RectF();
    private RectF rectF = new RectF();
    private float[] points = new float[16];
    private RectF operationRect = new RectF();
    private float startX = -1f;
    private float startY = -1f;
    private OnPositionChangedListener onPositionChangedListener;
    // 可拖拽区域
    private RectF draggableRect = new RectF();
    private Paint fillPaint;

    private RectF resultRect = new RectF();

    private Bitmap centerIcon;
    private RectF rAdd = new RectF();
    private Rect iconRect = new Rect();
    private Paint bitmapPaint;
    // rect变换组件
    private ControlPointer controlPointer;
    // 缩放手势
    private ScaleGestureDetector detector;

    private boolean isEditEnable = true;
    private boolean resizeEnable = true;
    private int strokeColor = R.color.white;
    private boolean touchOutsideEnable = true;
    private float minSizeRatio = 0.1f;
    private RectF rScaleBL, rScaleBR, rScaleTR, rScaleTL, rScaleTC, rScaleLC, rScaleBC, rScaleRC;
    private RectF rScaleBLToDraw, rScaleBRToDraw, rScaleTRToDraw, rScaleTLToDraw;
    private boolean keepRatio = true;
    private int cornerTouchSize;
    private int cornerIconSize;
    private long downTime = -1L;

    private Runnable mClearTask;
    private Handler mHandler;
    private final int mDelay = 1 * 1000;

    private final boolean DEBUG = true;

    public MaterialGestureLayout(Context context) {
        super(context);
        init(context);
    }

    public MaterialGestureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialGestureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private OutCallabck mCallback;

    public interface OutCallabck {
        boolean isLayerVisibleById(int layerId);

        RectF getLayerRectF(int layerId);

        int findClosestCoordLayer(float x, float y);

        IRenderLayer getLayerById(int layerId);
    }

    public void setOutCallback(OutCallabck call) {
        mCallback = call;
    }

    public void updateData(int layerId, boolean indexChange) {
        if (currentMode == EDIT_MODE) {
            if (layerId == currentLayerId) {
                if (mCallback != null /*&& !mCallback.isLayerVisibleById(currentLayerId)*/) {
                    exitEditMode();
                }
            } else {
                enterEditMode(layerId);
            }
        } else if (indexChange && layerId != -1) {
            //进入编辑模式，初始化所有rect数据, 同时开启倒计时//
            enterEditMode(layerId);
        }
    }

    private void exitEditMode() {
        currentMode = COMMON_MODE;
        currentLayerId = -1;
        mHandler.removeCallbacks(mClearTask);
        detectTouchRegions(new RectF(0f, 0f, 1.0f, 1.0f));
        synChildPos(false);
        postInvalidate();
    }

    private void enterEditMode(int layerId) {
        currentMode = EDIT_MODE;
        currentLayerId = layerId;
        mHandler.removeCallbacks(mClearTask);
        Log.d(TAG, "postDelayed mode:" + currentMode + ",touchAction:" + touchAction);
        mHandler.postDelayed(mClearTask, mDelay);
        if (mCallback != null) {
            RectF rectF = mCallback.getLayerRectF(layerId);
            detectTouchRegions(rectF);
        }

        synChildPos(false);
        postInvalidate();
    }

    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        this.onPositionChangedListener = onPositionChangedListener;
    }

    public void setResizeEnable(boolean resizeEnable) {
        this.resizeEnable = resizeEnable;
    }

    // 设置是否支持编辑功能
    public void setEditEnable(boolean editEnable) {
        this.isEditEnable = editEnable;
    }

    /**
     * 设置是否支持点击外部取消编辑功能
     *
     * @param touchOutsideEnable
     */
    public void setTouchOutsideEnable(boolean touchOutsideEnable) {
        this.touchOutsideEnable = touchOutsideEnable;
    }

    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap
     */
    public static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 设置最小缩放尺寸
     *
     * @param minSizeRatio
     */
    public void setMinSizeRatio(float minSizeRatio) {
        this.minSizeRatio = minSizeRatio;
    }

    void init(Context context) {
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(ResourcesCompat.getColor(context.getResources(), R.color.white, null));

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3);
        borderPaint.setColor(ResourcesCompat.getColor(context.getResources(), strokeColor, null));

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.TRANSPARENT);
        fillPaint.setAntiAlias(true);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Drawable addDrawable = ContextCompat.getDrawable(getContext(), R.drawable.sxs_ic_done_edit_material);
        centerIcon = drawable2Bitmap(addDrawable);

        cornerTouchSize = context.getResources().getDimensionPixelSize(R.dimen.dp_24);
        cornerIconSize = context.getResources().getDimensionPixelSize(R.dimen.dp_8);
        pointRadius = context.getResources().getDimensionPixelSize(R.dimen.dp_3);
        mGap = context.getResources().getDimensionPixelSize(R.dimen.dp_10);

        rScaleBL = new RectF();
        rScaleTR = new RectF();
        rScaleBR = new RectF();
        rScaleTL = new RectF();

        rScaleTC = new RectF();
        rScaleBC = new RectF();
        rScaleRC = new RectF();
        rScaleLC = new RectF();

        rScaleBRToDraw = new RectF();
        rScaleTRToDraw = new RectF();
        rScaleBLToDraw = new RectF();
        rScaleTLToDraw = new RectF();

        controlPointer = new ControlPointer();

        detector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                //todo xkj 缩小图片的时候不灵敏 是因为没调用这个onScale方法 但是detector.onTouchEvent(event)已调用
                Log.d("tag", "detector.scaleFactor:" + detector.getScaleFactor());
                float centerX = rectF.centerX();
                float centerY = rectF.centerY();
                float width = rectF.width();
                float height = rectF.height();
                width *= detector.getScaleFactor();
                height *= detector.getScaleFactor();
                if (Math.min(width, height) < minSize) {
                    return true;
                }
                if (width > getWidth() * 8.6) {
                    return true;
                }
                if (height > getHeight() * 8.6) {
                    return true;
                }
                float left = detector.getFocusX() - width * scaleRatioX;
                float top = detector.getFocusY() - height * scaleRatioY;
                //rectF.set(centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2);
                rectF.set(left, top, left + width, top + height);

                handleBoundary();
                detectPoints();
                synChildPos(true);
                invalidate();

                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                scaleState = SCALE_STATE_START;
                //两点之间的中点位置不变原理//
                scaleRatioX = (detector.getFocusX() - rectF.left) / rectF.width();
                scaleRatioY = (detector.getFocusY() - rectF.top) / rectF.height();
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                scaleState = SCALE_STATE_END;
            }
        });
        mHandler = new Handler(Looper.getMainLooper());
        mClearTask = new Runnable() {
            @Override
            public void run() {
                if (DEBUG)
                    Log.d(TAG, "clearTask mode:" + currentMode + ",touchAction:" + touchAction);
                // 直播机不自动清除选中状态
//                onFinished(false);

                if (onPositionChangedListener != null) {
                    onPositionChangedListener.onTimerEnd();
                }
            }
        };
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //only play click sound effect//
            }
        });
    }

    public void forceFinish() {
        mHandler.post(mClearTask);
    }

    public void setKeepRatio(boolean keepRatio) {
        this.keepRatio = keepRatio;
    }

    public void synChildPos(boolean dataChange) {
        float x = rectF.left;
        float y = rectF.top;
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float width = rectF.width();
        float height = rectF.height();

        float rX = x / viewWidth;
        float rY = y / viewHeight;
        float rW = width / viewWidth;
        float rH = height / viewHeight;

/*        if (rW > 1) rW = 1;
        if (rW < 0) rW = 0;
        if (rH > 1) rH = 1;
        if (rH < 0) rH = 0;

        if (rX > 1.0 - 0.5*rW) rX = 1.0f - 0.5f*rW;
        if (rY > 1.0 - 0.5*rH) rY = 1.0f - 0.5f*rH;

        if (rX < -0.5f*rW) rX = -0.5f*rW;
        if (rY < -0.5f*rH) rY = -0.5f*rH;*/

        resultRect.set(rX, rY, rX + rW, rY + rH);
        rectF.set(rX * viewWidth, rY * viewHeight, (rX + rW) * viewWidth, (rY + rH) * viewHeight);

        if (DEBUG) Log.d(TAG, "resultRect:" + resultRect);

        if (dataChange && onPositionChangedListener != null && currentLayerId != -1) {
            if (currentLayerId != -1) {
                onPositionChangedListener.onDataChanged(currentLayerId, resultRect);
            }
        }
    }

    /**
     * 更新触摸区域
     *
     * @param rec
     */
    private void detectTouchRegions(RectF rec) {
        // 这里是复制了一份对象
        this.rectF.set(rec.left * getWidth(), rec.top * getHeight(), rec.right * getWidth(), rec.bottom * getHeight());
        detectPoints();

        touchableRect.set(rectF.left - pointRadius * 2, rectF.top - pointRadius * 2, rectF.right + pointRadius * 2, rectF.bottom + pointRadius * 2);
        draggableRect.set(rectF.left + pointRadius * 2, rectF.top + pointRadius * 2, rectF.right - pointRadius * 2, rectF.bottom - pointRadius * 2 + 50);

        if (draggableRect.width() <= minSize) {
            float delta = minSize - draggableRect.width();
            draggableRect.left -= delta / 2;
            draggableRect.right += delta / 2;
        }
        if (draggableRect.height() <= minSize) {
            float delta = minSize - draggableRect.height();
            draggableRect.top -= delta / 2;
            draggableRect.bottom += delta / 2;
        }
        // 设置一个缓冲的边缘，避免误操作导致保存动作触发。
        operationRect.set(draggableRect.left - 30, draggableRect.top - 30, draggableRect.right + 30, draggableRect.bottom + 30);
    }

    private void detectPoints() {
        points[0] = rectF.left;
        points[1] = rectF.top;

        points[2] = rectF.centerX();
        points[3] = rectF.top + 1;

        points[4] = rectF.right;
        points[5] = rectF.top;

        points[6] = rectF.left + 1;
        points[7] = rectF.centerY();

        points[8] = rectF.right - 1.5f;
        points[9] = rectF.centerY();

        points[10] = rectF.left;
        points[11] = rectF.bottom;

        points[12] = rectF.centerX();
        points[13] = rectF.bottom - 1;

        points[14] = rectF.right;
        points[15] = rectF.bottom;

        float l0 = points[0] - (cornerTouchSize >> 1);
        float t0 = points[1] - (cornerTouchSize >> 1);

        float l01 = points[0] - (cornerIconSize >> 1);
        float t01 = points[1] - (cornerIconSize >> 1);

        rScaleTL.set(l0 - cornerIconSize, t0 - cornerIconSize, l0 + cornerTouchSize + cornerIconSize, t0 + cornerTouchSize + cornerIconSize);
        rScaleTLToDraw.set(l01 - cornerIconSize, t01 - cornerIconSize, l01 + cornerIconSize + cornerIconSize, t01 + cornerIconSize + cornerIconSize);

        rScaleTC.set(points[2] - (cornerTouchSize >> 1), points[3] - (cornerTouchSize >> 1), points[2] + (cornerTouchSize >> 1), points[3] + (cornerTouchSize >> 1));
        rScaleRC.set(points[8] - (cornerTouchSize >> 1), points[9] - (cornerTouchSize >> 1), points[8] + (cornerTouchSize >> 1), points[9] + (cornerTouchSize >> 1));
        rScaleLC.set(points[6] - (cornerTouchSize >> 1), points[7] - (cornerTouchSize >> 1), points[6] + (cornerTouchSize >> 1), points[7] + (cornerTouchSize >> 1));
        rScaleBC.set(points[12] - (cornerTouchSize >> 1), points[13] - (cornerTouchSize >> 1), points[12] + (cornerTouchSize >> 1), points[13] + (cornerTouchSize >> 1));

        float l1 = points[10] - (cornerTouchSize >> 1);
        float t1 = points[11] - (cornerTouchSize >> 1);

        float l11 = points[10] - (cornerIconSize >> 1);
        float t11 = points[11] - (cornerIconSize >> 1);

        rScaleBL.set(l1 - cornerIconSize, t1 - cornerIconSize, cornerTouchSize + l1 + cornerIconSize, t1 + cornerTouchSize + cornerIconSize);
        rScaleBLToDraw.set(l11 - cornerIconSize, t11 - cornerIconSize, cornerIconSize + l11 + cornerIconSize, t11 + cornerIconSize + cornerIconSize);

        float l2 = points[4] - (cornerTouchSize >> 1);
        float t2 = points[5] - (cornerTouchSize >> 1);

        float l21 = points[4] - (cornerIconSize >> 1);
        float t21 = points[5] - (cornerIconSize >> 1);

        rScaleTR.set(l2 - cornerIconSize, t2 - cornerIconSize, cornerTouchSize + l2 + cornerIconSize, t2 + cornerTouchSize + cornerIconSize);
        rScaleTRToDraw.set(l21 - cornerIconSize, t21 - cornerIconSize, cornerIconSize + l21 + cornerIconSize, cornerIconSize + t21 + cornerIconSize);

        float l3 = points[14] - (cornerTouchSize >> 1);
        float t3 = points[15] - (cornerTouchSize >> 1);

        float l31 = points[14] - (cornerIconSize >> 1);
        float t31 = points[15] - (cornerIconSize >> 1);

        rScaleBR.set(l3 - cornerIconSize, t3 - cornerIconSize, cornerTouchSize + l3 + cornerIconSize, t3 + cornerTouchSize + cornerIconSize);
        rScaleBRToDraw.set(l31 - cornerIconSize, t31 - cornerIconSize, cornerIconSize + l31 + cornerIconSize, +cornerIconSize + t31 + cornerIconSize);

        if (centerIcon != null) {
            float w = rectF.width() * 0.15f;
            float h = rectF.height() * 0.10f;
            float size = Math.min(w, h);
            if (size < minSize) size = minSize;
            float lAdd = rectF.left + (rectF.width() / 2 - size / 2);
            float tAdd = rectF.top + (rectF.height() / 2 - size / 2);
            float rAdd = lAdd + size;
            float bAdd = tAdd + size;
            // 设置点击按钮的rect区域
            this.rAdd.set(lAdd, tAdd, rAdd, bAdd);
            iconRect.set(-4, -4, centerIcon.getWidth() + 4, centerIcon.getHeight() + 4);
        }
    }

    //判断当前是否点在可拖动的几个点范围中（边框四周的点）
    private int getTouchAction(MotionEvent event) {
        MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
        event.getPointerCoords(0, pointerCoords);

        if (rScaleBL.contains(pointerCoords.x, pointerCoords.y)) {
            return TOUCH_SCALE_BOTTOM_LEFT;
        }
        if (rScaleBR.contains(pointerCoords.x, pointerCoords.y)) {
            return TOUCH_SCALE_BOTTOM_RIGHT;
        }

        if (rScaleTL.contains(pointerCoords.x, pointerCoords.y)) {
            return TOUCH_SCALE_TOP_LEFT;
        }

        if (rScaleTR.contains(pointerCoords.x, pointerCoords.y)) {
            return TOUCH_SCALE_TOP_RIGHT;
        }
        if (rScaleTC.contains(pointerCoords.x, pointerCoords.y)) {
            return TOUCH_SCALE_TOP_CENTER;
        }
        if (rScaleRC.contains(pointerCoords.x, pointerCoords.y)) {
            return TOUCH_SCALE_RIGHT_CENTER;
        }
        if (rScaleLC.contains(pointerCoords.x, pointerCoords.y)) {
            return TOUCH_SCALE_LEFT_CENTER;
        }
        if (rScaleBC.contains(pointerCoords.x, pointerCoords.y)) {
            return TOUCH_SCALE_BOTTOM_CENTER;
        }

        if (currentMode == EDIT_MODE) {
            if (rAdd.contains(pointerCoords.x, pointerCoords.y)) {
                return TOUCH_DONE;
            }
            if (mCallback != null) {
                int layerId = mCallback.findClosestCoordLayer(pointerCoords.x / getWidth(), pointerCoords.y / getHeight());
                //优先顶面的图层//
                if (layerId != -1 && layerId != currentLayerId) {
                    return TOUCH_OTHER;
                }
            }
        }
        if (draggableRect.contains(pointerCoords.x, pointerCoords.y)) {
            // 由于rAdd实际上包裹在可拖拽范围draggableRect内部，所以要在这里判断。
            if (rAdd.contains(pointerCoords.x, pointerCoords.y)) {
                return TOUCH_DONE;
            }
            /* else if (fitOtherOption(pointerCoords.x, pointerCoords.y, false)) {
                return TOUCH_OTHER;
            }*/
            return TOUCH_MOVE;
        }

        if (!operationRect.contains(pointerCoords.x, pointerCoords.y)) {
            //if (fitOtherOption(pointerCoords.x, pointerCoords.y, true)) return TOUCH_OTHER;
            //SceneBean.LayerBean bean = mScene.findClosestCoordLayer(pointerCoords.x / getWidth(), pointerCoords.y / getHeight());
            int layerId = -1;
            if (mCallback != null)
                layerId = mCallback.findClosestCoordLayer(pointerCoords.x / getWidth(), pointerCoords.y / getHeight());
            if (layerId == -1) return TOUCH_OUTSIDE_CANCEL;
            return TOUCH_OTHER;
        }

        return TOUCH_INVALID;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //int width = getWidth();
        //int height = getHeight();
/*        float left = 0;//width;// * data.left;
        float right = left + width;// * data.width();
        float top = 0;//height * data.top;
        float bottom = top + height;// * data.height();
        detectTouchRegions(new RectF(left, top, right, bottom));*/
        minSize = (int) (getWidth() * minSizeRatio);
        mGap = (int) (getWidth() * 0.06f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: " + w + "x" + h + "," + oldw + "x" + oldh);
    }

    public void delayClearTask() {
        if (mHandler != null && currentMode == EDIT_MODE) {
            mHandler.removeCallbacks(mClearTask);
            mHandler.postDelayed(mClearTask, mDelay);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isEditEnable) {
            return super.dispatchTouchEvent(event);
        }
        if (currentMode == EDIT_MODE) {
            mHandler.removeCallbacks(mClearTask);
            mHandler.postDelayed(mClearTask, mDelay);
            //多指缩放//
            if (event.getPointerCount() >= 2 && resizeEnable) {
                return detector.onTouchEvent(event);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isAppMode = false;
    private IRenderLayer currentAppLayer = null;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEditEnable) {
            return true;
        }
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN && isAppMode) {
            //如果当前是点击的应用 则把全部事件给应用
            VirtualScreenHelper.Companion.getInstance().getMultiScreenHelper().handleTouch(event, new RectF(0F, 0f, 1f, 1f), currentAppLayer.getRect());

        } else {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    int actionIndex = event.getActionIndex();
                    int firstPointerId = event.getPointerId(actionIndex);
                    int index = event.findPointerIndex(firstPointerId);
                    startX = event.getX(index);
                    startY = event.getY(index);

                    //xkj 判断当前是否点击的是应用显示图层
                    int layerIdStart = -1;
                    if (mCallback != null) {
                        layerIdStart = mCallback.findClosestCoordLayer(startX / getWidth(), startY / getHeight());
                        currentAppLayer = mCallback.getLayerById(layerIdStart);
                        if (currentAppLayer != null && currentAppLayer.getConfig().getType() == sdk.smartx.director.layer.LayerType.Surface) {
                            isAppMode = true;
                            onFinished(true); //xkj 清除以前的选中
                            VirtualScreenHelper.Companion.getInstance().getMultiScreenHelper().handleTouch(event, new RectF(0F, 0f, 1f, 1f), currentAppLayer.getRect());
                            //如果是app图层，点击的时候就通知选中
                            if (onPositionChangedListener != null) {
                                onPositionChangedListener.onSelected(layerIdStart);
                            }
                            break;
                        } else {
                            isAppMode = false;
                        }
                    } else {
                        isAppMode = false;
                    }

                    controlPointer.resetAttachEdge();
                    if (currentMode == COMMON_MODE) {
                        touchAction = TOUCH_STARTED;
                    } else {
                        if (scaleState == SCALE_STATE_END) {
                            scaleState = SCALE_STATE_READY;
                        }
                        //判断当前是否点在可拖动的几个点范围中（边框四周的点）
                        touchAction = getTouchAction(event);
                        Log.d("tag", "touchAction:" + touchAction);
                    }
                    // 不可修改尺寸的情况下，强制改为move
                    if (!resizeEnable && touchAction >= 100) {
                        touchAction = TOUCH_MOVE;
                    }
                    if (touchAction == TOUCH_INVALID) return false;
                    downTime = System.currentTimeMillis();
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (touchAction == TOUCH_STARTED) {
                        return true;
                    }
                    if (scaleState == SCALE_STATE_START || scaleState == SCALE_STATE_END) {
                        return true;
                    }
                    float endX = event.getX();
                    float endY = event.getY();
                    float disX = endX - startX;
                    float disY = endY - startY;

                    if (touchAction == TOUCH_MOVE) {
                        controlPointer.move(rectF, disX, disY, this);
                    } else if (touchAction >= 100) {
                        if (keepRatio) {
                            controlPointer.ratioDrag(rectF, disX, disY, touchAction);
                        } else {
                            controlPointer.drag(rectF, disX, disY, ControlOrientation.find(touchAction));
                        }
                    }
                    handleBoundary();
                    detectPoints();
                    synChildPos(true);
                    invalidate();
                    startY = endY;
                    startX = endX;
                    break;
                case MotionEvent.ACTION_UP:
                    float x = event.getX();
                    float y = event.getY();
                    if (touchAction == TOUCH_STARTED) {
                        if (Math.abs(x - startX) > touchSlop || Math.abs(y - startY) > touchSlop) {
                            startX = -1;
                            startY = -1;
                            touchAction = TOUCH_INVALID;
                            break;
                        } else {
                            int layerId = -1;
                            if (mCallback != null)
                                layerId = mCallback.findClosestCoordLayer(x / getWidth(), y / getHeight());
                            if (layerId == -1) {
                                touchAction = TOUCH_INVALID;
                                break;
                            }

                            //进入编辑模式，初始化所有rect数据, 同时开启倒计时//
                            if (onPositionChangedListener != null) {
                                onPositionChangedListener.onSelected(layerId);
                            }
                            enterEditMode(layerId);
                            performClick();
                            postInvalidate();
                            startX = -1;
                            startY = -1;
                            scaleState = SCALE_STATE_READY;
                            touchAction = TOUCH_INVALID;
                            break;
                        }
                    }
                    if (touchAction == TOUCH_DONE && rAdd.contains(x, y)) {
                        long upTime = System.currentTimeMillis();
                        if (upTime - downTime <= 500) {
                            performClick();
                            onFinished(true);
                        }
                    } else if (touchAction == TOUCH_OUTSIDE_CANCEL && touchOutsideEnable) {
                        long upTime = System.currentTimeMillis();
                        if (upTime - downTime <= 500) {
                            onFinished(true); //xkj 点击屏幕空白地方 清除以前的选中
                            if (onPositionChangedListener != null) {
                                onPositionChangedListener.onTouchOutside();
                            }
                        }
                    } else if (touchAction == TOUCH_OTHER) {
                        if (Math.abs(x - startX) <= touchSlop && Math.abs(y - startY) <= touchSlop) {
                            //SceneBean.LayerBean bean = mScene.findClosestCoordLayer(x / getWidth(), y / getHeight());
                            int layerId = -1;//
                            if (mCallback != null)
                                layerId = mCallback.findClosestCoordLayer(x / getWidth(), y / getHeight());
                            if (layerId == -1) {
                                startX = -1;
                                startY = -1;
                                touchAction = TOUCH_INVALID;
                                break;
                            }
                            //进入编辑模式，初始化所有rect数据, 同时开启倒计时//
                            if (onPositionChangedListener != null) {
                                onPositionChangedListener.onSelected(layerId);
                            }
                            enterEditMode(layerId);
                            performClick();
                            postInvalidate();
                            startX = -1;
                            startY = -1;
                            scaleState = SCALE_STATE_READY;
                            touchAction = TOUCH_INVALID;
                            break;
                        } else {
                            startX = -1;
                            startY = -1;
                            scaleState = SCALE_STATE_READY;
                            touchAction = TOUCH_INVALID;
                        }
                    }
                    startX = -1;
                    startY = -1;
                    break;
            }

        }
        if (DEBUG)
            Log.d(TAG, "onTouchEvent touchAction:" + touchAction + ",scaleState:" + scaleState);
        return true;
    }

    void onFinished(boolean fromUser) {
        if (onPositionChangedListener != null && currentLayerId != -1) {
            if (currentLayerId != -1) {
                onPositionChangedListener.onFinish(currentLayerId, new RectF(resultRect));
            }
        }
        exitEditMode();
        updateData(currentLayerId, false);
    }

    private void handleBoundary() {
/*        View view = this;
        //范围限制//
        float width = rectF.width();
        float height = rectF.height();
        if (rectF.right > view.getWidth() + 0.5f*width) {
            float dis = rectF.right - (view.getWidth() + 0.5f*width);
            rectF.offset(-dis, 0);
        }
        if (rectF.bottom > view.getHeight() + 0.5f*height) {
            float dis = rectF.bottom - (view.getHeight() + 0.5f*height);
            rectF.offset(0, -dis);
        }

        if (rectF.left < -0.5f*width) {
            float dis = -0.5f*width - rectF.left;
            rectF.offset(dis, 0);
        }
        if (rectF.top < -0.5f*height) {
            float dis = -0.5f*height - rectF.top;
            rectF.offset(0, dis);
        }*/
    }

    public void setStrokeColor(int color) {
        strokeColor = color;
        borderPaint.setColor(ResourcesCompat.getColor(getResources(), strokeColor, null));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentMode == COMMON_MODE || (rectF.width() < 1 && rectF.height() < 1)) {
            return;
        }
        canvas.drawRect(rectF.left + 2, rectF.top + 2, rectF.right - 2, rectF.bottom - 2, borderPaint);
        canvas.drawRect(rectF, fillPaint);

        drawPoints(canvas);
        if (centerIcon != null && (rectF.width() > rAdd.width() && rectF.height() > rAdd.height())) {
            //Log.d(TAG, "draw:" + rAdd.left + "-" + rAdd.top + "," + rAdd.width() + "x" + rAdd.height() + "  ,rect:" + rectF.left + "-" + rectF.top + "," + rectF.width() + "x" + rectF.height());
//  xkj 去掉中间的对号icon
//            canvas.drawBitmap(centerIcon, iconRect, rAdd, bitmapPaint);
            //canvas.drawRect(rAdd.left + 1, rAdd.top + 1, rAdd.right - 1, rAdd.bottom - 1, borderPaint);
        }

        touchableRect.set(rectF.left - pointRadius * 2, rectF.top - pointRadius * 2, rectF.right + pointRadius * 2, rectF.bottom + pointRadius * 2);
        draggableRect.set(rectF.left + pointRadius * 2, rectF.top + pointRadius * 2 - 30, rectF.right - pointRadius * 2, rectF.bottom - pointRadius * 2 + 30);
        operationRect.set(draggableRect.left, draggableRect.top, draggableRect.right, draggableRect.bottom);

        if (draggableRect.width() <= minSize) {
            float delta = minSize - draggableRect.width();
            draggableRect.left -= delta / 2;
            draggableRect.right += delta / 2;
        }
        if (draggableRect.height() <= minSize) {
            float delta = minSize - draggableRect.height();
            draggableRect.top -= delta / 2;
            draggableRect.bottom += delta / 2;
        }

    }

    private void drawPoints(Canvas canvas) {
        canvas.drawRect(rScaleTLToDraw, pointPaint);
        canvas.drawRect(rScaleTRToDraw, pointPaint);
        canvas.drawRect(rScaleBLToDraw, pointPaint);
        canvas.drawRect(rScaleBRToDraw, pointPaint);
        if (!keepRatio) {
            canvas.drawCircle(points[2], points[3], pointRadius, pointPaint);
            canvas.drawCircle(points[6], points[7], pointRadius, pointPaint);
            canvas.drawCircle(points[8], points[9], pointRadius, pointPaint);
            canvas.drawCircle(points[12], points[13], pointRadius, pointPaint);
        }
    }

    enum ControlOrientation {
        TOP_LEFT(100),
        TOP_CENTER(101),
        TOP_RIGHT(102),
        CENTER_LEFT(103),
        CENTER_RIGHT(104),
        BOTTOM_LEFT(105),
        BOTTOM_CENTER(106),
        BOTTOM_RIGHT(107),
        UNKNOWN(-100);
        int index;

        ControlOrientation(int index) {
            this.index = index;
        }

        public static ControlOrientation find(int index) {
            ControlOrientation[] values = ControlOrientation.values();
            for (ControlOrientation value : values) {
                if (value.index == index) {
                    return value;
                }
            }
            return UNKNOWN;
        }
    }

    public interface OnPositionChangedListener {
        void onSelected(int id);

        void onDataChanged(int id, RectF rectF);

        void onFinish(int id, RectF rectF);

        void onTouchOutside(); // 触摸到图层外面

        void onTimerEnd(); //每次操作后倒计时完毕
    }

    public static class OrderRect extends RectF implements Cloneable {
        private int order;

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        @NonNull
        @Override
        protected OrderRect clone() {
            OrderRect orderRect = new OrderRect();
            orderRect.setOrder(order);
            orderRect.set(left, top, right, bottom);
            return orderRect;
        }
    }

    final class ControlPointer {
        int attachXEdgeDirection = 0;
        int attachYEdgeDirection = 0;
        float accuDisX = 0;
        float accuDisY = 0;

        public ControlPointer() {
        }

        public void ratioDrag(RectF target, float dx, float dy, int touchAction) {
            // y =  kx + b 线性函数
            float y0 = target.top;
            float x0 = target.left;

            float y1 = target.bottom;
            float x1 = target.right;

            float k = (y1 - y0) / (x1 - x0);
            float b = y0 - k * x0;

            float targetX = 0f;
            float targetY = 0f;

            if (dx == 0) {
                return;
            }

            if (touchAction == TOUCH_SCALE_TOP_LEFT) {
                targetX = target.left + dx;
                targetY = k * targetX + b;

                if (dx > 0 && (target.right - targetX < minSize
                        || target.bottom - targetY < minSize)
                ) return;

                if (dx < 0 && target.width() > getWidth()) return;
                if (dx * k < 0 && target.height() > getHeight()) return;

                target.set(targetX, targetY, target.right, target.bottom);

            } else if (touchAction == TOUCH_SCALE_BOTTOM_LEFT) {
                float deltaX = dx;
                float deltaY = dx * k;

                //  if (dx < 0 && (targetX - target.left) < MIN_RECT_WIDTH || target.bottom - targetY < MIN_RECT_WIDTH) return;

                if (deltaX < 0 && target.width() > getWidth()) return;
                if (deltaY < 0 && target.height() > getHeight()) return;

                if (dx > 0 && (target.width() < minSize || target.height() < minSize)) return;

                target.set(target.left + deltaX, target.top, target.right, target.bottom - deltaY);


            } else if (touchAction == TOUCH_SCALE_BOTTOM_RIGHT) {
                targetX = target.right + dx;
                targetY = k * targetX + b;

                if (dx < 0 && (targetX - target.left < minSize
                        || targetY - target.top < minSize)
                ) return;

                if (dx > 0 && target.width() > getWidth()) return;
                if (dy > 0 && target.height() > getHeight()) return;

                target.set(target.left, target.top, targetX, targetY);
            } else if (touchAction == TOUCH_SCALE_TOP_RIGHT) {

                float deltaX = dx;
                float deltaY = dx * k;

                if (deltaX > 0 && target.width() > getWidth()) return;
                if (deltaY > 0 && target.height() > getHeight()) return;

                if (dx < 0 && (target.width() < minSize || target.height() < minSize)) return;
                target.set(target.left, target.top - deltaY, target.right + deltaX, target.bottom);

            }

        }

        public void resetAttachEdge() {
            attachXEdgeDirection = 0;
            attachYEdgeDirection = 0;
            accuDisX = 0;
            accuDisY = 0;
        }

        public void move(RectF rectF, float disX, float disY, MaterialGestureLayout view) {
            float oldWidth = rectF.width();
            float oldHeight = rectF.height();
            accuDisX += disX;
            accuDisY += disY;

            if (attachXEdgeDirection > 0) {
                if (disX > 0 && accuDisX < mGap) {
                    disX = 0;
                } else if (disX < 0) {
                    attachXEdgeDirection = 0;
                }
            } else if (attachXEdgeDirection < 0) {
                if (disX < 0 && accuDisX > -mGap) {
                    disX = 0;
                } else if (disX > 0) {
                    attachXEdgeDirection = 0;
                }
            }
            if (attachYEdgeDirection > 0) {
                if (disY > 0 && accuDisY < mGap) {
                    disY = 0;
                } else if (disY < 0) {
                    attachYEdgeDirection = 0;
                }
            } else if (attachYEdgeDirection < 0) {
                if (disY < 0 && accuDisY > -mGap) {
                    disY = 0;
                } else if (disY > 0) {
                    attachYEdgeDirection = 0;
                }
            }
            rectF.offset(disX, disY);

            //四个边角的吸附功能//
            if (disX > 0) {
                if (rectF.right < view.getWidth() && rectF.right + mGap > view.getWidth()) {
                    rectF.right = view.getWidth();
                    rectF.left = rectF.right - oldWidth;
                    attachXEdgeDirection = 1;
                    accuDisX = 0;
                } else if (rectF.left < 0 && rectF.left + mGap > 0) {
                    rectF.left = 0;
                    rectF.right = oldWidth;
                    attachXEdgeDirection = 1;
                    accuDisX = 0;
                }
            } else if (disX < 0) {
                if (rectF.left > 0 && rectF.left - mGap < 0) {
                    rectF.left = 0;
                    rectF.right = oldWidth;
                    attachXEdgeDirection = -1;
                    accuDisX = 0;
                } else if (rectF.right > view.getWidth() && rectF.right - mGap < view.getWidth()) {
                    rectF.right = view.getWidth();
                    rectF.left = rectF.right - oldWidth;
                    attachXEdgeDirection = -1;
                    accuDisX = 0;
                }
            }
            if (disY > 0) {
                if (rectF.bottom < view.getHeight() && rectF.bottom + mGap > view.getHeight()) {
                    rectF.bottom = view.getHeight();
                    rectF.top = rectF.bottom - oldHeight;
                    attachYEdgeDirection = 1;
                    accuDisY = 0;
                } else if (rectF.top < 0 && rectF.top + mGap > 0) {
                    rectF.top = 0;
                    rectF.bottom = oldHeight;
                    attachYEdgeDirection = 1;
                    accuDisY = 0;
                }
            } else if (disY < 0) {
                if (rectF.top > 0 && rectF.top - mGap < 0) {
                    rectF.top = 0;
                    rectF.bottom = oldHeight;
                    attachYEdgeDirection = -1;
                    accuDisY = 0;
                } else if (rectF.bottom > view.getHeight() && rectF.bottom - mGap < view.getHeight()) {
                    rectF.bottom = view.getHeight();
                    rectF.top = rectF.bottom - oldHeight;
                    attachYEdgeDirection = -1;
                    accuDisY = 0;
                }
            }
            //范围限制//
/*            float width = rectF.width();
            float height = rectF.height();
            if (rectF.right > view.getWidth() + 0.5f*width) {
                float dis = rectF.right - (view.getWidth() + 0.5f*width);
                rectF.offset(-dis, 0);
            }
            if (rectF.bottom > view.getHeight() + 0.5f*height) {
                float dis = rectF.bottom - (view.getHeight() + 0.5f*height);
                rectF.offset(0, -dis);
            }

            if (rectF.left < -0.5f*width) {
                float dis = -0.5f*width - rectF.left;
                rectF.offset(dis, 0);
            }
            if (rectF.top < -0.5f*height) {
                float dis = -0.5f*height - rectF.top;
                rectF.offset(0, dis);
            }*/
        }


        public void drag(RectF target, float dx, float dy, ControlOrientation orientation) {

            switch (orientation) {
                case TOP_LEFT:
                    if (dx > 0f && target.width() - dx < minSize || dy > 0f && target.height() - dy < minSize)
                        return;
                    //if (dx < 0 && target.width() > getWidth()) return;
                    //if (dy < 0 && target.height() > getHeight()) return;
                    target.set(target.left + dx, target.top + dy, target.right, target.bottom);
                    break;
                case TOP_CENTER:
                    if (dy > 0f && target.height() - dy < minSize) return;
                    //if (dy < 0 && target.height() > getHeight()) return;
                    target.set(target.left, target.top + dy, target.right, target.bottom);
                    break;
                case TOP_RIGHT:
                    if (dx < 0f && (target.width() + dx < minSize) || dy > 0f && target.height() - dy < minSize)
                        return;
                    //if (dx > 0 && target.width() > getWidth()) return;
                    //if (dy < 0 && target.height() > getHeight()) return;
                    target.set(target.left, target.top + dy, target.right + dx, target.bottom);
                    break;
                case CENTER_LEFT:
                    if (dx > 0f && target.width() - dx < minSize) return;
                    //if (dx < 0 && target.width() > getWidth()) return;
                    target.set(target.left + dx, target.top, target.right, target.bottom);
                    break;
                case CENTER_RIGHT:
                    if (dx < 0f && (target.width() + dx < minSize)) return;
                    //if (dx > 0 && target.width() > getWidth()) return;
                    target.set(target.left, target.top, target.right + dx, target.bottom);
                    break;
                case BOTTOM_LEFT:
                    if (dx > 0f && target.width() - dx < minSize || dy < 0f && target.height() + dy < minSize)
                        return;
                    //if (dx < 0 && target.width() > getWidth()) return;
                    //if (dy > 0 && target.height() > getHeight()) return;
                    target.set(target.left + dx, target.top, target.right, target.bottom + dy);
                    break;
                case BOTTOM_CENTER:
                    if (dy < 0f && target.height() + dy < minSize) return;
                    //if (dy > 0 && target.height() > getHeight()) return;
                    target.set(target.left, target.top, target.right, target.bottom + dy);
                    break;
                case BOTTOM_RIGHT:
                    if (dx < 0f && (target.width() + dx < minSize) || dy < 0f && target.height() + dy < minSize) {
                        return;
                    }
                    //if (dx > 0 && target.width() > getWidth()) return;
                    //if (dy > 0 && target.height() > getHeight()) return;
                    target.set(target.left, target.top, target.right + dx, target.bottom + dy);
                    break;
            }
        }
    }
}

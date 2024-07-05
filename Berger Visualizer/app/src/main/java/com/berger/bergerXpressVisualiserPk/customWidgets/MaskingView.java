package com.berger.bergerXpressVisualiserPk.customWidgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;


import com.berger.bergerXpressVisualiserPk.R;
import com.berger.bergerXpressVisualiserPk.utill.Constants;
import com.berger.bergerXpressVisualiserPk.utill.Utills;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class MaskingView extends View {

    private static final String TAG = "CirclesDrawingView";

    private int w;
    private int h;
    private Paint mPaint;
    private Point pPoint;
    private ArrayList<Point> points;
    private static int num = 0;
    private Context context;
    private float circleRadius = 0.03f;
    private float strokeWidth = 0.008f;
    float screenWidthPixel  = this.getResources().getDisplayMetrics().widthPixels;
    float screenHeightPixel = this.getResources().getDisplayMetrics().heightPixels;

    /**
     * Stores data about single circle
     */
    private static class CircleArea {
        int radius;
        int centerX;
        int centerY;
        int number;

        CircleArea(int centerX, int centerY, int radius, int number) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;
            this.number = number;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }

    /**
     * Paint to draw circles
     */
    private Paint mCirclePaint;

    private final Random mRadiusGenerator = new Random();
    // Radius limit in pixels
    private final static int RADIUS_LIMIT = 10;

    private static final int CIRCLES_LIMIT = 10;

    /**
     * All available circles
     */
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);

    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public MaskingView(final Context ct) {
        super(ct);

        init(ct);
    }

    public MaskingView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);

        init(ct);
    }

    public MaskingView(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);

        init(ct);
    }

    private void init(final Context ct) {
        this.context = ct;

        strokeWidth = strokeWidth * screenWidthPixel;
        circleRadius = circleRadius * screenWidthPixel;

        mCirclePaint = new Paint();
        points = new ArrayList<>();
        pPoint = new Point();

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(strokeWidth);

        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStrokeWidth(strokeWidth);
        mCirclePaint.setStyle(Paint.Style.FILL);

        initializeMasking();
    }

    private void initializeMasking(){
        points = new ArrayList<>();
        if(Constants.points != null && Constants.points.size() > 0){
            for(int i = 0 ; i < Constants.points.size(); i++){
                CircleArea touchedCircle = new CircleArea(Constants.points.get(i).x, Constants.points.get(i).y, (int) circleRadius, num);
                num++;

                Point point = new Point();

                point.x = Constants.points.get(i).x;
                point.y = Constants.points.get(i).y;

                Log.w(TAG, "Added circle " + touchedCircle);
                mCircles.add(touchedCircle);
                points.add(point);
            }
            invalidate();
        }
    }

    @Override
    public void onDraw(final Canvas canv) {
        // background bitmap to cover all area
//        canv.drawBitmap(mBitmap, null, mMeasuredRect, null);

        int i = 0;
        int j = mCircles.size();

        for (CircleArea circle : mCircles) {
            canv.drawCircle(circle.centerX, circle.centerY, circle.radius, mCirclePaint);

            points.get(circle.number).x = circle.centerX;
            points.get(circle.number).y = circle.centerY;

//            if(i%2 == 0){
//                pPoint.x = circle.
//            } else {
//
//            }

//            if(i%2 != 0 && i < points.size() && points.size() > 1){
            if(i>0)
                canv.drawLine(points.get(i).x, points.get(i).y, points.get(i-1).x, points.get(i-1).y, mPaint);
//            }

            i++;
        }
        Constants.points = points;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                if(touchedCircle != null) {
                    touchedCircle.centerX = xTouch;
                    touchedCircle.centerY = yTouch;
                    mCirclePointer.put(event.getPointerId(0), touchedCircle);

                    invalidate();
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                if(xTouch < 0)
                    xTouch = 0;
                if(xTouch > w)
                    xTouch = w;
                if(yTouch < 0)
                    yTouch = 0;
                if(yTouch > h)
                    yTouch = h;

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                if(touchedCircle != null) {
                    mCirclePointer.put(pointerId, touchedCircle);
                    touchedCircle.centerX = xTouch;
                    touchedCircle.centerY = yTouch;
                    invalidate();
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                Log.w(TAG, "Move");

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    if(xTouch < 0)
                        xTouch = 0;
                    if(xTouch > w)
                        xTouch = w;
                    if(yTouch < 0)
                        yTouch = 0;
                    if(yTouch > h)
                        yTouch = h;

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (touchedCircle != null) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearCirclePointer() {
        Log.w(TAG, "clearCirclePointer");

        mCirclePointer.clear();
    }

    /**
     * Search and creates new (if needed) circle based on touch area
     *  @param xTouch int x of touch
     * @param yTouch int y of touch
     * @return
     */
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null == touchedCircle) {

            if (mCircles.size() == CIRCLES_LIMIT) {
                Log.w(TAG, "Clear all circles, size is " + mCircles.size());
                // remove first circle
//                mCircles.clear();
//                points.clear();
//                Constants.points.clear();
//                num = 0;

                Utills.showToast((Activity) context, context.getResources().getString(R.string.paint_toast_masking_cannot_add));
                return null;
            } else {
                touchedCircle = new CircleArea(xTouch, yTouch, (int) circleRadius, num);
                num++;

                Point point = new Point();

                point.x = xTouch;
                point.y = yTouch;

                Log.w(TAG, "Added circle " + touchedCircle);
                mCircles.add(touchedCircle);
                points.add(point);

            }
        }

        return touchedCircle;
    }

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     * @return {@link CircleArea} touched circle or null if no circle has been touched
     */
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;

        for (CircleArea circle : mCircles) {
            if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
                touched = circle;
                break;
            }
        }

        return touched;
    }

    public void clear(){
        mCircles.clear();
        mCirclePointer.clear();
        points.clear();
        num = 0;
        invalidate();
    }

    /*
    private Paint mPaint;
    private Paint cPaint;
    private Path mPath;

    private Boolean startPoint;
    private Boolean endPoint;

    private float startPointX;
    private float startPointY;

    private float endPointX;
    private float endPointY;

    public MaskingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);

        cPaint = new Paint();
        cPaint.setColor(Color.BLUE);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setAntiAlias(true);

        mPath = new Path();
        startPoint = true;
        endPoint = false;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if(startPoint){
            canvas.drawCircle(startPointX, startPointY, 5, cPaint);
        } else {
            canvas.drawCircle(endPointX, endPointY, 5, cPaint);
            canvas.drawLine(startPointX, startPointY, endPointX, endPointY, mPaint);
        }
        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                if(startPoint) {
                    startPointX = event.getX();
                    startPointY = event.getY();
                }

                if(endPoint){
                    endPointX = event.getX();
                    endPointY = event.getY();
                }

//                mPath.moveTo(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                if(startPoint) {
                    startPointX = event.getX();
                    startPointY = event.getY();
                }

                if(endPoint){
                    endPointX = event.getX();
                    endPointY = event.getY();
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:

                if(startPoint){
                    startPoint = false;
                    endPoint = true;
                } else if(endPoint){
                    startPoint = true;
                    endPoint = false;
                }
                invalidate();

                break;
        }

        return true;
    }
    */


//    private Paint paint = new Paint();
//
//    public MaskingView(Context context) {
//        super(context);
//        paint.setColor(Color.WHITE);
//        paint.setStrokeWidth(1f);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.drawLine(20, 100, 140, 30, paint);
//    }

}

package com.smile.miclock;

import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

/**
 * @Author: LZQ
 * @CreateDate: 2019/4/11 13:43
 */
public class MiClockView extends View {

    /**
     * 钟表半径
     */
    private int mRadius;

    /**
     * 画布
     */
    private Canvas mCanvas;

    /**
     * 文字画笔
     */
    private Paint mTextPaint;

    /**
     * 刻度文字颜色
     */
    private int mScaleNumColor;

    /**
     * 最外成圆弧颜色
     */
    private int mOutCircleColor;

    /**
     * 最外成圆弧宽度
     */
    private float mOutCircleWidth;

    /**
     * 最外层圆弧画笔
     */
    private Paint mOutCirclePaint;

    /**
     * 小时圆圈的外接矩形
     */
    private RectF mOutCircleRectF;

    /**
     * 最外层数字刻度外接矩形
     */
    private Rect mTextRect;

    /**
     * 最外成文字的size
     */
    private float mTextSize;

    /**
     * 刻度圆弧外接矩形
     */
    private RectF mScaleArcRecctF;

    /**
     * 刻度宽度
     */
    private float mScaleWidth;

    /**
     * 刻度圆弧画笔
     */
    private Paint mScaleArcPaint;
    /**
     * 刻度圆弧开始颜色
     */
    private int mScaleArcStartColor;
    /**
     * 刻度圆弧结束颜色
     */
    private int mScaleArcEndColor;
    /**
     * 梯度扫描渐变
     */
    private SweepGradient mSweepGradient;
    /**
     * 刻度矩阵
     */
    private Matrix mGradientMatrix;

    /**
     * 刻度空隙
     */
    private Paint mSweepLineSaclePaint;
    /**
     * 背景颜色
     */
    private int mBackgroundColor;

    /**
     * 秒针画笔
     */
    private Paint mSecondHandPaint;

    /**
     * 秒针路径
     */
    private Path mSencondPath;

    /**
     * 内圈画笔
     */
    private Paint mInnerCirclePaint;

    /**
     * 分针画笔
     */
    private Paint mMinuteHandPaint;

    /**
     * 分针路径
     */
    private Path mMinutePath;
    /**
     * 分针宽度
     */
    private float mMinuteWidth;

    /**
     * 时针画笔
     */
    private Paint mHourHandPaint;

    /**
     * 时针路径
     */
    private Path mHourPath;
    /**
     * 时针宽度
     */
    private float mHourWidth;

    /**
     * 圆心半径
     */
    private float mCircleCenterRadius;
    /**
     * 圆心画笔
     */
    private Paint mCircleCenterPaint;

    /**
     * 时针角度
     */
    private float mHourDegree;
    /**
     * 分针角度
     */
    private float mMinuteDegree;
    /**
     * 秒针角度
     */
    private float mSecondDegree;
    /**
     * 触摸时作用在Camera的矩阵
     */
    private Matrix mCameraMatrix;
    /**
     * 照相机，用于旋转时钟实现3D效果
     */
    private Camera mCamera;
    /**
     * camera绕X轴旋转的角度
     */
    private float mCameraRotateX;
    /**
     * camera绕Y轴旋转的角度
     */
    private float mCameraRotateY;
    /**
     * 指针的在x轴的位移
     */
    private float mCanvasTranslateX;
    /**
     * 指针的在y轴的位移
     */
    private float mCanvasTranslateY;
    /**
     * camera旋转的最大角度
     */
    private float mMaxCameraRotate = 10;
    /**
     * 指针的最大位移
     */
    private float mMaxCanvasTranslate;

    /**
     * 手指松开时时钟晃动的动画
     */
    private ValueAnimator mShakeAnim;

    public MiClockView(Context context) {
        this(context, null);
    }

    public MiClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MiClockView);

        //外层圆弧部分
        mOutCircleColor = typedArray.getColor(R.styleable.MiClockView_outCircleColor, Color.parseColor("#80ffffff"));
        mOutCircleWidth = typedArray.getDimension(R.styleable.MiClockView_outCircleWidth, DensityUtils.dp2px(context, 1));

        mOutCircleRectF = new RectF();

        mOutCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutCirclePaint.setColor(mOutCircleColor);
        mOutCirclePaint.setStrokeWidth(mOutCircleWidth);
        mOutCirclePaint.setStyle(Paint.Style.STROKE);

        //数字刻度部分
        mTextRect = new Rect();
        mScaleNumColor = typedArray.getColor(R.styleable.MiClockView_scaleNumberColor, Color.parseColor("#80ffffff"));
        mTextSize = typedArray.getDimension(R.styleable.MiClockView_scaleTextSize, DensityUtils.dp2px(context, 14));
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mScaleNumColor);
        mTextPaint.setTextSize(mTextSize);

        //渐变刻度部分
        mScaleWidth = typedArray.getDimension(R.styleable.MiClockView_scaleWidth, DensityUtils.dp2px(context, 15));
        mScaleArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleArcPaint.setStyle(Paint.Style.STROKE);
        mScaleArcPaint.setStrokeWidth(mScaleWidth);
        mScaleArcRecctF = new RectF();
        mScaleArcStartColor = typedArray.getColor(R.styleable.MiClockView_scaleStartColor, Color.parseColor("#80ffffff"));
        mScaleArcEndColor = typedArray.getColor(R.styleable.MiClockView_scaleEndColor, Color.parseColor("#ffffff"));
        mGradientMatrix = new Matrix();


        mBackgroundColor = typedArray.getColor(R.styleable.MiClockView_backgroundColor, Color.parseColor("#ffffff"));
        mSweepLineSaclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSweepLineSaclePaint.setStyle(Paint.Style.STROKE);
        mSweepLineSaclePaint.setColor(mBackgroundColor);

        //内圆
        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setStrokeWidth(typedArray.getDimension(R.styleable.MiClockView_innerCircleWidth, DensityUtils.dp2px(context, 1)));
        mInnerCirclePaint.setColor(typedArray.getColor(R.styleable.MiClockView_innerCircleColor, Color.parseColor("#80ffffff")));

        //圆心半径
        mCircleCenterRadius = typedArray.getDimension(R.styleable.MiClockView_circleCenterRadius, DensityUtils.dp2px(context, 5));
        mCircleCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleCenterPaint.setStyle(Paint.Style.STROKE);
        mCircleCenterPaint.setColor(typedArray.getColor(R.styleable.MiClockView_circleCenterColor, Color.parseColor("#FFFFFF")));
        mCircleCenterPaint.setStrokeWidth(typedArray.getDimension(R.styleable.MiClockView_circleCenterWidth, DensityUtils.dp2px(context, 5)));

        //秒针
        int mSecondHandColor = typedArray.getColor(R.styleable.MiClockView_secondHandColor, Color.parseColor("#FFFFFF"));
        mSecondHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondHandPaint.setColor(mSecondHandColor);
        mSencondPath = new Path();

        //分针
        int mMinuteHandColor = typedArray.getColor(R.styleable.MiClockView_minuteHandColor, Color.parseColor("#FFFFFF"));
        mMinuteHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinuteHandPaint.setColor(mMinuteHandColor);
        mMinutePath = new Path();
        mMinuteWidth = typedArray.getDimension(R.styleable.MiClockView_minuteHandWidth, DensityUtils.dp2px(context, 5));

        //时针
        int mHourHandColor = typedArray.getColor(R.styleable.MiClockView_hourHandColor, Color.parseColor("#80FFFFFF"));
        mHourHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourHandPaint.setColor(mHourHandColor);
        mHourPath = new Path();
        mHourWidth = typedArray.getDimension(R.styleable.MiClockView_hourHandWidth, DensityUtils.dp2px(context, 5));

        mCameraMatrix = new Matrix();
        mCamera = new Camera();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min((w - getPaddingLeft() - getPaddingRight()), (h - getPaddingTop() - getPaddingBottom())) / 2;
        mSweepGradient = new SweepGradient(w / 2F, h / 2F, new int[]{mScaleArcStartColor, mScaleArcEndColor}, new float[]{0.75F, 1F});
        mSweepLineSaclePaint.setStrokeWidth(mRadius * 0.012F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;

        setCameraRotate();
        getTimeDegree();
        drawOutCircle();
        drawOutNumber();
        drawScaleArc();
        drawSecondHand();
        drawInnerCircle();
        drawMinuteHand();
        drawHourHand();
        drawCenterCircle();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mShakeAnim != null && mShakeAnim.isRunning()) {
                    mShakeAnim.cancel();
                }
                getCameraRotate(event);
                getCanvasTranslate(event);
                break;
            case MotionEvent.ACTION_MOVE:
                //根据手指坐标计算camera应该旋转的大小
                getCameraRotate(event);
                getCanvasTranslate(event);
                break;
            case MotionEvent.ACTION_UP:
                //松开手指，时钟复原并伴随晃动动画
                startShakeAnim();
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 时钟晃动动画
     */
    private void startShakeAnim() {
        final String cameraRotateXName = "cameraRotateX";
        final String cameraRotateYName = "cameraRotateY";
        final String canvasTranslateXName = "canvasTranslateX";
        final String canvasTranslateYName = "canvasTranslateY";
        PropertyValuesHolder cameraRotateXHolder =
                PropertyValuesHolder.ofFloat(cameraRotateXName, mCameraRotateX, 0);
        PropertyValuesHolder cameraRotateYHolder =
                PropertyValuesHolder.ofFloat(cameraRotateYName, mCameraRotateY, 0);
        PropertyValuesHolder canvasTranslateXHolder =
                PropertyValuesHolder.ofFloat(canvasTranslateXName, mCanvasTranslateX, 0);
        PropertyValuesHolder canvasTranslateYHolder =
                PropertyValuesHolder.ofFloat(canvasTranslateYName, mCanvasTranslateY, 0);
        mShakeAnim = ValueAnimator.ofPropertyValuesHolder(cameraRotateXHolder,
                cameraRotateYHolder, canvasTranslateXHolder, canvasTranslateYHolder);
        mShakeAnim.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                float f = 0.571429f;
                return (float) (Math.pow(2, -2 * input) * Math.sin((input - f / 4) * (2 * Math.PI) / f) + 1);
            }
        });
        mShakeAnim.setDuration(1000);
        mShakeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCameraRotateX = (float) animation.getAnimatedValue(cameraRotateXName);
                mCameraRotateY = (float) animation.getAnimatedValue(cameraRotateYName);
                mCanvasTranslateX = (float) animation.getAnimatedValue(canvasTranslateXName);
                mCanvasTranslateY = (float) animation.getAnimatedValue(canvasTranslateYName);
            }
        });
        mShakeAnim.start();
    }

    /**
     * 获取camera旋转的大小
     *
     * @param event motionEvent
     */
    private void getCameraRotate(MotionEvent event) {
        float rotateX = -(event.getY() - getHeight() / 2);
        float rotateY = (event.getX() - getWidth() / 2);
        //求出此时旋转的大小与半径之比
        float[] percentArr = getPercent(rotateX, rotateY);
        //最终旋转的大小按比例匀称改变
        mCameraRotateX = percentArr[0] * mMaxCameraRotate;
        mCameraRotateY = percentArr[1] * mMaxCameraRotate;
    }

    /**
     * 当拨动时钟时，会发现时针、分针、秒针和刻度盘会有一个较小的偏移量，形成近大远小的立体偏移效果
     * 一开始我打算使用 matrix 和 camera 的 mCamera.translate(x, y, z) 方法改变 z 的值
     * 但是并没有效果，所以就动态计算距离，然后在 onDraw()中分零件地 mCanvas.translate(x, y)
     *
     * @param event motionEvent
     */
    private void getCanvasTranslate(MotionEvent event) {
        float translateX = (event.getX() - getWidth() / 2);
        float translateY = (event.getY() - getHeight() / 2);
        //求出此时位移的大小与半径之比
        float[] percentArr = getPercent(translateX, translateY);
        //最终位移的大小按比例匀称改变
        mCanvasTranslateX = percentArr[0] * mMaxCanvasTranslate;
        mCanvasTranslateY = percentArr[1] * mMaxCanvasTranslate;
    }

    /**
     * 获取一个操作旋转或位移大小的比例
     *
     * @param x x大小
     * @param y y大小
     * @return 装有xy比例的float数组
     */
    private float[] getPercent(float x, float y) {
        float[] percentArr = new float[2];
        float percentX = x / mRadius;
        float percentY = y / mRadius;
        if (percentX > 1) {
            percentX = 1;
        } else if (percentX < -1) {
            percentX = -1;
        }
        if (percentY > 1) {
            percentY = 1;
        } else if (percentY < -1) {
            percentY = -1;
        }
        percentArr[0] = percentX;
        percentArr[1] = percentY;
        return percentArr;
    }

    /**
     * 3D旋转
     */
    private void setCameraRotate() {
        mCameraMatrix.reset();
        mCamera.save();
        mCamera.rotateX(mCameraRotateX);
        mCamera.rotateY(mCameraRotateY);
        mCamera.getMatrix(mCameraMatrix);
        mCamera.restore();
        mCameraMatrix.preTranslate(-getWidth() / 2F, -getHeight() / 2F);
        //在动作之后post再回到原位
        mCameraMatrix.postTranslate(getWidth() / 2F, getHeight() / 2F);
        mCanvas.concat(mCameraMatrix);
    }

    /**
     * 绘制最外成圆圈
     */
    private void drawOutCircle() {
        mOutCircleRectF.set(
                getWidth() / 2F - mRadius + mTextSize / 2F,
                getHeight() / 2F - mRadius + mTextSize / 2F,
                getWidth() / 2F + mRadius - mTextSize / 2F,
                getHeight() / 2F + mRadius - mTextSize / 2F
        );

        for (int i = 0; i < 4; i++) {
            mCanvas.drawArc(mOutCircleRectF, 5 + i * 90, 80, false, mOutCirclePaint);
        }

    }

    /**
     * 绘制最外层数字刻度
     */
    private void drawOutNumber() {
        String timeText = "12";
        mTextPaint.getTextBounds(timeText, 0, timeText.length(), mTextRect);
        float textLengthTwo = mTextRect.width();
        mCanvas.drawText("12", getWidth() / 2F - textLengthTwo / 2F, getHeight() / 2F - mRadius + mTextRect.height(), mTextPaint);

        timeText = "3";
        mTextPaint.getTextBounds(timeText, 0, timeText.length(), mTextRect);
        float textLengthOne = mTextRect.width();

        mCanvas.drawText("3", getWidth() / 2F + mRadius - textLengthOne / 2F - mTextSize / 2F, getHeight() / 2F + mTextRect.height() / 2F, mTextPaint);
        mCanvas.drawText("6", getWidth() / 2F - textLengthOne / 2F, getHeight() / 2F + mRadius + mTextSize / 2F - mTextRect.height(), mTextPaint);
        mCanvas.drawText("9", getWidth() / 2F - mRadius - textLengthOne / 2F + mTextSize / 2F, getHeight() / 2F + mTextRect.height() / 2F, mTextPaint);

    }

    /**
     * 绘制刻度圆圈
     */
    private void drawScaleArc() {
        mCanvas.save();
        mScaleArcRecctF.set(
                getWidth() / 2F - mRadius + mScaleWidth * 2F,
                getHeight() / 2F - mRadius + mScaleWidth * 2F,
                getWidth() / 2F + mRadius - mScaleWidth * 2F,
                getHeight() / 2F + mRadius - mScaleWidth * 2F
        );

        mGradientMatrix.setRotate(mSecondDegree - 90, getWidth() / 2F, getHeight() / 2F);
        mSweepGradient.setLocalMatrix(mGradientMatrix);
        mScaleArcPaint.setShader(mSweepGradient);
        mCanvas.drawArc(mScaleArcRecctF, 0, 360, false, mScaleArcPaint);

        for (int i = 0; i < 200; i++) {
            mCanvas.drawLine(
                    getWidth() / 2F,
                    getHeight() / 2F - mRadius + mScaleWidth * 1.5F,
                    getWidth() / 2F,
                    getHeight() / 2F - mRadius + mScaleWidth * 2.5F,
                    mSweepLineSaclePaint
            );
            mCanvas.rotate(1.8F, getWidth() / 2F, getHeight() / 2F);
        }
        mCanvas.restore();
    }

    /**
     * 绘制秒针
     */
    private void drawSecondHand() {
        mCanvas.save();
        mCanvas.rotate(mSecondDegree, getWidth() / 2F, getHeight() / 2F);
        if (mSencondPath.isEmpty()) {
            mSencondPath.reset();
            mSencondPath.moveTo(getWidth() / 2F, getHeight() / 2F - mRadius + mScaleWidth * 3F);
            mSencondPath.lineTo(getWidth() / 2F - 0.05f * mRadius, getHeight() / 2F - mRadius + mScaleWidth * 3F + 0.1F * mRadius);
            mSencondPath.lineTo(getWidth() / 2F + 0.05f * mRadius, getHeight() / 2F - mRadius + mScaleWidth * 3F + 0.1F * mRadius);
            mSencondPath.close();
        }
        mCanvas.drawPath(mSencondPath, mSecondHandPaint);
        mCanvas.restore();
    }

    /**
     * 绘制内圆圈
     */
    private void drawInnerCircle() {
        float offset = mScaleWidth * 3F + 0.12F * mRadius;
        mCanvas.drawCircle(getWidth() / 2F, getHeight() / 2F, mRadius - offset, mInnerCirclePaint);
    }

    /**
     * 绘制分针
     */
    private void drawMinuteHand() {
        mCanvas.save();
        mCanvas.rotate(mMinuteDegree, getWidth() / 2F, getHeight() / 2F);
        if (mMinutePath.isEmpty()) {
            float offset = mRadius - mScaleWidth * 3F - 0.2F * mRadius;
            mMinutePath.reset();
            mMinutePath.moveTo(getWidth() / 2F - mMinuteWidth / 2F, getHeight() / 2F - offset);
            mMinutePath.lineTo(getWidth() / 2F - mMinuteWidth / 2F, getHeight() / 2F - mCircleCenterRadius);
            mMinutePath.lineTo(getWidth() / 2F + mMinuteWidth / 2F, getHeight() / 2F - mCircleCenterRadius);
            mMinutePath.lineTo(getWidth() / 2F + mMinuteWidth / 2F, getHeight() / 2F - offset);
            mMinutePath.quadTo(getWidth() / 2F, getHeight() / 2F - offset - mMinuteWidth, getWidth() / 2F - mMinuteWidth / 2F, getHeight() / 2F - offset);
        }

        mCanvas.drawPath(mMinutePath, mMinuteHandPaint);
        mCanvas.restore();
    }

    /**
     * 绘制时针
     */
    private void drawHourHand() {
        mCanvas.save();
        mCanvas.rotate(mHourDegree, getWidth() / 2F, getHeight() / 2F);
        if (mHourPath.isEmpty()) {
            float offset = mRadius - mScaleWidth * 3F - 0.3F * mRadius;
            mHourPath.reset();
            mHourPath.moveTo(getWidth() / 2F - mHourWidth / 2F, getHeight() / 2F - offset);
            mHourPath.lineTo(getWidth() / 2F - mHourWidth / 2F, getHeight() / 2F - mCircleCenterRadius);
            mHourPath.lineTo(getWidth() / 2F + mHourWidth / 2F, getHeight() / 2F - mCircleCenterRadius);
            mHourPath.lineTo(getWidth() / 2F + mHourWidth / 2F, getHeight() / 2F - offset);
            mHourPath.quadTo(getWidth() / 2F, getHeight() / 2F - offset - mMinuteWidth, getWidth() / 2F - mMinuteWidth / 2F, getHeight() / 2F - offset);
        }

        mCanvas.drawPath(mHourPath, mHourHandPaint);
        mCanvas.restore();
    }

    /**
     * 绘制圆心
     */
    private void drawCenterCircle() {
        mCanvas.drawCircle(getWidth() / 2F, getHeight() / 2F, mCircleCenterRadius, mCircleCenterPaint);
    }

    /**
     * 获取当前时分秒所对应的角度
     * 为了不让秒针走得像老式挂钟一样僵硬，需要精确到毫秒
     */
    private void getTimeDegree() {
        Calendar calendar = Calendar.getInstance();
        float milliSecond = calendar.get(Calendar.MILLISECOND);
        float second = calendar.get(Calendar.SECOND) + milliSecond / 1000;
        float minute = calendar.get(Calendar.MINUTE) + second / 60;
        float hour = calendar.get(Calendar.HOUR) + minute / 60;
        mSecondDegree = second / 60F * 360F;
        mMinuteDegree = minute / 60F * 360F;
        mHourDegree = hour / 12F * 360F;
    }

}

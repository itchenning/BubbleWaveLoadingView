package com.terry.bubblewaveloadingview.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.terry.bubblewaveloadingview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author: Terry
 * Date: 2019-06-21 12:52
 * Comment:
 */
public class BubbleWaveLoadingView extends View {

    private static final int DEFAULT_WIDTH = 300;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.1f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;

    private Context mContext;
    private int mWidth;

    private int mBgColor;
    private int mBorderColor;
    private int mFirstWaveColor;
    private int mSecondWaveColor;
    private int mBubbleColor;

    private float mBorderWidth;
    private float mBubbleMaxRadius;
    private float mBubbleMinRadius;
    private float mBubbleMaxSize;
    private float mBunbleMaxSpeedY;
    private int mProgress;

    private Paint mBgPaint;
    private Paint mBorderPaint;
    private Paint mWavePaint;
    private Paint mBubblePaint;

    private List<Bubble> mBubbles = new ArrayList<>();
    private ObjectAnimator waveShiftAnim;
    private AnimatorSet mAnimatorSet;
    private float mWaveShiftRatio;
    private float mWaterLevelRatio;
    private Bitmap bitmapBuffer;
    private double mDefaultWaterLevel;
    private BitmapShader mWaveShader;
    private Matrix mShaderMatrix;
    private float mAmplitudeRatio;

    public BubbleWaveLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(this, "waterLevelRatio", mWaterLevelRatio, ((float) mProgress / 100F));
        waterLevelAnim.setDuration(1000);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        AnimatorSet animatorSetProgress = new AnimatorSet();
        animatorSetProgress.play(waterLevelAnim);
        animatorSetProgress.start();
    }

    public void startAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    public void cancelAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    public void setWaveShiftRatio(float waveShiftRatio) {
        if (this.mWaveShiftRatio != waveShiftRatio) {
            this.mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    public void setWaterLevelRatio(float waterLevelRatio) {
        if (this.mWaterLevelRatio != waterLevelRatio) {
            this.mWaterLevelRatio = waterLevelRatio;
            invalidate();
        }
    }

    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mWidth = getWidth(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BubbleWaveLoadingView);
        mBgColor = attributes.getColor(R.styleable.BubbleWaveLoadingView_wb_bg_color, Color.parseColor("#FFF0F0F0"));
        mBorderColor = attributes.getColor(R.styleable.BubbleWaveLoadingView_wb_border_color, Color.parseColor("#FFFFFFFF"));
        mFirstWaveColor = attributes.getColor(R.styleable.BubbleWaveLoadingView_wb_first_wave_color, Color.parseColor("#FF4646F8"));
        mSecondWaveColor = attributes.getColor(R.styleable.BubbleWaveLoadingView_wb_second_wave_color, Color.parseColor("#66266DFF"));
        mBubbleColor = attributes.getColor(R.styleable.BubbleWaveLoadingView_wb_bubble_color, Color.parseColor("#33FFFFFF"));

        mBorderWidth = attributes.getDimension(R.styleable.BubbleWaveLoadingView_wb_boder_width, dip2px(6));
        mBubbleMaxRadius = attributes.getDimension(R.styleable.BubbleWaveLoadingView_wb_bubble_max_radius, dip2px(10));
        mBubbleMinRadius = attributes.getDimension(R.styleable.BubbleWaveLoadingView_wb_bubble_min_radius, dip2px(2));
        mBubbleMaxSize = attributes.getInteger(R.styleable.BubbleWaveLoadingView_wb_bubble_max_size, 30);
        mBunbleMaxSpeedY = attributes.getInteger(R.styleable.BubbleWaveLoadingView_wb_bubble_max_speed_y, 3);
        mProgress = attributes.getInteger(R.styleable.BubbleWaveLoadingView_wb_progress, 60);

        float amplitudeRatioAttr = attributes.getFloat(R.styleable.BubbleWaveLoadingView_wb_amplitude, 50f) / 1000;
        mAmplitudeRatio = (amplitudeRatioAttr > DEFAULT_AMPLITUDE_RATIO) ? DEFAULT_AMPLITUDE_RATIO : amplitudeRatioAttr;
        attributes.recycle();
        initPaints();
        initOtherVariates();
        initWaveAnimation();
        createWaveShader();
        setProgress(mProgress);
    }

    private float dip2px(float dpValue) {
        return dpValue * (mContext.getResources().getDisplayMetrics().density) + 0.5f;
    }

    private void initOtherVariates() {
        mShaderMatrix = new Matrix();
    }

    private void initWaveAnimation() {
        waveShiftAnim = ObjectAnimator.ofFloat(this, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(waveShiftAnim);
        mAnimatorSet.start();
    }

    private void initPaints() {
        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setAntiAlias(true);

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setAntiAlias(true);

        mWavePaint = new Paint();
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setColor(mFirstWaveColor);
        mWavePaint.setAntiAlias(true);

        mBubblePaint = new Paint();
        mBubblePaint.setStyle(Paint.Style.FILL);
        mBubblePaint.setColor(mBubbleColor);
        mBubblePaint.setAntiAlias(true);
    }

    private int getWidth(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_width});
        float width = ta.getDimension(0, DEFAULT_WIDTH);
        ta.recycle();
        return (int) width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawBorder(canvas);
        drawWave(canvas);
        tryCreateBubble();
        refreshBubbles();
        drawBubbles(canvas);
    }

    private void createWaveShader() {
        if (bitmapBuffer != null) {
            return;
        }
        double defaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / mWidth;
        float defaultAmplitude = mWidth * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = mWidth * DEFAULT_WATER_LEVEL_RATIO;
        float defaultWaveLength = mWidth;

        Bitmap bitmap = Bitmap.createBitmap(mWidth, mWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint wavePaint = new Paint();
        wavePaint.setStrokeWidth(2);
        wavePaint.setAntiAlias(true);

        // Draw default waves into the bitmap.
        // y=Asin(ωx+φ)+h
        final int endX = mWidth + 1;
        final int endY = mWidth + 1;

        float[] waveY = new float[endX];

        wavePaint.setColor(mSecondWaveColor);
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * defaultAngularFrequency;
            float beginY = (float) (mDefaultWaterLevel + defaultAmplitude * Math.sin(wx));
            canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);
            waveY[beginX] = beginY;
        }

        wavePaint.setColor(mFirstWaveColor);
        final int wave2Shift = (int) (defaultWaveLength / 4);
        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
        }

        // Use the bitamp to create the shader.
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        this.mWavePaint.setShader(mWaveShader);

    }

    private void drawBubbles(Canvas canvas) {
        List<Bubble> list = new ArrayList<>(mBubbles);
        for (Bubble bubble : list) {
            if (null == bubble) {
                continue;
            }
            canvas.drawCircle(bubble.x, bubble.y, bubble.radius, mBubblePaint);
        }
    }

    private void refreshBubbles() {
        List<Bubble> list = new ArrayList<>(mBubbles);
        for (Bubble bubble : list) {
            if (isOutOfRange(bubble)) {
                mBubbles.remove(bubble);
            } else {
                int i = mBubbles.indexOf(bubble);
                if (bubble.x + bubble.speedX <= 0 + bubble.radius + mBorderWidth / 2) {
                    bubble.x = 0 + bubble.radius + mBorderWidth / 2;
                } else if (bubble.x + bubble.speedX >= mWidth - bubble.radius - mBorderWidth / 2) {
                    bubble.x = 0 - bubble.radius - mBorderWidth / 2;
                } else {
                    bubble.x = bubble.x + bubble.speedX;
                }
                bubble.y = bubble.y - bubble.speedY;
                mBubbles.set(i, bubble);
            }
        }
    }

    private boolean isOutOfRange(Bubble bubble) {
        if (bubble.y - bubble.speedY <= bubble.radius) {
            return true;
        }
        int minY = (int) (mWidth - (int) (mProgress / 100F * mWidth) + bubble.radius);
        if (bubble.y < minY) {
            return true;
        }
        float bw = getBetween(new android.graphics.Point((int) bubble.x, (int) bubble.y), new Point(mWidth / 2, mWidth / 2));
        if (bw > mWidth / 2 - mBorderWidth - bubble.radius) {
            return true;
        }
        return false;
    }

    private float getBetween(Point start, Point end) {
        return (float) Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(start.y - end.y, 2));
    }

    private void tryCreateBubble() {
        if (mBubbles.size() >= mBubbleMaxSize) {
            return;
        }
        Random random = new Random();
        if (random.nextFloat() < 0.95) {
            return;
        }
        Bubble bubble = new Bubble();
        int radius = random.nextInt((int) (mBubbleMaxRadius - mBubbleMinRadius));
        radius += mBubbleMinRadius;
        float speedY = random.nextFloat() * mBunbleMaxSpeedY;
        while (speedY < 1) {
            speedY = random.nextFloat() * mBunbleMaxSpeedY;
        }
        bubble.radius = radius;
        bubble.speedY = speedY;
        bubble.x = mWidth / 2;
        bubble.y = mWidth - mBorderWidth - radius - 2;
        float speedX = random.nextFloat() - 0.5f;
        while (speedX == 0) {
            speedX = random.nextFloat() - 0.5f;
        }
        bubble.speedX = speedX * 2;
        mBubbles.add(bubble);
    }

    private void drawWave(Canvas canvas) {
        if (mWaveShader == null) {
            mWavePaint.setShader(null);
            return;
        }
        if (mWavePaint.getShader() == null) {
            mWavePaint.setShader(mWaveShader);
        }
        mShaderMatrix.setScale(1, mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0, (float) mDefaultWaterLevel);
        mShaderMatrix.postTranslate(mWaveShiftRatio * mWidth, (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * mWidth);
        mWaveShader.setLocalMatrix(mShaderMatrix);
        canvas.drawCircle(mWidth / 2f, mWidth / 2f, mWidth / 2F - mBorderWidth, mWavePaint);
    }

    private void drawBorder(Canvas canvas) {
        if (mBorderWidth <= 0) {
            return;
        }
        canvas.drawCircle(mWidth / 2f, mWidth / 2f, (mWidth - mBorderWidth) / 2f - 1f, mBorderPaint);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2 - mBorderWidth, mBgPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        startAnimation();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimation();
        super.onDetachedFromWindow();
    }

    private class Bubble {
        int radius;     // 气泡半径
        float speedY;   // 上升速度
        float speedX;   // 平移速度
        float x;        // 气泡x坐标
        float y;        // 气泡y坐标
    }
}

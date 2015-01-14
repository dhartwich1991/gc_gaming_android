package com.jdapplications.gcgaming.ui;

/**
 * Created by danielhartwich on 1/13/15.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.jdapplications.gcgaming.R;

public class FloatingActionButtonTest extends View {

    public static final boolean DEBUG = false;
    public static final String LOG_TAG = "FloatingButton";

    private static final int Z_TRANSLATION_DURATION = 100;
    private static final float SHADOW_COEF_NORMAL = 0.9f;
    private static final float SHADOW_COEF_PRESSED = 0.7f;

    private final Interpolator hideInterpolator;
    private final Interpolator showInterpolator;
    private final Paint mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float shadowRadius, dx, dy;
    private final int shadowColor;
    private final float elevation;
    private final float pressedElevation;
    private final int duration;
    private final int gravity;
    private final int padding;
    private int margin;
    private Bitmap mBitmap;
    private int mColor;
    private Configuration configuration;
    private boolean mHidden = false;
    /**
     * The FAB button's Y position when it is displayed.
     */
    private float mYDisplayed = -1;
    private Float mInset = null;
    private Integer mTop;

    public FloatingActionButtonTest(Context context) {
        this(context, null);
    }

    public FloatingActionButtonTest(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.layout_gravity,
            android.R.attr.layout_marginBottom,
            android.R.attr.layout_marginTop,
            android.R.attr.paddingBottom,
            android.R.attr.paddingTop,
    };

    public FloatingActionButtonTest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        this.gravity = a.getInt(0, Gravity.NO_GRAVITY);
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
            this.padding = a.getDimensionPixelSize(4, 0);
            this.margin = a.getDimensionPixelSize(2, 0) + padding;
        } else {
            this.padding = a.getDimensionPixelSize(3, 0);
            this.margin = a.getDimensionPixelSize(1, 0) + padding;
        }
        a.recycle();

        final Resources res = getResources();
        final float defaultElevation = res.getDimension(R.dimen.fab_elevation);
        final float defaultElevationPressed = res.getDimension(R.dimen.fab_press_translation_z);
        final int defaultDuration = 100;

        a = getContext().obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, 0);
        mColor = a.getColor(R.styleable.FloatingActionButton_fab_color, Color.WHITE);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setColor(mColor);
        elevation = a.getDimension(R.styleable.FloatingActionButton_fab_elevation, defaultElevation);
        pressedElevation = a.getDimension(R.styleable.FloatingActionButton_fab_elevationPressed, defaultElevationPressed);
        duration = a.getInteger(R.styleable.FloatingActionButton_fab_duration, defaultDuration);

        shadowColor = a.getInteger(R.styleable.FloatingActionButton_shadowColor, Color.argb(110, 0, 0, 0));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || isInEditMode()) {
            hideInterpolator = showInterpolator = new AccelerateDecelerateInterpolator();
            shadowRadius = elevation;
            dx = elevation * 0.15f;
            dy = elevation * 0.3f;
            mButtonPaint.setShadowLayer(SHADOW_COEF_NORMAL * elevation, dx, dy, shadowColor);
        } else {
            //showInterpolator = hideInterpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_linear_in);
            hideInterpolator = showInterpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in);
            shadowRadius = 0;
            dx = dy = 0.0f;
            setElevation(elevation);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
            setClipToOutline(true);
        }

        Drawable drawable = a.getDrawable(R.styleable.FloatingActionButton_drawable);
        if (null != drawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        a.recycle();

        if (DEBUG) Log.d("Log", "init to " + (mHidden ? "hidden" : "shown"));
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int changed = configuration == null ? ActivityInfo.CONFIG_ORIENTATION : configuration.diff(newConfig);
        if (DEBUG)
            Log.d("Log", "onConfigurationChanged from " + configuration + " to " + newConfig + " changed=" + changed);
        if (0 != (changed & (ActivityInfo.CONFIG_LAYOUT_DIRECTION | ActivityInfo.CONFIG_ORIENTATION | ActivityInfo.CONFIG_SCREEN_LAYOUT | ActivityInfo.CONFIG_SCREEN_SIZE))) {
            mYDisplayed = -1;
            mInset = null;
        }
        configuration = newConfig;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mYDisplayed = -1;
        mInset = null;
    }

    @SuppressLint("InlinedApi")
    private int getHiddenPos() {
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
            return 0 - getHeight();
        }

        Configuration configuration = getContext().getResources().getConfiguration();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = mWindowManager.getDefaultDisplay();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
                return display.getHeight();
            } else {
                Point size = new Point();
                display.getSize(size);
                return size.y;
            }
        }

        return ((configuration.screenHeightDp) * configuration.densityDpi) / 160;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void updateShownPosition() {
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
            mYDisplayed = getHeight() + margin;
        } else {
            mYDisplayed = getHiddenPos() - getHeight() - margin;
        }

        if (DEBUG)
            Log.d("LOG", "update mYDisplayed (" + (mHidden ? "hidden" : "shown") + ") = " + mYDisplayed + " is at " + getYPosition() + " height = " + getHeight() + " padding=" + getPaddingBottom() + " statusHeight=" + getStatusBarHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        configuration = null;
    }

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public void setColor(int color) {
        mColor = color;
        mButtonPaint.setColor(mColor);
        invalidate();
    }

    public void setDrawable(Drawable drawable) {
        mBitmap = ((BitmapDrawable) drawable).getBitmap();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius = (float) (getWidth() / 2) - shadowRadius /*- Math.max(dx, dy)*/;
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mButtonPaint);
        if (null != mBitmap) {
            canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2,
                    (getHeight() - mBitmap.getHeight()) / 2, mDrawablePaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // Perform the default behavior
        super.onLayout(changed, left, top, right, bottom);

        // Store the FAB button's displayed Y position if we are not already aware of it
        if (mYDisplayed == -1) {
            updateShownPosition();
        }
        if (mInset == null && !mHidden) {
            mInset = mYDisplayed - getYPosition();
            if (Math.abs(mInset) <= 1.0f)
                mInset = 0.0f;
            if (DEBUG) Log.d("LOG", "update mInset=" + mInset + " mYDisplayed=" + mYDisplayed);
        }

        if (null == mTop || mTop != getTop()) {
            if (DEBUG) Log.v("LOG", "top Changed from " + mTop + " to " + getTop());
            mTop = getTop();

            if (mHidden) {
                // hide better
                TranslateAnimation animator = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                        TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, (getHiddenPos() - getYPosition()) / getHeight()
                );
                animator.setDuration(0);
                startAnimation(animator);
            }
        }
    }

    private float getYPosition() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return getTop();
        }

        return getY();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int color;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            color = mColor;
        } else {
            color = darkenColor(mColor);
        }
        mButtonPaint.setColor(color);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mButtonPaint.setShadowLayer(SHADOW_COEF_PRESSED * elevation, dx, SHADOW_COEF_PRESSED * dy, shadowColor);
            } else {
                animate().translationZ(pressedElevation - elevation).setDuration(Z_TRANSLATION_DURATION);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mButtonPaint.setShadowLayer(SHADOW_COEF_NORMAL * elevation, dx, SHADOW_COEF_NORMAL * dy, shadowColor);
            } else {
                animate().translationZ(0).setDuration(Z_TRANSLATION_DURATION);
            }
        }

        invalidate();
        return super.onTouchEvent(event);
    }

    public boolean hide(boolean hide) {
        // If the hidden state is being updated
        if (mHidden != hide) {

            // Store the new hidden state
            mHidden = hide;

            if (DEBUG)
                Log.d("LOG", "scroll to " + (mHidden ? "hide" : "show") + " = " + (mHidden ? getHiddenPos() : (mYDisplayed - (mInset == null ? 0 : mInset))) + " top=" + getTop());

            // Animate the FAB to it's new Y position
            TranslateAnimation animator;
            if (hide) {
                animator = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                        TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, (getHiddenPos() - getYPosition()) / getHeight()
                );
            } else {
                animator = new TranslateAnimation(
                        TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                        TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                        TranslateAnimation.RELATIVE_TO_SELF, (getHiddenPos() - getYPosition()) / getHeight(),
                        TranslateAnimation.RELATIVE_TO_SELF, 0.0f
                );
            }

            animator.setDuration(duration);
            animator.setInterpolator(hide ? hideInterpolator : showInterpolator);
            animator.setFillAfter(true);
            startAnimation(animator);

            return true;
        }
        return false;
    }

    public boolean isHidden() {
        return mHidden;
    }

    public Interpolator getShowInterpolator() {
        return showInterpolator;
    }

    public Interpolator getHideInterpolator() {
        return hideInterpolator;
    }

    public int getShowHideDuration() {
        return duration;
    }

    public void setShowMargin(int showMargin) {
        if (margin != showMargin + padding) {
            if (DEBUG)
                Log.d("Log", "setShowMargin to " + showMargin + " from " + (margin - padding));

            margin = showMargin + padding;

            if (mYDisplayed != -1) {
                updateShownPosition();

                if (!mHidden) {
                    // move the item to the new mYDisplayed value
                    TranslateAnimation animator = new TranslateAnimation(
                            TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                            TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                            TranslateAnimation.ABSOLUTE, getHiddenPos(),
                            TranslateAnimation.RELATIVE_TO_SELF, mYDisplayed - (mInset == null ? 0 : mInset)
                    );
                    animator.setDuration(0);
                    startAnimation(animator);
                }
            } else {
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if (layoutParams instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutParams;
                    if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
                        params.topMargin = showMargin;
                    } else {
                        params.bottomMargin = showMargin;
                    }
                    setLayoutParams(params);
                }
            }
        }
    }
}
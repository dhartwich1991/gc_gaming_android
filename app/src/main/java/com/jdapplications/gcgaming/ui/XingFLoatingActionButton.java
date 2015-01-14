package com.jdapplications.gcgaming.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.jdapplications.gcgaming.R;

/**
 * Created by danielhartwich on 1/13/15.
 */
public class XingFLoatingActionButton extends FrameLayout implements Checkable {

    private final Paint mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint;
    private Bitmap mBitmap;

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changes.
     */
    public static interface OnCheckedChangeListener {

        /**
         * Called when the checked state of a FAB has changed.
         *
         * @param fabView   The FAB view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(XingFLoatingActionButton fabView, boolean isChecked);
    }

    /**
     * An array of states.
     */
    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    private static final String TAG = "FloatingActionButton";

    // A boolean that tells if the FAB is checked or not.
    private boolean mChecked;

    // A listener to communicate that the FAB has changed it's state
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public XingFLoatingActionButton(Context context) {
        this(context, null, 0, 0);
    }

    public XingFLoatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public XingFLoatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XingFLoatingActionButton(Context context, AttributeSet attrs, int defStyleAttr,
                                    int defStyleRes) {
        super(context, attrs, defStyleAttr);

        setClickable(true);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FloatingActionButton);

        float radius, dx, dy;
        radius = 10.0f;
        dx = 0.0f;
        dy = 10.0f;
        int color = Color.argb(100, 0, 0, 0);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(10, 0, 10, color);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Drawable drawable = a.getDrawable(R.styleable.FloatingActionButton_drawable);
        if (null != drawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        setWillNotDraw(false);

        WindowManager mWindowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);


        // Set the outline provider for this view. The provider is given the outline which it can
        // then modify as needed. In this case we set the outline to be an oval fitting the height
        // and width.
//        setOutlineProvider(new ViewOutlineProvider() {
//            @Override
//            public void getOutline(View view, Outline outline) {
//                outline.setOval(0, 0, getWidth(), getHeight());
//            }
//        });
//
//        // Finally, enable clipping to the outline, using the provider we set above
//        setClipToOutline(true);
    }

    /**
     * Sets the checked/unchecked state of the FAB.
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        // If trying to set the current state, ignore.
        if (checked == mChecked) {
            return;
        }
        mChecked = checked;

        // Now refresh the drawable state (so the icon changes)
        refreshDrawableState();

        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6), paint);
    }


    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * Override performClick() so that we can toggle the checked state when the view is clicked
     */
    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // As we have changed size, we should invalidate the outline so that is the the
        // correct size
//        invalidateOutline();
    }


    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}

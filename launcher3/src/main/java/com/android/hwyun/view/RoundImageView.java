package com.android.hwyun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.android.launcher3.R;


public class RoundImageView extends AppCompatImageView {
    private final int TYPE_FTLLET;
    private final int TYPE_ROUND;
    private int mHeight;
    private int mWidth;
    private Paint paint;
    private float radius;
    private int type;

    public RoundImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RoundImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TYPE_FTLLET = 0;
        this.TYPE_ROUND = 1;
        this.radius = 0.0f;
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.RoundImageView, i, 0);
        this.radius = obtainStyledAttributes.getDimension(0, 0.0f);
        this.type = obtainStyledAttributes.getInt(1, 1);
        obtainStyledAttributes.recycle();
        this.paint = new Paint();
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float f) {
        this.radius = f;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            Bitmap scaleBitmap = getScaleBitmap(drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : null, (float) getWidth(), (float) getHeight());
            int i = this.type;
            if (i == 0) {
                this.paint.reset();
                canvas.drawBitmap(getFilletBitmap(scaleBitmap, this.radius), 0.0f, 0.0f, (Paint) null);
            } else if (i == 1) {
                int min = Math.min(getWidth(), getHeight());
                this.paint.reset();
                canvas.drawBitmap(getRoundBitmap(Bitmap.createScaledBitmap(scaleBitmap, min, min, false), (float) min), 0.0f, 0.0f, (Paint) null);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private static Bitmap getScaleBitmap(Bitmap bitmap, float f, float f2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(f / ((float) width), f2 / ((float) height));
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap getFilletBitmap(Bitmap bitmap, float f) {
        Bitmap createBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Rect rect = new Rect(0, 0, getWidth(), getHeight());
        RectF rectF = new RectF(rect);
        this.paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        this.paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, f, f, this.paint);
        this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, this.paint);
        return createBitmap;
    }

    public Bitmap getRoundBitmap(Bitmap bitmap, float f) {
        int i = (int) f;
        Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.paint.setAntiAlias(true);
        float f2 = f / 2.0f;
        canvas.drawCircle(f2, f2, f2, this.paint);
        this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, this.paint);
        return createBitmap;
    }
}
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.android.hwyun.batchinstall.bean.DownloadFileInfo;
import com.android.hwyun.batchinstall.hwcloud.DownloadManager;
import com.android.hwyun.batchinstall.hwcloud.DownloadViewHolder;
import com.android.launcher3.Application.MainApplication;
import com.android.launcher3.IconCache.IconLoadRequest;
import com.android.launcher3.model.PackageItemInfo;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;

/**
 * TextView that draws a bubble behind the text. We cannot use a LineBackgroundSpan
 * because we want to make the bubble taller than the text and TextView's clip is
 * too aggressive.
 */
public class BubbleTextView extends TextView
        implements BaseRecyclerViewFastScrollBar.FastScrollFocusableView, DownloadViewHolder {

    private static SparseArray<Theme> sPreloaderThemes = new SparseArray<Theme>(2);

    private static final float SHADOW_LARGE_RADIUS = 4.0f;
    private static final float SHADOW_SMALL_RADIUS = 1.75f;
    private static final float SHADOW_Y_OFFSET = 2.0f;
    private static final int SHADOW_LARGE_COLOUR = 0xDD000000;
    private static final int SHADOW_SMALL_COLOUR = 0xCC000000;

    private static final int DISPLAY_WORKSPACE = 0;
    private static final int DISPLAY_ALL_APPS = 1;

    private static final float FAST_SCROLL_FOCUS_MAX_SCALE = 1.15f;
    private static final int FAST_SCROLL_FOCUS_MODE_NONE = 0;
    private static final int FAST_SCROLL_FOCUS_MODE_SCALE_ICON = 1;
    private static final int FAST_SCROLL_FOCUS_MODE_DRAW_CIRCLE_BG = 2;
    private static final int FAST_SCROLL_FOCUS_FADE_IN_DURATION = 175;
    private static final int FAST_SCROLL_FOCUS_FADE_OUT_DURATION = 125;

    private final Launcher mLauncher;
    private Drawable mIcon;
    private Bitmap mIconCorner;
    private final Drawable mBackground;
    private final CheckLongPressHelper mLongPressHelper;
    private final HolographicOutlineHelper mOutlineHelper;
    private final StylusEventHelper mStylusEventHelper;

    private boolean mBackgroundSizeChanged;

    private Bitmap mPressedBackground;

    private float mSlop;

    private final boolean mDeferShadowGenerationOnTouch;
    private final boolean mCustomShadowsEnabled;
    private final boolean mLayoutHorizontal;
    private final int mIconSize;
    private int mTextColor;

    private boolean mStayPressed;
    private boolean mIgnorePressedStateChange;
    private boolean mDisableRelayout = false;

    private ObjectAnimator mFastScrollFocusAnimator;
    private Paint mFastScrollFocusBgPaint;
    private float mFastScrollFocusFraction;
    private boolean mFastScrollFocused;
    private final int mFastScrollMode = FAST_SCROLL_FOCUS_MODE_SCALE_ICON;

    private IconLoadRequest mIconLoadRequest;

    private int mDownloadPregress = 0;
    private String mDownloadId = "";

    public BubbleTextView(Context context) {
        this(context, null, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLauncher = (Launcher) context;
        DeviceProfile grid = mLauncher.getDeviceProfile();

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BubbleTextView, defStyle, 0);
        mCustomShadowsEnabled = a.getBoolean(R.styleable.BubbleTextView_customShadows, true);
        mLayoutHorizontal = a.getBoolean(R.styleable.BubbleTextView_layoutHorizontal, false);
        mDeferShadowGenerationOnTouch =
                a.getBoolean(R.styleable.BubbleTextView_deferShadowGeneration, false);

        int display = a.getInteger(R.styleable.BubbleTextView_iconDisplay, DISPLAY_WORKSPACE);
        int defaultIconSize = grid.iconSizePx;
        if (display == DISPLAY_WORKSPACE) {
//            setTextSize(TypedValue.COMPLEX_UNIT_PX, grid.iconTextSizePx);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, grid.inv.iconTextSize);
        } else if (display == DISPLAY_ALL_APPS) {
//            setTextSize(TypedValue.COMPLEX_UNIT_PX, grid.allAppsIconTextSizePx);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, grid.inv.iconTextSize);
            defaultIconSize = grid.allAppsIconSizePx;
        }

        mIconSize = a.getDimensionPixelSize(R.styleable.BubbleTextView_iconSizeOverride,
                defaultIconSize);

        a.recycle();

        if (mCustomShadowsEnabled) {
            // Draw the background itself as the parent is drawn twice.
            mBackground = getBackground();
            setBackground(null);
        } else {
            mBackground = null;
        }

        mLongPressHelper = new CheckLongPressHelper(this);
        mStylusEventHelper = new StylusEventHelper(this);

        mOutlineHelper = HolographicOutlineHelper.obtain(getContext());
        if (mCustomShadowsEnabled) {
            setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
        }

        if (mFastScrollMode == FAST_SCROLL_FOCUS_MODE_DRAW_CIRCLE_BG) {
            mFastScrollFocusBgPaint = new Paint();
            mFastScrollFocusBgPaint.setAntiAlias(true);
            mFastScrollFocusBgPaint.setColor(
                    getResources().getColor(R.color.container_fastscroll_thumb_active_color));
        }

        setAccessibilityDelegate(LauncherAppState.getInstance().getAccessibilityDelegate());
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache) {
        applyFromShortcutInfo(info, iconCache, false);
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache,
                                      boolean promiseStateChanged) {

        Bitmap b = info.getIcon(iconCache);

        //下载的快捷方式默认有个角标
        if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
            Log.i("shortcut", "applyFromShortcutInfo " + info.getIntent().getPackage());

            mIconCorner = ImageUtils.getBitmap(R.drawable.app_download_corner);
            Rect dst = mLauncher.getIconCornerRect(b);
            //它得先变成那么大。然后再合并
            mIconCorner = ImageUtils.scale(mIconCorner, dst.width(), dst.height());
            b = ImageUtils.addImageWatermark(b, mIconCorner, dst.left, dst.top, 255);

            //算法有问题，不同大小图片会导致可能会把图标变成圆的，所以注释
//            //转圆角，为了和原图标的圆角贴合
//            DeviceProfile grid = mLauncher.getDeviceProfile();
//            float mRadius = (9 * (grid.iconSizePx / 24));
//            b = ImageUtils.toRoundCorner(b, mRadius);
        }

        FastBitmapDrawable iconDrawable = mLauncher.createIconDrawable(b);
        iconDrawable.setGhostModeEnabled(info.isDisabled != 0);

        setIcon(iconDrawable, mIconSize, iconCache.getIconScale());
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }

        String idByPackageName = DownloadManager.getImpl().getIdByPackageName(info.getIntent().getPackage());
        if (!StringUtils.isEmpty(idByPackageName)) {//检查一下是否正在下载
            setText(MainApplication.getContext().getString(R.string.status_installing));
        } else {
            setText(info.title);
        }
        setTag(info);

        if (promiseStateChanged || info.isPromise()) {
            applyState(promiseStateChanged);
        }
    }

    public void applyFromApplicationInfo(AppInfo info) {
        setIcon(mLauncher.createIconDrawable(info.iconBitmap), mIconSize, 1f);
        setText(info.title);
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }
        // We don't need to check the info since it's not a ShortcutInfo
        super.setTag(info);

        // Verify high res immediately
        verifyHighRes();
    }

    public void applyFromPackageItemInfo(PackageItemInfo info) {
        setIcon(mLauncher.createIconDrawable(info.iconBitmap), mIconSize, 1f);
        setText(info.title);
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }
        // We don't need to check the info since it's not a ShortcutInfo
        super.setTag(info);

        // Verify high res immediately
        verifyHighRes();
    }

    /**
     * Overrides the default long press timeout.
     */
    public void setLongPressTimeout(int longPressTimeout) {
        mLongPressHelper.setLongPressTimeout(longPressTimeout);
    }

    @Override
    protected boolean setFrame(int left, int top, int right, int bottom) {
        if (getLeft() != left || getRight() != right || getTop() != top || getBottom() != bottom) {
            mBackgroundSizeChanged = true;
        }
        return super.setFrame(left, top, right, bottom);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBackground || super.verifyDrawable(who);
    }

    @Override
    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
        }
        super.setTag(tag);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (!mIgnorePressedStateChange) {
            updateIconState();
        }
    }

    /**
     * Returns the icon for this view.
     */
    public Drawable getIcon() {
        return mIcon;
    }

    /**
     * Returns whether the layout is horizontal.
     */
    public boolean isLayoutHorizontal() {
        return mLayoutHorizontal;
    }

    private void updateIconState() {
        if (mIcon instanceof FastBitmapDrawable) {
            ((FastBitmapDrawable) mIcon).setPressed(isPressed() || mStayPressed);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Call the superclass onTouchEvent first, because sometimes it changes the state to
        // isPressed() on an ACTION_UP
        boolean result = super.onTouchEvent(event);

        // Check for a stylus button press, if it occurs cancel any long press checks.
        if (mStylusEventHelper.checkAndPerformStylusEvent(event)) {
            mLongPressHelper.cancelLongPress();
            result = true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // So that the pressed outline is visible immediately on setStayPressed(),
                // we pre-create it on ACTION_DOWN (it takes a small but perceptible amount of time
                // to create it)
                if (!mDeferShadowGenerationOnTouch && mPressedBackground == null) {
                    mPressedBackground = mOutlineHelper.createMediumDropShadow(this);
                }

                // If we're in a stylus button press, don't check for long press.
                if (!mStylusEventHelper.inStylusButtonPressed()) {
                    mLongPressHelper.postCheckForLongPress();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // If we've touched down and up on an item, and it's still not "pressed", then
                // destroy the pressed outline
                if (!isPressed()) {
                    mPressedBackground = null;
                }

                mLongPressHelper.cancelLongPress();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!Utilities.pointInView(this, event.getX(), event.getY(), mSlop)) {
                    mLongPressHelper.cancelLongPress();
                }
                break;
        }
        return result;
    }

    void setStayPressed(boolean stayPressed) {
        mStayPressed = stayPressed;
        if (!stayPressed) {
            mPressedBackground = null;
        } else {
            if (mPressedBackground == null) {
                mPressedBackground = mOutlineHelper.createMediumDropShadow(this);
            }
        }

        // Only show the shadow effect when persistent pressed state is set.
        ViewParent parent = getParent();
        if (parent != null && parent.getParent() instanceof BubbleTextShadowHandler) {
            ((BubbleTextShadowHandler) parent.getParent()).setPressedIcon(
                    this, mPressedBackground);
        }

        updateIconState();
    }

    void clearPressedBackground() {
        setPressed(false);
        setStayPressed(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (super.onKeyDown(keyCode, event)) {
            // Pre-create shadow so show immediately on click.
            if (mPressedBackground == null) {
                mPressedBackground = mOutlineHelper.createMediumDropShadow(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Unlike touch events, keypress event propagate pressed state change immediately,
        // without waiting for onClickHandler to execute. Disable pressed state changes here
        // to avoid flickering.
        mIgnorePressedStateChange = true;
        boolean result = super.onKeyUp(keyCode, event);

        mPressedBackground = null;
        mIgnorePressedStateChange = false;
        updateIconState();
        return result;
    }

    @Override
    public String getDownloadID() {
        return mDownloadId;
    }

    @Override
    public void setDownloadID(String downloadID) {
        this.mDownloadId = downloadID;
    }

    @Override
    public void updateProgress(DownloadFileInfo fileInfo) {
        mDownloadPregress = fileInfo.getProgress();
        setText(MainApplication.getContext().getString(R.string.status_downloading, mDownloadPregress));
        postInvalidate();
    }

    @Override
    public void downloadError(int errorCode) {
        mDownloadPregress = 0;
        ItemInfo itemInfo = (ItemInfo) super.getTag();
        setText(itemInfo.title.toString());
        postInvalidate();
    }

    @Override
    public void downloadSuccess(DownloadFileInfo fileInfo) {
        mDownloadPregress = 100;
        setText(MainApplication.getContext().getString(R.string.status_installing));
        postInvalidate();
    }

    @Override
    public void installSuccess(DownloadFileInfo fileInfo) {
        ItemInfo itemInfo = (ItemInfo) super.getTag();
        setText(itemInfo.title.toString());
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDownloadPregress > 0 && mDownloadPregress < 100) {
            Paint mSquarePaint = new Paint(); //矩形
            mSquarePaint.setAntiAlias(true);
            mSquarePaint.setColor(mLauncher.getResources().getColor(R.color.dark_color));
            mSquarePaint.setStyle(Paint.Style.FILL);

            Paint mRingPaint = new Paint();  //圆环
            mRingPaint.setAntiAlias(true);
            mRingPaint.setColor(mLauncher.getResources().getColor(R.color.alpha_color2));
            mRingPaint.setStyle(Paint.Style.STROKE);

            Paint mCircularPaint = new Paint(); //扇形
            mCircularPaint.setAntiAlias(true);
            mCircularPaint.setColor(mLauncher.getResources().getColor(R.color.alpa_black));
            mCircularPaint.setStyle(Paint.Style.FILL);

            //圆心
            DeviceProfile grid = mLauncher.getDeviceProfile();
            int iconCenterX = getScrollX() + (getWidth() / 2);
            int iconCenterY = getScrollY() + getPaddingTop() + (grid.iconSizePx / 2);

            float mStrokeWidth = (grid.iconSizePx / 12);
            float mRadius = (11 * (grid.iconSizePx / 24));
            //进度圆环的半径
            float mCircularRadius = (mRadius - mStrokeWidth / 2.0F);

            mRingPaint.setStrokeWidth(mStrokeWidth);

            Rect localRect = new Rect();
            localRect.left = iconCenterX - grid.iconSizePx / 2;
            localRect.top = iconCenterY - grid.iconSizePx / 2;
            localRect.right = iconCenterX + grid.iconSizePx / 2;
            localRect.bottom = iconCenterY + grid.iconSizePx / 2;
            RectF localRectF1 = new RectF(localRect);
            float f = 14 * getWidth() / 128;
            canvas.drawRoundRect(localRectF1, f, f, mSquarePaint);
            canvas.drawCircle(iconCenterX, iconCenterY, mRadius, mRingPaint);

            //进度条显示
            RectF localRectF2 = new RectF();
            localRectF2.left = (iconCenterX - mCircularRadius);
            localRectF2.top = (iconCenterY - mCircularRadius);
            localRectF2.right = (localRectF2.left + 2.0F * mCircularRadius);
            localRectF2.bottom = (localRectF2.top + 2.0F * mCircularRadius);
            canvas.drawArc(localRectF2, -90.0F, -(float) (3.6D * (100 - mDownloadPregress)), true, mCircularPaint);
        } else {
//            if(mIconCorner != null) {
//                LogUtils.iTag("shortcut","ondraw");
//
//                Paint mSquarePaint = new Paint();
//                mSquarePaint.setAntiAlias(true);
//
//                Rect dst = mIcon.getBounds();
//                dst.left = dst.right*3/4;
//                dst.top = dst.bottom*3/4;
//
//                canvas.drawBitmap(mIconCorner,
//                        new Rect(0,0, mIconCorner.getWidth(), mIconCorner.getHeight()),
//                        dst,
//                        mSquarePaint);
//            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mCustomShadowsEnabled) {
            // Draw the fast scroll focus bg if we have one
            if (mFastScrollMode == FAST_SCROLL_FOCUS_MODE_DRAW_CIRCLE_BG &&
                    mFastScrollFocusFraction > 0f) {
                DeviceProfile grid = mLauncher.getDeviceProfile();
                int iconCenterX = getScrollX() + (getWidth() / 2);
                int iconCenterY = getScrollY() + getPaddingTop() + (grid.iconSizePx / 2);
                canvas.drawCircle(iconCenterX, iconCenterY,
                        mFastScrollFocusFraction * (getWidth() / 2), mFastScrollFocusBgPaint);
            }

            super.draw(canvas);

            return;
        }

        final Drawable background = mBackground;
        if (background != null) {
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();

            if (mBackgroundSizeChanged) {
                background.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
                mBackgroundSizeChanged = false;
            }

            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }

        // If text is transparent, don't draw any shadow
        if (getCurrentTextColor() == getResources().getColor(android.R.color.transparent)) {
            getPaint().clearShadowLayer();
            super.draw(canvas);
            return;
        }

        // We enhance the shadow by drawing the shadow twice
        getPaint().setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
        super.draw(canvas);
        canvas.save();
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(),
                getScrollX() + getWidth(),
                getScrollY() + getHeight(), Region.Op.INTERSECT);
        getPaint().setShadowLayer(SHADOW_SMALL_RADIUS, 0.0f, 0.0f, SHADOW_SMALL_COLOUR);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mBackground != null) mBackground.setCallback(this);

        if (mIcon instanceof PreloadIconDrawable) {
            ((PreloadIconDrawable) mIcon).applyPreloaderTheme(getPreloaderTheme());
        }
        mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBackground != null) mBackground.setCallback(null);
    }

    @Override
    public void setTextColor(int color) {
        mTextColor = color;
        super.setTextColor(color);
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        mTextColor = colors.getDefaultColor();
        super.setTextColor(colors);
    }

    public void setTextVisibility(boolean visible) {
        Resources res = getResources();
        if (visible) {
            super.setTextColor(mTextColor);
        } else {
            super.setTextColor(res.getColor(android.R.color.transparent));
        }
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mLongPressHelper.cancelLongPress();
    }

    public void applyState(boolean promiseStateChanged) {
        if (getTag() instanceof ShortcutInfo) {
            ShortcutInfo info = (ShortcutInfo) getTag();
            final boolean isPromise = info.isPromise();
            final int progressLevel = isPromise ?
                    ((info.hasStatusFlag(ShortcutInfo.FLAG_INSTALL_SESSION_ACTIVE) ?
                            info.getInstallProgress() : 0)) : 100;

            if (mIcon != null) {
                final PreloadIconDrawable preloadDrawable;
                if (mIcon instanceof PreloadIconDrawable) {
                    preloadDrawable = (PreloadIconDrawable) mIcon;
                } else {
                    preloadDrawable = new PreloadIconDrawable(mIcon, getPreloaderTheme());
                    setIcon(preloadDrawable, mIconSize, 1f);
                }

                preloadDrawable.setLevel(progressLevel);
                if (promiseStateChanged) {
                    preloadDrawable.maybePerformFinishedAnimation();
                }
            }
        }
    }

    private Theme getPreloaderTheme() {
        Object tag = getTag();
        int style = ((tag != null) && (tag instanceof ShortcutInfo) &&
                (((ShortcutInfo) tag).container >= 0)) ? R.style.PreloadIcon_Folder
                : R.style.PreloadIcon;
        Theme theme = sPreloaderThemes.get(style);
        if (theme == null) {
            theme = getResources().newTheme();
            theme.applyStyle(style, true);
            sPreloaderThemes.put(style, theme);
        }
        return theme;
    }

    /**
     * Sets the icon for this view based on the layout direction.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Drawable setIcon(Drawable icon, int iconSize, float iconScale) {
        mIcon = icon;
        if (iconSize != -1) {
            mIcon.setBounds(0, 0, (int) (iconSize * iconScale), iconSize);
        }

        if (mLayoutHorizontal) {
            if (Utilities.ATLEAST_JB_MR1) {
                setCompoundDrawablesRelative(mIcon, null, null, null);
            } else {
                setCompoundDrawables(mIcon, null, null, null);
            }
        } else {
            setCompoundDrawables(null, mIcon, null, null);
        }
        return icon;
    }

    @Override
    public void requestLayout() {
        if (!mDisableRelayout) {
            super.requestLayout();
        }
    }

    /**
     * Applies the item info if it is same as what the view is pointing to currently.
     */
    public void reapplyItemInfo(final ItemInfo info) {
        if (getTag() == info) {
            mIconLoadRequest = null;
            mDisableRelayout = true;
            if (info instanceof AppInfo) {
                applyFromApplicationInfo((AppInfo) info);
            } else if (info instanceof ShortcutInfo) {
                applyFromShortcutInfo((ShortcutInfo) info,
                        LauncherAppState.getInstance().getIconCache());
                if ((info.rank < FolderIcon.NUM_ITEMS_IN_PREVIEW) && (info.container >= 0)) {
                    View folderIcon =
                            mLauncher.getWorkspace().getHomescreenIconByItemId(info.container);
                    if (folderIcon != null) {
                        folderIcon.invalidate();
                    }
                }
            } else if (info instanceof PackageItemInfo) {
                applyFromPackageItemInfo((PackageItemInfo) info);
            }
            mDisableRelayout = false;
        }
    }

    /**
     * Verifies that the current icon is high-res otherwise posts a request to load the icon.
     */
    public void verifyHighRes() {
        if (mIconLoadRequest != null) {
            mIconLoadRequest.cancel();
            mIconLoadRequest = null;
        }
        if (getTag() instanceof AppInfo) {
            AppInfo info = (AppInfo) getTag();
            if (info.usingLowResIcon) {
                mIconLoadRequest = LauncherAppState.getInstance().getIconCache()
                        .updateIconInBackground(BubbleTextView.this, info);
            }
        } else if (getTag() instanceof ShortcutInfo) {
            ShortcutInfo info = (ShortcutInfo) getTag();
            if (info.usingLowResIcon) {
                mIconLoadRequest = LauncherAppState.getInstance().getIconCache()
                        .updateIconInBackground(BubbleTextView.this, info);
            }
        } else if (getTag() instanceof PackageItemInfo) {
            PackageItemInfo info = (PackageItemInfo) getTag();
            if (info.usingLowResIcon) {
                mIconLoadRequest = LauncherAppState.getInstance().getIconCache()
                        .updateIconInBackground(BubbleTextView.this, info);
            }
        }
    }

    // Setters & getters for the animation
    public void setFastScrollFocus(float fraction) {
        mFastScrollFocusFraction = fraction;
        if (mFastScrollMode == FAST_SCROLL_FOCUS_MODE_SCALE_ICON) {
            setScaleX(1f + fraction * (FAST_SCROLL_FOCUS_MAX_SCALE - 1f));
            setScaleY(1f + fraction * (FAST_SCROLL_FOCUS_MAX_SCALE - 1f));
        } else {
            invalidate();
        }
    }

    public float getFastScrollFocus() {
        return mFastScrollFocusFraction;
    }

    @Override
    public void setFastScrollFocused(final boolean focused, boolean animated) {
        if (mFastScrollMode == FAST_SCROLL_FOCUS_MODE_NONE) {
            return;
        }

        if (mFastScrollFocused != focused) {
            mFastScrollFocused = focused;

            if (animated) {
                // Clean up the previous focus animator
                if (mFastScrollFocusAnimator != null) {
                    mFastScrollFocusAnimator.cancel();
                }
                mFastScrollFocusAnimator = ObjectAnimator.ofFloat(this, "fastScrollFocus",
                        focused ? 1f : 0f);
                if (focused) {
                    mFastScrollFocusAnimator.setInterpolator(new DecelerateInterpolator());
                } else {
                    mFastScrollFocusAnimator.setInterpolator(new AccelerateInterpolator());
                }
                mFastScrollFocusAnimator.setDuration(focused ?
                        FAST_SCROLL_FOCUS_FADE_IN_DURATION : FAST_SCROLL_FOCUS_FADE_OUT_DURATION);
                mFastScrollFocusAnimator.start();
            } else {
                mFastScrollFocusFraction = focused ? 1f : 0f;
            }
        }
    }

    /**
     * Interface to be implemented by the grand parent to allow click shadow effect.
     */
    public static interface BubbleTextShadowHandler {
        void setPressedIcon(BubbleTextView icon, Bitmap background);
    }
}

package com.mineyang.yang.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.mineyang.yang.AppMainApplication;

/**
 * 获得屏幕相关的辅助类
 */
public class ScreenUtils {
    /**
     * 0为宽度  1为高度
     */
    private static final int WIDTH_DIRECTION = 0;
    private static final int HEIGHT_DIRECTION = 1;
    private static final Context context = AppMainApplication.Instance;

    private ScreenUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获得屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return getScreenWidthAndHeight(context, WIDTH_DIRECTION);
    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return getScreenWidthAndHeight(context, HEIGHT_DIRECTION);
    }

    private static int getScreenWidthAndHeight(Context context, int direction) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        if (WIDTH_DIRECTION == direction) {
            return outMetrics.widthPixels;
        } else {
            return outMetrics.heightPixels;
        }
    }

    public static float getScreenRatio() {
        return (float) getScreenHeight() / (float) getScreenWidth();
    }

    //和正常屏幕比
    public static float getScreenToBaseRatio() {
        return (float) getScreenRatio() / 1.77f;
    }

    /**
     * 获得状态栏的高度
     *
     * @return
     */
    public static int getStatusHeight() {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = AppMainApplication.Instance.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth();
        int height = getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth();
        int height = getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取屏幕尺寸，不包括虚拟功能高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeightWithoutNavigator(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    public static int getNavigatorHeight(Activity activity) {
        return getScreenHeight() - getScreenHeightWithoutNavigator(activity);
    }

    /**
     * 判断底部navigator是否已经显示
     *
     * @param activity
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean hasSoftKeys(Activity activity) {
        Display d = activity.getWindowManager().getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }
}
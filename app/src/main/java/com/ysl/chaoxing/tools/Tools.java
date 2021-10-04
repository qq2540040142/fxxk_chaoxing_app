package com.ysl.chaoxing.tools;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by ender on 2021/10/3 19:17
 * @author ysl
 */
public class Tools {
    /**
     * 存储String数据
     *
     * @param activity activity
     * @param key 数据的key
     * @param data 要存入的数据
     * */
    public static void saveStringData(Activity activity, String key, String data) {
        activity.getSharedPreferences("data", MODE_PRIVATE)
                .edit()
                .putString(key, data)
                .apply();
    }
    /**
     * 获取String数据
     *
     * @param activity activity
     * @param key 数据的key
     *
     * @return string
     * */
    public static String getStringData(Activity activity, String key) {
        return activity.getSharedPreferences("data", MODE_PRIVATE).getString(key, null);
    }

    /**
     * 修复小米手机toast带应用名称问题
     */
    public static void miuiToast(Context context, CharSequence text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 设置状态栏全透明
     *
     * @param activity 需要设置的activity
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
    }

    /**
     * 使状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个flag contentView才能延伸到状态栏
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //状态栏覆盖在contentView上面，设置透明使contentView的背景透出来
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * 修改状态栏字体颜色
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 通过状态栏资源id来获取状态栏高度
     *
     * @param context 上下文
     */
    public static int getStatusBarByResId(Context context) {
        int height = 0;
        // 获取状态栏资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            try {
                height = context.getResources().getDimensionPixelSize(resourceId);
            } catch (Exception e) {
                Log.e("获取状态栏高度", "getStatusBarByResId: ", e);
            }
        }
        return height;
    }
    /**
     * 动态获取存储权限
     */
    public static void requestPermission(Context context, Activity activity) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }
}

package com.ysl.chaoxing.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.ysl.chaoxing.databinding.ActivityMainBinding;
import com.ysl.chaoxing.fragment.StartFragment;
import com.ysl.chaoxing.tools.Tools;

/**
 * Created by ender on 2021/10/3 14:06
 *
 * @author ysl
 */
public class MainActivity extends FragmentActivity {
    private ActivityMainBinding binding;
    /**
     * 返回键flag
     */
    private boolean mIsExit = false;
    public static int fragmentFlag = 0;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        //沉浸式状态栏
        Tools.setTransparent(this);
        // 设置状态栏字体为黑色
        Tools.setAndroidNativeLightStatusBar(this, true);
        //Tools.requestPermission(getApplicationContext(), this);
        setContentView(binding.getRoot());
        initPython();
    }

    private void initPython() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    /**
     * 返回键监听
     */
    @Override
    public void onBackPressed() {
        /*
         * 因为嵌套fragment只能拦截一次返回键
         * 第二次会被activity消费
         * 设个flag判断是否消费拦截事件
         * 当flag为0时,则为不需要消费拦截事件
         * */
        if (fragmentFlag == 0) {
            if (mIsExit) {
                super.onBackPressed();
                this.finish();
            } else {
                Tools.miuiToast(getApplicationContext(), "再按一次退出");
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
        } else {
            //每返回一个fragment,flag自减,直至为0
            fragmentFlag--;
            if (StartFragment.thread.isAlive()) {
                StartFragment.thread.interrupt();
            }
            getSupportFragmentManager().popBackStack();
        }
    }
}


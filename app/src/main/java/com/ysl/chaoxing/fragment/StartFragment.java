package com.ysl.chaoxing.fragment;

import static com.ysl.chaoxing.tools.Tools.getStatusBarByResId;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.ysl.chaoxing.R;
import com.ysl.chaoxing.activity.MainActivity;
import com.ysl.chaoxing.databinding.FragmentStartBinding;
import com.ysl.chaoxing.tools.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ender on 2021/10/4 13:27
 *
 * @author ysl
 */
public class StartFragment extends Fragment {
    private FragmentStartBinding binding;
    public int num;
    public PyObject session;
    public static StartFragment instance;
    public static Thread thread;
    @SuppressLint("SimpleDateFormat")
    public SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

    public StartFragment(int position, PyObject session) {
        this.num = position;
        this.session = session;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStartBinding.inflate(inflater, container, false);
        instance = (StartFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        addText("注:如需返回上个界面,请点击右上角退出,否则会引发异常闪退!", requireActivity().getColor(R.color.red));
        allListener();
        setTabLayoutMarginTop();
        startThread();
        return binding.getRoot();
    }


    public void addText(String s) {
        TextView textView = new TextView(requireContext());
        textView.setText(String.format("[%s]%s", df.format(new Date()), s));
        textView.setTextSize(16);
        textView.setTextColor(requireActivity().getColor(R.color.black));
        binding.fragmentStartCanvas.post(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    binding.fragmentStartCanvas.addView(textView);
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.fragmentStartScrollview.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                            if ("python已安全退出!".equals(s)){
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        }
                    });

                }
            }
        });
    }

    public void addText(String s, int color) {
        TextView textView = new TextView(requireContext());
        textView.setText(s);
        textView.setTextSize(16);
        textView.setTextColor(color);
        binding.fragmentStartCanvas.post(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    binding.fragmentStartCanvas.addView(textView);
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.fragmentStartScrollview.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                        }
                    });

                }
            }
        });
    }

    /**
     * 添加一个进度条
     *
     * @param s 进度条的内容
     * @return 进度条的view
     */
    public TextView addProgressBar(String s) {
        TextView textView = new TextView(requireContext());
        textView.setText(s);
        textView.setTextSize(16);
        textView.setTextColor(requireActivity().getColor(R.color.black));
        binding.fragmentStartCanvas.post(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    binding.fragmentStartCanvas.addView(textView);
                }
            }
        });
        return textView;
    }

    /**
     * 更新进度条
     *
     * @param textView 进度条的textview
     * @param s        进度条
     */
    public void updateProgressBar(TextView textView, String s) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(s);
                textView.setTextSize(16);
                textView.setTextColor(requireActivity().getColor(R.color.black));
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.fragmentStartScrollview.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                    }
                });
            }
        });
    }

    public static StartFragment getInstance() {
        return instance;
    }

    private void startThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    Python.getInstance().getModule("main")
                            .callAttr("main"
                                    , Tools.getStringData(requireActivity(), "userName")
                                    , Tools.getStringData(requireActivity(), "userId")
                                    , session, num);
                }
            }
        });
        thread.start();
    }

    private void allListener() {
        binding.fragmentStartExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fragmentFlag--;
                Python.getInstance().getModule("main")
                        .callAttr("set_running", false);
            }
        });
    }

    /**
     * 根据状态栏高度设置tabLayout的marginTop
     */
    private void setTabLayoutMarginTop() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) binding.fragmentStartToolbar.getLayoutParams();
        lp.topMargin = getStatusBarByResId(requireContext());
        binding.fragmentStartToolbar.setLayoutParams(lp);
    }

}

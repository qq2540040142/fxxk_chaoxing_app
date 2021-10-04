package com.ysl.chaoxing.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.ysl.chaoxing.R;
import com.ysl.chaoxing.databinding.FragmentStartBinding;
import com.ysl.chaoxing.tools.Tools;

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

    public StartFragment(int position, PyObject session) {
        this.num = position;
        this.session = session;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStartBinding.inflate(inflater, container, false);
        instance = (StartFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Python python = Python.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                python.getModule("main")
                        .callAttr("main"
                                , Tools.getStringData(requireActivity(), "userName")
                                ,Tools.getStringData(requireActivity(),"userId")
                                , session, num);
            }
        }).start();
        return binding.getRoot();
    }

    public void addText(String s) {
        TextView textView = new TextView(requireContext());
        textView.setText(s);
        textView.setTextSize(16);
        textView.setTextColor(requireActivity().getColor(R.color.black));
        binding.fragmentStartCanvas.post(new Runnable() {
            @Override
            public void run() {
                binding.fragmentStartCanvas.addView(textView);
                binding.fragmentStartScrollview.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
            }
        });
    }

    public static StartFragment getInstance() {
        return instance;
    }
}

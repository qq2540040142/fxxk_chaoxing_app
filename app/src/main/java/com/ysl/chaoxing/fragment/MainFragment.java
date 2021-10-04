package com.ysl.chaoxing.fragment;

import static com.ysl.chaoxing.tools.Tools.getStatusBarByResId;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.ysl.chaoxing.R;
import com.ysl.chaoxing.adapter.AllSubjectRecyclerAdapter;
import com.ysl.chaoxing.data.AllSubject;
import com.ysl.chaoxing.databinding.FragmentMainBinding;
import com.ysl.chaoxing.tools.SpacesItemDecoration;
import com.ysl.chaoxing.tools.Tools;

import java.util.Objects;

/**
 * Created by ender on 2021/10/3 20:08
 * @author ysl
 */
public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private PyObject session;
    private String TAG = "MainFragment";

    /**
     * 构造方法
     *
     * @param session 登录成功获取到的session
     * */
    public MainFragment(PyObject session){
        this.session = session;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater,container,false);
        initRecyclerView();
        return binding.getRoot();
    }

    /**
     * 获取用户的所有课程
     * */
    private AllSubject initSubject(){
        Python python = Python.getInstance();
        PyObject pyObject = python.getModule("foreWork").callAttr("find_courses",Tools.getStringData(requireActivity(),"userName"),session);
        return pyObject.toJava(AllSubject.class);
    }

    private void initRecyclerView(){
        //为每个item设置间距
        binding.fragmentMainRecyclerview.addItemDecoration(new SpacesItemDecoration(27));
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.fragmentMainRecyclerview.setLayoutManager(manager);
        AllSubjectRecyclerAdapter adapter = new AllSubjectRecyclerAdapter(requireContext(),initSubject());
        adapter.setOnItemClick(new AllSubjectRecyclerAdapter.OnItemClick() {
            @Override
            public void itemClick(View view, int position) {
                switchFragment(new StartFragment(position,session));
            }
        });
        binding.fragmentMainRecyclerview.setAdapter(adapter);
        setTabLayoutMarginTop();
    }

    /**
     * 根据状态栏高度设置tabLayout的marginTop
     */
    private void setTabLayoutMarginTop() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.fragmentMainRecyclerview.getLayoutParams();
        lp.topMargin = getStatusBarByResId(requireContext());
        binding.fragmentMainRecyclerview.setLayoutParams(lp);
    }

    /**
     * fragment跳转至fragment
     *
     * @param nextFragment 要跳转的fragment
     */
    private void switchFragment(Fragment nextFragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.switch_fragment_enter,
                        R.animator.switch_fragment_exit,
                        R.animator.switch_fragment_pop_enter,
                        R.animator.switch_fragment_pop_exit);
        if (!nextFragment.isAdded()) {
            transaction
                    .hide(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)))
                    .add(R.id.nav_host_fragment, nextFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else {
            transaction
                    .hide(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)))
                    .show(nextFragment)
                    //压栈式添加
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }
}

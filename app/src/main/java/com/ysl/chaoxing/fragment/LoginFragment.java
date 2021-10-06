package com.ysl.chaoxing.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.ysl.chaoxing.R;
import com.ysl.chaoxing.databinding.FragmentLoginBinding;
import com.ysl.chaoxing.tools.Tools;

import java.util.Objects;

/**
 * Created by ender on 2021/10/3 19:20
 * @author ysl
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    /**是否记住密码标识*/
    private boolean isCheck;
    private String TAG = "LoginFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        //刚进页面获取一下本地是否有存储的密码
        getSaveUsernameAndPassword();
        login();
        return binding.getRoot();
    }

    /**
     *登录操作
     * */
    private void login(){
        binding.fragmentLoginLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Python python = Python.getInstance();
                PyObject pyObject = python.getModule("getUser")
                        .callAttr("login"
                                , Objects.requireNonNull(binding.fragmentLoginNameEditText.getText()).toString()
                                , Objects.requireNonNull(binding.fragmentLoginPassWordfEditText.getText()).toString());
                if (pyObject == null){
                    Tools.miuiToast(requireContext(),"登录失败,请检查你的账号密码是否正确");
                }else{
                    savePassword();
                    PyObject pyObject1 = python.getModule("getUser")
                            .callAttr("getUserId");
                    Tools.saveStringData(requireActivity(),"userId",pyObject1.toString());
                    switchFragment(new MainFragment(pyObject));
                }
            }
        });
    }

    /**
     * 记住密码
     * */
    private void savePassword(){
        if (isCheck){
            Log.i(TAG, "onCheckedChanged: 存储账号密码成功");
            Tools.saveStringData(requireActivity(),"userName",binding.fragmentLoginNameEditText.getText().toString());
            Tools.saveStringData(requireActivity(),"passWord",binding.fragmentLoginPassWordfEditText.getText().toString());
        }

    }

    /**
     * 获取记住的密码
     * */
    private void getSaveUsernameAndPassword(){
        binding.fragmentLoginSavePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck = isChecked;
            }
        });
        String userName = Tools.getStringData(requireActivity(),"userName");
        String passWord = Tools.getStringData(requireActivity(),"passWord");
        if (userName != null && passWord != null){
            binding.fragmentLoginNameEditText.setText(userName);
            binding.fragmentLoginPassWordfEditText.setText(passWord);
            binding.fragmentLoginSavePassword.setChecked(true);
        }
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
                    .commitAllowingStateLoss();
        } else {
            transaction
                    .hide(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)))
                    .show(nextFragment)
                    .commitAllowingStateLoss();
        }
    }
}

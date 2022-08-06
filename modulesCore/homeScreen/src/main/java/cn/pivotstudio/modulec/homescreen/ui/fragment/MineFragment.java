package cn.pivotstudio.modulec.homescreen.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMineBinding;

/**
 * @classname:MineFragment
 * @description:
 * @date:2022/5/2 22:57
 * @version:1.0
 * @author:
 */
public class MineFragment extends BaseFragment {
    private FragmentMineBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false);
        return binding.getRoot();
    }
}

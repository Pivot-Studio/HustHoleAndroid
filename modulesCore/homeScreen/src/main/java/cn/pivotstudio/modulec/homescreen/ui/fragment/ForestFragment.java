package cn.pivotstudio.modulec.homescreen.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.libbase.base.ui.fragment.BaseFragment;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestBinding;
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHoleAdapter;

/**
 * @classname:ForestFragment
 * @description:
 * @date:2022/5/2 22:57
 * @version:1.0
 * @author:
 */
public class ForestFragment extends BaseFragment {
    private FragmentForestBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forest, container, false);
        binding.recyclerViewForestHoles.setAdapter(new ForestHoleAdapter());
        return binding.getRoot();
    }
}

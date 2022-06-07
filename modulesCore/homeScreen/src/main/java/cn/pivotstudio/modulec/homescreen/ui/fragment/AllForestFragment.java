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
import cn.pivotstudio.modulec.homescreen.databinding.FragmentAllFrorestBinding;

public class AllForestFragment extends BaseFragment {

    private FragmentAllFrorestBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_frorest, container, false);
        return binding.getRoot();
    }
}

package cn.pivotstudio.modulec.homescreen.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.libbase.base.ui.fragment.BaseFragment;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestBinding;
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHeadAdapter;
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHoleAdapter;
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestViewModel;

/**
 * @classname: ForestFragment
 * @description:
 * @date: 2022/5/2 22:57
 * @version: 1.0
 * @author:
 */
public class ForestFragment extends BaseFragment {
    private FragmentForestBinding binding;
    private ForestViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forest, container, false);
        viewModel = new ViewModelProvider(this).get(ForestViewModel.class);

        // 初始化两个RecyclerView
        ForestHoleAdapter holeAdapter = new ForestHoleAdapter();
        binding.recyclerViewForestHoles.setAdapter(holeAdapter);
        holeAdapter.submitList(viewModel.getForestHoles());

        ForestHeadAdapter headAdapter = new ForestHeadAdapter();
        binding.recyclerViewForestHead.setAdapter(headAdapter);
        headAdapter.submitList(viewModel.getForestHeads());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (binding.getForestFragment() == null) {
            binding.setForestFragment(this);
        }
    }

    /**
     * 利用 Navigation 导航到 AllForestFragment
     * <p>相关类 {@link AllForestFragment},nav_graph.xml</p>
     */
    public void navToAllForests() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_forest_fragment_to_all_forest_fragment);
    }

}

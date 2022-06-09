package cn.pivotstudio.modulec.homescreen.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.libbase.base.ui.fragment.BaseFragment;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.databinding.FragmentAllFrorestBinding;

import cn.pivotstudio.modulec.homescreen.ui.adapter.AllForestAdapter;
import cn.pivotstudio.modulec.homescreen.viewmodel.AllForestViewModel;

public class AllForestFragment extends BaseFragment {

    private FragmentAllFrorestBinding binding;
    private AllForestViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_all_frorest, container, false);
        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(AllForestViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View.OnClickListener onCardClick = (v) -> {
            navToForestDetail();
        };

        // 初始化6个RecyclerView
        AllForestAdapter allForestAdapter = new AllForestAdapter(onCardClick);

        binding.hotRecyclerView.setAdapter(allForestAdapter);
        allForestAdapter.submitList(viewModel.getForestCards());

        binding.limitedRecyclerView.setAdapter(allForestAdapter);
        allForestAdapter.submitList(viewModel.getForestCards());

        binding.emoRecyclerView.setAdapter(allForestAdapter);
        allForestAdapter.submitList(viewModel.getForestCards());

        binding.campusRecyclerView.setAdapter(allForestAdapter);
        allForestAdapter.submitList(viewModel.getForestCards());

        binding.studyRecyclerView.setAdapter(allForestAdapter);
        allForestAdapter.submitList(viewModel.getForestCards());

        binding.entertainmentRecyclerView.setAdapter(allForestAdapter);
        allForestAdapter.submitList(viewModel.getForestCards());
    }

    public void navToForestDetail() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_all_forest_fragment_to_forest_detail_fragment);
    }
}

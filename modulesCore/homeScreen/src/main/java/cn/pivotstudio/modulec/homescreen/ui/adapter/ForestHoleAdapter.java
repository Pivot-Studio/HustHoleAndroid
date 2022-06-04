package cn.pivotstudio.modulec.homescreen.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestBinding;
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestBinding;
import cn.pivotstudio.modulec.homescreen.model.ForestHoleResponse;


/**
 * @classname:ForestHoleAdapter
 * @description: 小树林界面大 RecyclerView 的 Adapter
 * @date:2022/5/4 0:26
 * @version:1.0
 * @author: mhh
 */
public class ForestHoleAdapter extends ListAdapter<ForestHoleResponse.ForestHole, ForestHoleAdapter.ForestHoleViewHolder> {

    // DiffCallback 是一个饿汉式单例类，用法参照 ListAdapter 官方使用方法
    public static class DiffCallback extends DiffUtil.ItemCallback<ForestHoleResponse.ForestHole> {

        private static final DiffCallback DIFF_CALLBACK = new DiffCallback();

        public static DiffCallback getInstance() {
            return DIFF_CALLBACK;
        }

        private DiffCallback() { }

        @Override
        public boolean areItemsTheSame(@NonNull ForestHoleResponse.ForestHole oldItem, @NonNull ForestHoleResponse.ForestHole newItem) {
            return oldItem.holeId.equals(newItem.holeId);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ForestHoleResponse.ForestHole oldItem, @NonNull ForestHoleResponse.ForestHole newItem) {
            return oldItem.content.equals(newItem.content);
        }
    }

    public ForestHoleAdapter() {
        super(DiffCallback.getInstance());
    }


    @NonNull
    @Override
    public ForestHoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ForestHoleViewHolder holder, int position) {

    }

    static class ForestHoleViewHolder extends RecyclerView.ViewHolder {

        private ItemForestBinding binding;

        // Constructor
        public ForestHoleViewHolder(ItemForestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ForestHoleResponse.ForestHole forestHole) {
            binding.setForestHole(forestHole);
        }


    }


}

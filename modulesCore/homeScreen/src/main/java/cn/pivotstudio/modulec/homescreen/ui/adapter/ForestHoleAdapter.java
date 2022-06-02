package cn.pivotstudio.modulec.homescreen.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @classname:ForestHoleAdapter
 * @description: 小树林界面大 RecyclerView 的 Adapter
 * @date:2022/5/4 0:26
 * @version:1.0
 * @author: mhh
 */
public class ForestHoleAdapter extends RecyclerView.Adapter<ForestHoleAdapter.ForestHoleViewHolder> {

    @NonNull
    @Override
    public ForestHoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ForestHoleViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ForestHoleViewHolder extends RecyclerView.ViewHolder {
        public ForestHoleViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}

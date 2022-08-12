package cn.pivotstudio.moduleb.libbase.base.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * @classname: BaseRecyclerViewAdapter
 * @description:
 * @date:2022/5/4 1:31
 * @version:1.0
 * @author:
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {
    public List<T> mDataList;
    public Context mContext;

    public BaseRecyclerViewAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateBaseViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        onBindBaseViewHolder(holder, position);
    }

    public void onRefreshData(List<T> list) {
        if (mDataList != null) {
            mDataList.clear();
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void onLoadMoreData(List<T> list) {
        if (mDataList != null && list != null) {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    protected void showMsg(CharSequence msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public abstract VH onCreateBaseViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindBaseViewHolder(VH holder, int position);
}



package cn.pivotstudio.modulec.homescreen.oldversion.message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.pivotstudio.modulec.homescreen.R;

public class SystemNotificationAdapter
    extends RecyclerView.Adapter<SystemNotificationAdapter.MyHolder> {
    private List<SystemNotificationBean> mSystemNotificationList;
    /*private LayoutInflater mInflater;*/
    private OnItemClickListener onItemClickListener;

    public SystemNotificationAdapter(List<SystemNotificationBean> mSystemNotificationList/*,Context context*/) {
        this.mSystemNotificationList = mSystemNotificationList;
        /*this.mInflater = LayoutInflater.from(context);*/
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public TextView time;

        public MyHolder(View view) {
            super(view);
            time = view.findViewById(R.id.system_notification_time);
            title = view.findViewById(R.id.system_notification_title);
            content = view.findViewById(R.id.system_notification_content);
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = /*mInflater*/LayoutInflater.from(parent.getContext())
            .inflate(R.layout.system_notification_item, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.content.setText(mSystemNotificationList.get(position).getSystemContent());
        holder.title.setText(mSystemNotificationList.get(position).getTitle());
        holder.time.setText(mSystemNotificationList.get(position).getTimestamp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position);
                    String TAG = "tag";
                    Log.d(TAG, "onClick: click " + position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSystemNotificationList.size();
    }
}
package com.example.hustholetest1.View.HomePage.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.NotificationBean;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationBean> mNotificationList;
    Context context;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView alias;
        TextView type;
        TextView time;
        TextView content;
        TextView hole_id;
        public ViewHolder(View view){
            super(view);
            alias = (TextView) view.findViewById(R.id.name);
            type = (TextView) view.findViewById(R.id.title);
            time = (TextView) view.findViewById(R.id.time);
            content = (TextView) view.findViewById(R.id.content);
            hole_id = (TextView) view.findViewById(R.id.hole_id);
        }

    }

    public NotificationAdapter(Context context,List<NotificationBean> notificationList){
        mNotificationList = notificationList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationBean notification = mNotificationList.get(position);
        holder.alias.setText(notification.getAlias());
        holder.time.setText(notification.getTime());
        holder.type.setText(notification.getType());
        holder.content.setText(notification.getReplyContent());
        holder.hole_id.setText("# "+notification.getHole_id());
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }
}

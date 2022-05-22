package cn.pivotstudio.modulec.homescreen.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.libbase.util.data.TimeUtil;

import cn.pivotstudio.modulec.homescreen.R;

/**
 * @classname:HomePageItemAdapter
 * @description:item的view加载适配器
 * @date:2022/5/4 15:40
 * @version:1.0
 * @author:
 */
public class HomePageItemAdapter {
    /**
     * 点赞按钮
     * @param view
     * @param is_thumbup
     */
    @BindingAdapter({"thumbupIcon"})
    public static void onClickThumbup(ImageView view, boolean is_thumbup){
    view.setImageResource(is_thumbup? R.mipmap.active:R.mipmap.inactive);

    }

    /**
     * 回复按钮
     * @param view
     * @param is_reply
     */
    @BindingAdapter({"replyIcon"})
    public static void onClickReply(ImageView view, boolean is_reply){
        view.setImageResource(is_reply? R.mipmap.active_2:R.mipmap.inactive_2);

    }

    /**
     * 收藏按钮
     * @param view
     * @param is_follow
     */
    @BindingAdapter({"followIcon"})
    public static void onClickFollow(ImageView view, boolean is_follow){
        view.setImageResource(is_follow? R.mipmap.active_3:R.mipmap.inactive_3);

    }

    /**
     * 举报，删除
     * @param view
     * @param is_mine
     */
    @BindingAdapter({"moreListIcon"})
    public static void onClickMore(ImageView view,boolean is_mine){
        view.setImageResource(is_mine?R.mipmap.vector6:R.mipmap.vector4);
    }

    /**
     * 时间加载
     * @param view
     * @param is_last_reply
     * @param created_timestamp
     */
    @BindingAdapter({"timeSign","time"})
    public static void onTime(TextView view,boolean is_last_reply,String created_timestamp){
        //后端搜索给的时间慢半个小时，麻
        String time= TimeUtil.time(created_timestamp)+((is_last_reply)?"更新":"发布");
        view.setText(time);
    }

    /**
     * 右上角小树林icon
     * @param view
     * @param forestName
     * @param role
     */
    @BindingAdapter({"forestIcon","role"})
    public static void onForestIconShow(Button view, String forestName,String role){
        if(role==null||forestName.equals("")){//搜索方式获得的数据没role,麻了
            view.setVisibility(View.INVISIBLE);
            return;
        }
        switch(role) {
            case "pivot":
                if(forestName.equals("")){
                    view.setVisibility(View.VISIBLE);
                    view.setPadding(15, 5, 6, 6);
                    view.setTextColor(view.getContext().getResources().getColor(R.color.GrayScale_50));
                    view.setText(" Pivot Studio团队 ");

                    Drawable homepressed = view.getContext().getResources().getDrawable(R.mipmap.pivot, null);
                    homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                    view.setCompoundDrawables(homepressed, null, null, null);
                    view.setBackgroundResource(R.drawable.tag_gray);
                    view.setOnClickListener(null);
                }else {
                    view.setVisibility(View.VISIBLE);
                    view.setText("  " + forestName+ "   ");
                }
                break;

            case "counseling_center":

                if(forestName.equals("")){
                    view.setVisibility(View.VISIBLE);
                    view.setPadding(15, 5, 6, 6);
                    view.setTextColor(view.getContext().getResources().getColor(R.color.red));
                    view.setText(" 校心理中心  ");

                    Drawable homepressed = view.getContext().getResources().getDrawable(R.mipmap.counselingcenter, null);
                    homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                    view.setCompoundDrawables(homepressed, null, null, null);
                    view.setBackgroundResource(R.drawable.tag_red);
                    view.setOnClickListener(null);

                }else {
                    view.setVisibility(View.VISIBLE);
                    view.setText("  " + forestName+ "   ");
                }
                break;

            default:
                view.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 右上角圆形icon
     * @param view
     * @param forestName
     * @param role
     */

    @BindingAdapter({"psIcon","role"})
    public static void onPsIconShow(ImageView view, String forestName,String role){
        if(role==null){
            view.setVisibility(View.GONE);
            return;
        }
        switch(role) {
            case "pivot":

                if(forestName.equals("")){
                    view.setVisibility(View.INVISIBLE);
                }else{
                    view.setVisibility(View.VISIBLE);
                    view.setImageResource(R.mipmap.pivot);
                }
                break;

            case "counseling_center":

                if(forestName.equals("")){
                    view.setVisibility(View.INVISIBLE);
                }else{
                    view.setVisibility(View.VISIBLE);
                    view.setImageResource(R.mipmap.counselingcenter);
                }
                break;

            default:
                view.setVisibility(View.GONE);
                break;
        }
    }

}

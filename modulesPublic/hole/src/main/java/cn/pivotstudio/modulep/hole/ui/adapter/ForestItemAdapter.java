package cn.pivotstudio.modulep.hole.ui.adapter;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import cn.pivotstudio.modulep.hole.R;


public class ForestItemAdapter {
    /**
     * 加载网络请求图片
     * @param v
     * @param url 图片url
     */
    @BindingAdapter({"loadUrl"})
    public static void setUrl(ImageView v,String url){
        RoundedCorners roundedCorners = new RoundedCorners(16);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(v.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(options)
                .into(v);
    }

    /**
     * 为小树林类型设置图标
     * @param v
     * @param name
     */
    @BindingAdapter({"loadTypeUrl"})
    public static void getUrlFormLocal(ImageView v,String name){
        if ("限定".equals(name)) {
            v.setImageResource(R.drawable.ic_specified_48dp);
        } else if ("热门".equals(name)) {
            v.setImageResource(R.drawable.ic_hot_40dp);
        } else if ("情感".equals(name)) {
            v.setImageResource(R.drawable.ic_emotion_38dp);
        } else if ("校园".equals(name)) {
            v.setImageResource(R.drawable.ic_school_48dp);
        } else if ("学习".equals(name)) {
            v.setImageResource(R.drawable.ic_study_48dp);
        } else if ("娱乐".equals(name)) {
            v.setImageResource(R.drawable.ic_disport_48dp);
        }
    }
}

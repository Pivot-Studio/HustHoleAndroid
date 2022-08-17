package cn.pivotstudio.modulep.hole.ui.adapter;

import static cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.context;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;

import cn.pivotstudio.moduleb.libbase.util.emoji.SpanStringUtil;
import cn.pivotstudio.moduleb.libbase.util.data.TimeUtil;

import cn.pivotstudio.modulep.hole.R;
import cn.pivotstudio.modulep.hole.model.ReplyListResponse;
import ru.noties.markwon.Markwon;

/**
 * @classname:HoleAdapter
 * @description:
 * @date:2022/5/9 0:49
 * @version:1.0
 * @author:
 */
public class HoleAdapter {

    @BindingAdapter({"aliasName", "aliasNameOwner"})
    public static void setAlias(TextView v, String alias, Boolean is_mine) {
        v.setText(is_mine ? alias + "(我)" : alias);
    }

    @BindingAdapter({"emojiBar"})
    public static void showEmojiBar(ImageView view, Boolean is_emoji_opened) {
        view.setImageResource(!is_emoji_opened ? R.mipmap.group314 : R.mipmap.group320);
    }


    @BindingAdapter({"hintText"})
    public static void setHintText(EditText view, ReplyListResponse.ReplyResponse answered) {

        SpannableString ss = new SpannableString("回复@" + answered.getAlias() + (answered.getIs_mine() ? "(我)" : "") + ":");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setHint(ss);
    }

    @BindingAdapter({"markDownContent"})
    public static void setMDContent(TextView view, String content) {
        CharSequence text = Markwon.markdown(context, content.trim().replace("\n", "  \n"));

        SpannableString spannableString = SpanStringUtil.getEmotionMarkdownContent(0x0001, context, view, text);
        view.setText(spannableString);
    }

    @BindingAdapter({"turnIcon"})
    public static void setTurnIcon(ImageView view, ObservableField<Boolean> is_descend) {
        view.setImageResource(is_descend.get() ? R.mipmap.group112 : R.mipmap.group111);
    }

    /**
     * 修改只看洞主图标
     *
     * @param view
     * @param is_owner
     */
    @BindingAdapter({"ownerText"})
    public static void setOwnerText(Button view, ObservableField<Boolean> is_owner) {


        if (is_owner.get()) {
            view.setPadding(20, 5, 6, 6);
            view.setTextColor(view.getResources().getColor(R.color.HH_BandColor_3));
            Drawable homepressed = view.getResources().getDrawable(R.mipmap.vector8, null);
            homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
            view.setCompoundDrawables(homepressed, null, null, null);

        } else {
            view.setPadding(0, 0, 0, 0);
            view.setCompoundDrawables(null, null, null, null);
            view.setTextColor(view.getResources().getColor(R.color.GrayScale_50));
        }
    }

    /**
     * 设置热评icon
     *
     * @param view
     * @param is_hot 是否是热评
     */
    @BindingAdapter({"hotIcon"})
    public static void setHotIcon(TextView view, Boolean is_hot) {
        view.setVisibility(is_hot ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter({"replyContentShow"})
    public static void setContentVisibility(ConstraintLayout view, Integer reply_to) {
        view.setVisibility(reply_to == -1 ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter({"replyToContent", "replyToAlias", "isMine"})
    public static void setReplyToContent(TextView view, String reply_to_content, String reply_to_alias, Boolean is_mine) {
        if (reply_to_content.equals("")) {
            view.setText(" 该评论（or回复）已删除 ");
        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder("" + reply_to_alias + " : " + reply_to_content);
//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            ForegroundColorSpan redSpan = new ForegroundColorSpan(view.getResources().getColor(R.color.GrayScale_0));
            builder.setSpan(redSpan, 0, reply_to_alias.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(builder);
        }
    }

    /**
     * 右上角小树林icon
     *
     * @param view
     * @param forestName
     * @param role
     */
    @BindingAdapter({"forestIcon", "role"})
    public static void setForestIcon(Button view, String forestName, String role) {
        if (role == null) {//后端搜索出来的数据没role,麻了
            view.setVisibility(View.INVISIBLE);
            return;
        }
        switch (role) {
            case "pivot":
                if (forestName.equals("")) {
                    view.setVisibility(View.VISIBLE);
                    view.setPadding(15, 5, 6, 6);
                    view.setTextColor(view.getContext().getResources().getColor(R.color.GrayScale_50));
                    view.setText(" Pivot Studio团队 ");

                    Drawable homepressed = view.getContext().getResources().getDrawable(R.mipmap.pivot, null);
                    homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                    view.setCompoundDrawables(homepressed, null, null, null);
                    view.setBackgroundResource(R.drawable.tag_gray);
                    view.setOnClickListener(null);
                } else {
                    view.setVisibility(View.VISIBLE);
                    view.setText("  " + forestName + "   ");
                }
                break;

            case "counseling_center":

                if (forestName.equals("")) {
                    view.setVisibility(View.VISIBLE);
                    view.setPadding(15, 5, 6, 6);
                    view.setTextColor(view.getContext().getResources().getColor(R.color.red));
                    view.setText(" 校心理中心  ");

                    Drawable homepressed = view.getContext().getResources().getDrawable(R.mipmap.counselingcenter, null);
                    homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                    view.setCompoundDrawables(homepressed, null, null, null);
                    view.setBackgroundResource(R.drawable.tag_red);
                    view.setOnClickListener(null);

                } else {
                    view.setVisibility(View.VISIBLE);
                    view.setText("  " + forestName + "   ");
                }
                break;

            default:
                view.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 右上角圆形icon
     *
     * @param view
     * @param forestName
     * @param role
     */
    @BindingAdapter({"psIcon", "role"})
    public static void setCircleIcon(ImageView view, String forestName, String role) {
        if (role == null) {
            view.setVisibility(View.INVISIBLE);
            return;
        }
        switch (role) {
            case "pivot":

                if (forestName.equals("")) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    view.setImageResource(R.drawable.ic_pivot_studio_16dp);
                }
                break;

            case "counseling_center":

                if (forestName.equals("")) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    view.setImageResource(R.mipmap.counselingcenter);
                }
                break;

            default:

                view.setVisibility(View.INVISIBLE);
                break;
        }
    }
}

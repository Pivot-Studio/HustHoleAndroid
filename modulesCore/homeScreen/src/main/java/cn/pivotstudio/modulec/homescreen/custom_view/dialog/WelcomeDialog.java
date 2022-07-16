package cn.pivotstudio.modulec.homescreen.custom_view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.pivotstudio.modulec.homescreen.R;

/**
 * @classname:WelcomeDialog
 * @description:欢迎的dialog
 * @date:2022/5/3 16:19
 * @version:1.0
 * @author:
 */
public class WelcomeDialog extends Dialog {
    private Context context;

    public WelcomeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public WelcomeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public WelcomeDialog(Context context) {
        super(context);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        setContentView(R.layout.dialog_welcome);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);//触摸灰色部分无响应

        TextView mTitleTv = (TextView) findViewById(R.id.tv_dialoghomepage_title);
        mTitleTv.setText(R.string.homepage_2);

        TextView mContentTv = (TextView) findViewById(R.id.tv_dialoghomepage_content);
        String welcomeMsg = ("<small><font color=\"#00000000\">你好，这里是1037树洞~</font></small><br>" +
                " <small><font color=\"#666666\">请先别急着跳过噢，花三十秒听听树洞的悄悄话吧</font></small>" + "<font color=\"#666666\">(●'◡'●)</font><br><br>" +
                "<strong><font color=\"#00000000\">1037树洞是什么</font></strong><br><br>" +
                "<small><font color=\"#666666\">1037树洞是专属于HUSTer的</font></small><small><font color=\"#00000000\">匿名社区</font></small>，" +
                "<small><font color=\"#666666\">通过学号绑定的校园邮箱来验证你的华科在校学生身份。你的学号邮箱仅会被用于验证，而不会在社区中被展示；通过后台加密算法，除了在严重违反社区规范的情况下且运营者认为有必要时，</font></small>" +
                "<small><font color=\"#00000000\">任何人都无法获知你的发言身份</font></small><small><font color=\"#666666\">。在这里，你可以真正地畅所欲言。</font></small><br><br>" +
                "<strong><font color=\"#00000000\">我们的初衷</font></strong><br><br>" +
                " <small><font color=\"#666666\">敲下几行文字，1037树洞可以满足你任何的交流需求：</font></small><br><br>" +
                " <small><font color=\"#00000000\">倾诉自己内心深处的伤感或喜悦，分享华科最新发生的大小趣事，寻找校内拥有小众爱好的朋友，寻求学长学姐们给自己的建议，交流对于热点社会问题的看法……</font></small><br><br>" +
                " <small><font color=\"#666666\">树洞的本质是人和人之间的互相倾诉，只有人来人往，树洞才会好玩儿~在1037树洞，所有的声音都会被认真倾听，你的一切发言不用担心被熟人监视，而你的交流对象都是和你思维高度同频的HUSTer~</font></small><br><br>" +
                "<strong><font color=\"#00000000\">我们的期望</font></strong><br><br>" +
                " <small><font color=\"#666666\">为了让每一位洞友都能在1037树洞找到温暖，我们希望你：</font></small><br><br>" +
                " <small><font color=\"#00000000\">&#160;&#160;●&#160;做一个友善的倾听者，尊重他人，即使TA与你观点相异；</font></small><br>" +
                " <small><font color=\"#00000000\">&#160;&#160;●&#160;不要发布令人感到不适或者违反法律法规的内容，包括但不限于侮辱他人、侵犯隐私、发布暴力或色情内容等；</font></small><br>" +
                " <small><font color=\"#00000000\">&#160;&#160;●&#160;在参与讨论时，请与我们一起维护社区的安全，对于社区内令人不适的内容主动制止。</font></small><br><br>" +
                " <small><font color=\"#666666\">匿名社区的良好环境需要你我共同维护~感谢你的支持！</font></small><br><br>" +
                "<small><font color=\"#00000000\">祝你在1037树洞玩得愉快。</font></small><br>");
        mContentTv.setText(Html.fromHtml(welcomeMsg));

        Button mBtn_ok = (Button) findViewById(R.id.btn_dialoghomepage_sure);
        mBtn_ok.setOnClickListener(this::onClick);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    private void onClick(View v) {
        if (v.getId() == R.id.btn_dialoghomepage_sure) {
            dismiss();
        }
    }
}

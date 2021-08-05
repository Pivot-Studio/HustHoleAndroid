package com.example.hustholetest1.View.HomePage.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.hustholetest1.R;
import com.githang.statusbar.StatusBarCompat;

public class Page2_ApplyForestActivity extends AppCompatActivity {
    private TextView textView,text;
    private Button button;
    private ImageView back;
    private PopupWindow  popWindow;
    private LinearLayout linearLayout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2_applyforest);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        textView=(TextView)findViewById(R.id.textView50);
        back=(ImageView)findViewById(R.id.backView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page2_ApplyForestActivity.this, Page2_AllForestsActivity.class);
                startActivity(intent);
            }
        });
        String content="没有找到想要加入的小树林？来向1037树洞运营组提交你的灵感吧！在你提交申请之后的七天内，我们会评估小树林是否可以被创建，并将结果通过系统通知告诉你。通过阅读小树林公约可以提高你申请的通过率噢~"; //文本内容在上面已经有了
        SpannableStringBuilder spannable = new SpannableStringBuilder(content);
        //SpannableStringBuilder spannable = new SpannableStringBuilder(str);
        //spannable.setSpan(new ForegroundColorSpan(Color.RED),34,38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new TextClick(), 79, 84,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannable);
        linearLayout=(LinearLayout) findViewById(R.id.include);








    }
    public class TextClick extends ClickableSpan {
        @Override
        public void onClick(View view) {
            View contentView = LayoutInflater.from(Page2_ApplyForestActivity.this).inflate(R.layout.page2_applyforest_popupwindow, null);
           // View contentView2=LayoutInflater.from(P).inflate(R.layout.screen, null);
              popWindow=new PopupWindow(contentView);
            popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popWindow.setAnimationStyle(R.style.Page2Anim);
            popWindow.showAsDropDown(back);
            //popupWindow2=new PopupWindow(contentView2);
            button=(Button)contentView.findViewById(R.id.button);
            text=(TextView)contentView.findViewById(R.id.textView60);
            String aa=("<large><font color=\"#00000000\">小树林公约</font></large><br>");
            text.setText(Html.fromHtml(aa));



            //button2=(Button)contentView.findViewById(R.id.button7);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.dismiss();
                }
            });
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor( getResources().getColor(R.color.HH_Reminder_Link));   //设置字体颜色
            // ds.setColor(Color.parseColor("#000000"));   //自定义颜色值
            ds.setUnderlineText(false); //设置没有下划线
        }
    }

}

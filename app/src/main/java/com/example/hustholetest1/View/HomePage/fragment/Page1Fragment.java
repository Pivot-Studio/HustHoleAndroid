package com.example.hustholetest1.View.HomePage.fragment;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.Model.EditTextReaction;
import com.example.hustholetest1.Model.StandardRefreshHeader;
import com.example.hustholetest1.R;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Page1Fragment extends Fragment {
    private Button button1,button2;
    private EditText editText1;
    private ImageView imageView;
    private TextView textView;
    private boolean flag=false;
    private PopupWindow popWindow,popupWindow2;
    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerView;
    public static Page1Fragment newInstance() {
        Page1Fragment fragment = new Page1Fragment();
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page1fragment, container, false);
        editText1=rootView.findViewById(R.id.editText);
        imageView= (ImageView)rootView.findViewById(R.id.imageView);
        imageView.setImageResource(R.mipmap.triangle);
        textView=(TextView)rootView.findViewById(R.id.textView);
        constraintLayout=(ConstraintLayout) rootView.findViewById(R.id.linearLayout);
        //recyclerView=(RecyclerView)rootView.findViewById(R.id.recyclerView);
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.page1fragment_1));
        EditTextReaction.EditTextSize(editText1,string1,12);
        editText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==true){
                  EndTriangleAnim();
                }
            }
        });

        RefreshLayout refreshLayout = (RefreshLayout)rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(4000/*,false*/);
//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(4000/*,false*/);
                //传入false表示加载失败
            }
        });



        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.page1fragment_popupwindow1, null);
        View contentView2=LayoutInflater.from(getActivity()).inflate(R.layout.screen, null);
        popWindow=new PopupWindow(contentView);
        popupWindow2=new PopupWindow(contentView2);
        button1=(Button)contentView.findViewById(R.id.button5);
        button2=(Button)contentView.findViewById(R.id.button7);



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setBackgroundResource(R.drawable.standard_button);
                button1.setTextAppearance(getActivity(),R.style.popupwindow_button_click);
                button2.setBackgroundResource(R.drawable.standard_button_gray);
                button2.setTextAppearance(getActivity(),R.style.popupwindow_button);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button2.setBackgroundResource(R.drawable.standard_button);
                button2.setTextAppearance(getActivity(),R.style.popupwindow_button_click);
                button1.setBackgroundResource(R.drawable.standard_button_gray);
                button1.setTextAppearance(getActivity(),R.style.popupwindow_button);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==false){
                    Log.d("wsccsf","1");
                    BeginTriangleAnim();
                    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(rootView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                   // recyclerView.setBackgroundColor(getResources().getColor(R.color.GrayScale_50));
                }else{
                    Log.d("wsccsf","3");
                    EndTriangleAnim();
                    //recyclerView.setBackgroundColor(getResources().getColor(R.color.GrayScale_100));
                }
            }
        });
        contentView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndTriangleAnim();
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        EndTriangleAnim();


    }
    private void BeginTriangleAnim(){
        RotateAnimation rotate;
        rotate =new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setImageResource(R.mipmap.triangle);
                Log.d("wsccsf","2");
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        flag=true;
        popupWindow2.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.showAsDropDown(constraintLayout);

        popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.contextMenuAnim);
        popWindow.showAsDropDown(constraintLayout);
    }
    private void EndTriangleAnim(){
        RotateAnimation rotate;
        rotate =new RotateAnimation(180f,360f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);

        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setImageResource(R.mipmap.triangle);
                Log.d("wsccsf","4");
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        flag=false;
        popWindow.dismiss();
        popupWindow2.dismiss();
    }
}

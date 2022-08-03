package cn.pivotstudio.modulec.homescreen.oldversion.forest;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.model.EditTextReaction;
import cn.pivotstudio.modulec.homescreen.oldversion.model.GlideRoundTransform;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApplyForestActivity extends AppCompatActivity {
    private RequestInterface request;
    private Retrofit retrofit;
    private TextView textView, text, warnName, warnIntroduce, titleBar;
    private Button ppwButton, addCover, send;
    private ImageButton addIcon;
    private EditText forestName, forestIntroduce;
    private ConstraintLayout back;
    private ImageView mCoverIv;
    private PopupWindow popWindow;
    private ConstraintLayout constraintLayout;
    private ScrollView scrollView;
    private Boolean popWindowCondtion = false;
    private static Bitmap bitmap1, bitmap2;
    private Boolean mForestNameCondition = false, mForestIntroduceCondition = false;

    public static void setIcon(Bitmap bitmap) {
        bitmap1 = bitmap;
    }

    public static void setCover(Bitmap bitmap) {
        bitmap2 = bitmap;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyforest);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        titleBar = (TextView) findViewById(R.id.tv_titlebarcentergreen_title);
        titleBar.setText("申请小树林");
        textView = (TextView) findViewById(R.id.tv_applyforest_content);
        back = (ConstraintLayout) findViewById(R.id.cl_titlebarcentergreen_back);
        warnName = (TextView) findViewById(R.id.tv_applyforest_warnname);
        warnIntroduce = (TextView) findViewById(R.id.tv_applyforest_warnintroduce);
        warnName.setVisibility(View.INVISIBLE);
        warnIntroduce.setVisibility(View.INVISIBLE);
        forestName = (EditText) findViewById(R.id.et_applyforest_name);
        forestIntroduce = (EditText) findViewById(R.id.et_applyforest_introduce);
        send = (Button) findViewById(R.id.btn_applyforest_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {//加载纵向列表标题

                    File file, file2;

                    @Override
                    public void run() {
                        try {
                            file = saveFile(bitmap1, "icon");
                            file2 = saveFile(bitmap2, "cover");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        RequestBody fileRQ = RequestBody.create(MediaType.parse("image/png"), file);
                        MultipartBody.Part part =
                            MultipartBody.Part.createFormData("image_url", file.getName(), fileRQ);

                        RequestBody fileRQ2 =
                            RequestBody.create(MediaType.parse("image/png"), file2);
                        MultipartBody.Part part2 =
                            MultipartBody.Part.createFormData("background_image_url",
                                file2.getName(), fileRQ2);

                        RequestBody body =
                            new MultipartBody.Builder().addFormDataPart("forest_name",
                                    forestName.getText().toString())
                                .addFormDataPart("description",
                                    forestIntroduce.getText().toString())
                                .addFormDataPart("image_url", file.getName(), fileRQ)
                                .addFormDataPart("background_image_url", file2.getName(), fileRQ2)
                                .build();

                        /*

                         */
                        Call<ResponseBody> call = request.applyForest(body);//进行封装
                        //call.
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call,
                                                   Response<ResponseBody> response) {
                                if (response.code() == 200) {
                                    String json = "null";
                                    try {
                                        if (response.body() != null) {
                                            json = response.body().string();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(ApplyForestActivity.this, "申请成功",
                                        Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    String json = "null";
                                    String returncondition = null;
                                    if (response.errorBody() != null) {
                                        try {
                                            json = response.errorBody().string();
                                            //JSONObject jsonObject = new JSONObject(json);
                                            //returncondition = jsonObject.getString("msg");
                                            Toast.makeText(ApplyForestActivity.this, json,
                                                Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(ApplyForestActivity.this,
                                                R.string.network_unknownfailture, Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                Toast.makeText(ApplyForestActivity.this, "申请失败，请检查网络",
                                    Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });
        addIcon = (ImageButton) findViewById(R.id.btn_applyforest_addicon);
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ApplyForestActivity.this, CropPictureActivity.class);
                startActivity(intent);
            }
        });
        addCover = (Button) findViewById(R.id.btn_applyforest_addcover);
        addCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                    new Intent(ApplyForestActivity.this, CropOblongPictureActivity.class);
                startActivity(intent);
            }
        });
        SpannableString string1 =
            new SpannableString(this.getResources().getString(R.string.page2_applyforest_4));
        SpannableString string2 =
            new SpannableString(this.getResources().getString(R.string.page2_applyforest_7));
        EditTextReaction.EditTextSize(forestName, string1, 18);
        EditTextReaction.EditTextSize(forestIntroduce, string2, 15);
        forestName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (forestName.getText().toString().trim().length() > 10) {
                    warnName.setVisibility(View.VISIBLE);
                    mForestNameCondition = false;
                } else {
                    warnName.setVisibility(View.INVISIBLE);
                    mForestNameCondition = true;
                }
                ButtonCondition();
            }
        });
        forestIntroduce.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (forestIntroduce.getText().toString().trim().length() > 40) {
                    warnIntroduce.setVisibility(View.VISIBLE);
                    mForestIntroduceCondition = false;
                } else {
                    warnIntroduce.setVisibility(View.INVISIBLE);
                    mForestIntroduceCondition = true;
                }
                ButtonCondition();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                finish();
            }
        });

        String content =
            "没有找到想要加入的小树林？来向1037树洞运营组提交你的灵感吧！在你提交申请之后的七天内，我们会评估小树林是否可以被创建，并将结果通过系统通知告诉你。通过阅读小树林公约可以提高你申请的通过率噢~"; //文本内容在上面已经有了
        SpannableStringBuilder spannable = new SpannableStringBuilder(content);
        //SpannableStringBuilder spannable = new SpannableStringBuilder(str);
        //spannable.setSpan(new ForegroundColorSpan(Color.RED),34,38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new TextClick(), 79, 84, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannable);
        constraintLayout = (ConstraintLayout) findViewById(R.id.include);
        scrollView = (ScrollView) findViewById(R.id.scrollView2);

        retrofit = RetrofitManager.getRetrofit();
        request = RetrofitManager.getRequest();
        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void ButtonCondition() {
        if (mForestIntroduceCondition
            && mForestNameCondition
            && bitmap1 != null
            && bitmap2 != null) {
            send.setBackgroundResource(R.drawable.button);
            send.setEnabled(true);
        } else {
            send.setBackgroundResource(R.drawable.standard_button_gray);
            send.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bitmap1 != null) {
            Drawable drawable = new BitmapDrawable(bitmap1);
            addIcon.setBackground(drawable);
            ButtonCondition();
        }
        if (bitmap2 != null) {
            Drawable drawable = new BitmapDrawable(bitmap2);
            addCover.setBackground(drawable);
            ButtonCondition();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap1 != null) {
            bitmap1 = null;
        }
        if (bitmap2 != null) {
            bitmap2 = null;
        }
    }

    public File saveFile(Bitmap bm, String fileName)
        throws IOException {//将Bitmap类型的图片转化成file类型，便于上传到服务器
        //String path=Environment.getExternalStorageDirectory() + ;
        //   File extDir = Environment.getExternalStorageDirectory();

        //String filename = "downloadedMusic.mp3";

        //File dirFile = new File(path);
        // File file = new File(extDir,fileName);
        if (ContextCompat.checkSelfPermission(ApplyForestActivity.this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ApplyForestActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(ApplyForestActivity.this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }
        }

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(directory, fileName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bm.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File  dirFile = new File(directory, "/Ask");



        if(!dirFile.exists()){
            dirFile.mkdir();
        }

        File myCaptureFile = new File(dirFile.getName() + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
        bos.flush();
        bos.close();


        return myCaptureFile;

         */
        return file;
    }

    public class TextClick extends ClickableSpan {
        @Override
        public void onClick(View view) {
            if (popWindowCondtion == false) {
                popWindowCondtion = true;
                View contentView = LayoutInflater.from(ApplyForestActivity.this)
                    .inflate(R.layout.ppw_applyforest, null);
                // View contentView2=LayoutInflater.from(P).inflate(R.layout.screen, null);
                InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                popWindow = new PopupWindow(contentView);
                int[] location = new int[2];
                titleBar.getLocationOnScreen(location);
                WindowManager wm = (WindowManager) ApplyForestActivity.this.getSystemService(
                    Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                // 从默认显示器中获取显示参数保存到dm对象中
                wm.getDefaultDisplay().getMetrics(dm);

                DisplayMetrics outMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
                int widthPixels = outMetrics.widthPixels;
                int heightPixels = outMetrics.heightPixels;
                //Log.i("TAG", "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels);
                // px=(dpi/160)dp。公式变形：dp=px*160/dpi
                int bb = ApplyForestActivity.this.getResources().getDisplayMetrics().densityDpi;
                //Log.d("height", dm.heightPixels+"+"+location[1]);
                popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popWindow.setHeight(dm.heightPixels - ((location[1] * bb) / 160) - 20);
                //popWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                popWindow.setAnimationStyle(R.style.Page2Anim);
                popWindow.showAsDropDown(constraintLayout);

                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.6f; // 0.0~1.0
                getWindow().setAttributes(lp);
                //popWindow.backgroundAlpha
                // 实例化一个ColorDrawable颜色为半透明
           /* ColorDrawable dw = new ColorDrawable(0x60000000);
            // 设置SelectPicPopupWindow弹出窗体的背景
            popWindow.setBackgroundDrawable(dw);
            // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
            contentView.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {

                    int height = contentView.findViewById(R.id.pop_layout).getTop();
                    int y = (int) event.getY();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (y < height) {
                            dismiss();
                        }
                    }
                    return true;
                }
            });*/
                mCoverIv = (ImageView) contentView.findViewById(R.id.iv_ppwapplyforest_cover);
                Glide.with(ApplyForestActivity.this)
                    .load(R.mipmap.maskgroup)
                    .transform(new GlideRoundTransform(28))
                    .into(mCoverIv);

                //popupWindow2=new PopupWindow(contentView2);
                ppwButton = (Button) contentView.findViewById(R.id.btn_ppwapplyforest_sure);
                text = (TextView) contentView.findViewById(R.id.tv_ppwapplyforest_content);
                String aa = ("<strong><font color=\"#00000000\">什么是小树林？</font></strong><br><br>"
                    + " <small><font color=\"#7E7E7E\">“小树林”是聚集相同类型树洞、供洞友就某一主题进行倾诉的场所。新建一个小树林，就像在城市广场边建立一个小社区，社区里的人有着相似的乐趣和烦恼，彼此互相倾诉和倾听。不同的社区发出的声音共同构成了热闹的树洞广场。</font></small><br><br>"
                    + "<strong><font color=\"#343434\">如何提高申请新建小树林的通过率？</font></strong><br><br>"
                    + "<small><font color=\"#343434\">1、小树林需要有一个明确、具体、集中的主题</font></small><br><br>"
                    + "<small><font color=\"#7E7E7E\">1037树洞是一个倾诉与倾听的场所，小树林所做的是帮助洞友们聚焦自己感兴趣的领域，同时聚集对这个领域感兴趣的人。我们希望小树林里讨论的内容是有同一主题的，洞友们对出现在某一小树林里的树洞内容应该能有大概的心理预期。</font></small><br><br>"
                    + "<small><font color=\"#343434\">2、避免申请建立主题相似的小树林</font></small><br><br>"
                    + "<small><font color=\"#7E7E7E\">申请新建小树林时请先看看现有的小树林中有没有与你想新建的讨论主题类似的，注意申请新建时明确与现有小树林的讨论边界。</font></small><br><br>"
                    + "<small><font color=\"#343434\">3、小树林的主题需要具有普适性，对大多数人有意义</font></small><br><br>"
                    + "<small><font color=\"#7E7E7E\">请避免根据个人偏好申请小树林，比如专业性过强的话题、解决个人问题的求助、粒度过小的具体事物等。\n"
                    + "×：“吃鸡俱乐部”\n"
                    + "√：“游戏玩家冲锋队”</font></small><br><br>"
                    + "<small><font color=\"#343434\">4、发挥你的灵感，取一个有趣的小树林名称</font></small><br><br>"
                    + "<small><font color=\"#7E7E7E\">有趣的名称是小树林社区繁荣的基础。我们希望你能发挥灵感创意，对小树林讨论的主题进行精准而有趣地概括，让人既能一眼看出小树林讨论的话题内容，又能对小树林名称本身啧啧称赞。\n"
                    + "×：“考试挂科怎么办”\n"
                    + "√：“你能做的岂止如此””</font></small><br><br>"
                    + "<small><font color=\"#343434\">5、避免申请不符合社区规范的主题</font></small><br><br>"
                    + "<small><font color=\"#7E7E7E\">1037树洞是一个匿名社区，但匿名并不意味着我们没有价值观。对于暴力挑衅、歧视对立、色情低俗、封建迷信的内容，我们坚决零容忍。因此申请新建的小树林如果与这类主题相关，我们将对申请人进行直接封号处理。</font></small><br><br>"
                    + "<strong><font color=\"#343434\">如何更好地使用小树林？</font></strong><br><br>"
                    + "<small><font color=\"#7E7E7E\">1、避免在小树林里发布和讨论主题无关的树洞；\n"
                    + "2、发布树洞前先想想，能不能选个合适的小树林再发布；\n"
                    + "3、自觉维护小树林内友善的讨论氛围，不要人身攻击和恶意评论。</font></small><br><br>");
                // constraintLayout.setBackgroundColor(getResources().getColor(R.color.darkscreen));

                text.setText(Html.fromHtml(aa));
                text.setHeight(dm.heightPixels - ((location[1] * bb) / 160) * 2 - 200);
                text.setMovementMethod(ScrollingMovementMethod.getInstance());
                //button2=(Button)contentView.findViewById(R.id.button7);
                ppwButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popWindowCondtion = false;
                        // constraintLayout.setBackgroundColor(getResources().getColor(R.color.HH_BandColor_1));
                        popWindow.dismiss();
                        lp.alpha = 1f; // 0.0~1.0
                        getWindow().setAttributes(lp);
                    }
                });
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(getResources().getColor(R.color.HH_Reminder_Link));   //设置字体颜色
            // ds.setColor(Color.parseColor("#000000"));   //自定义颜色值
            ds.setUnderlineText(false); //设置没有下划线
        }
    }
}

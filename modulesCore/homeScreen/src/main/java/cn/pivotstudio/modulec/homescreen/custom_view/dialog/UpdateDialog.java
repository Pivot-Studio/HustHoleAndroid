package cn.pivotstudio.modulec.homescreen.custom_view.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import cn.pivotstudio.moduleb.libbase.util.permission.NotificationSetUtil;
import cn.pivotstudio.moduleb.libbase.util.download.UrlDownloadUtil;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity;

/**
 * @classname:UpdateDialog
 * @description:更新时的dialog
 * @date:2022/5/3 1:00
 * @version:1.0
 * @author:
 */
public class UpdateDialog extends Dialog {
    Context context;
    String oldVersion, lastVersion, downloadUrl;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    public UpdateDialog(@NonNull Context context) {
        super(context);
    }

    public UpdateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param oldVersion  老版本
     * @param lastVersion 新版本
     * @param downloadUrl 下载url
     */
    public UpdateDialog(@NonNull Context context, String oldVersion, String lastVersion, String downloadUrl) {
        super(context);
        this.context = context;
        this.oldVersion = oldVersion;
        this.lastVersion = lastVersion;
        this.downloadUrl = downloadUrl;

        setContentView(R.layout.dialog_homescreenupdate);

        setCanceledOnTouchOutside(false);//点击灰色部分不退出
        getWindow().setBackgroundDrawableResource(R.drawable.notice);//设置圆角

        TextView update = findViewById(R.id.dialog_dialoghsupdate_delete_tv_yes);
        ConstraintLayout back = (ConstraintLayout) findViewById(R.id.cl_hsupdate);

        update.setOnClickListener(this::onClick);//布局中设置无效，暂时未知
        back.setOnClickListener(this::onClick);

        ImageView backIcon = findViewById(R.id.iv_backicon);
        backIcon.setImageResource(R.mipmap.vector10);

        TextView updateText = findViewById(R.id.tv_dialoghsupdate_content);
        updateText.setText("叮咚~洞洞子发现新版本：" + "\n您的当前版本为" + oldVersion + "\n1037树洞的最新版本为" + lastVersion + "\n请确定是否进行更新");
    }

    /**
     * 点击事件
     *
     * @param v
     */
    private void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_dialoghsupdate_delete_tv_yes) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {//判断是否有存储权限
                //没有权限则申请权限
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                //有权限直接执行
                if (downloadUrl.equals("")) {
                    Toast.makeText(context, "获取的下载链接为空", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    NotificationSetUtil.OpenNotificationSetting(context, new NotificationSetUtil.OnNextLitener() {
                        @Override
                        public void onNext() {
                            UrlDownloadUtil urlDownloadUtil = new UrlDownloadUtil(context, downloadUrl, HomeScreenActivity.class);
                            urlDownloadUtil.startDownload();
                            dismiss();
                        }
                    });
                }
            }
        } else if (id == R.id.cl_hsupdate) {
            dismiss();
        }
    }
}

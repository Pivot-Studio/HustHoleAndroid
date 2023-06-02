package cn.pivotstudio.modulec.homescreen.custom_view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import cn.pivotstudio.husthole.moduleb.network.model.VersionInfo;
import cn.pivotstudio.modulec.homescreen.R;


/**
 * @classname: UpdateDialog
 * @description: 更新时的dialog
 * @date: 2022/5/3 1:00
 * @version:1.0
 * @author:
 */
public class UpdateDialog extends Dialog {
    Context context;
    VersionInfo versionInfo;

    OnClickedListener listener;

    /**
     * 构造函数
     *
     * @param context     context
     * @param versionInfo 版本信息
     */
    public UpdateDialog(@NonNull Context context, VersionInfo versionInfo, OnClickedListener listener) {
        super(context);
        this.context = context;
        this.versionInfo = versionInfo;
        this.listener = listener;

        setContentView(R.layout.dialog_homescreenupdate);

        //点击灰色部分不退出
        setCanceledOnTouchOutside(false);
        //设置圆角
        getWindow().setBackgroundDrawableResource(R.drawable.notice);

        TextView update = findViewById(R.id.dialog_dialoghsupdate_delete_tv_yes);
        ConstraintLayout back = findViewById(R.id.cl_hsupdate);

        //布局中设置无效，暂时未知
        update.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);

        ImageView backIcon = findViewById(R.id.iv_backicon);
        backIcon.setImageResource(R.mipmap.vector10);

        TextView updateText = findViewById(R.id.tv_dialoghsupdate_content);
        String notification = String.format(context.getString(R.string.newVersionTip), getVersionName(), versionInfo.getVersionName(), versionInfo.getUpdateContent());
        updateText.setText(notification);
    }

    /**
     * 点击事件
     *
     * @param v view
     */
    private void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_dialoghsupdate_delete_tv_yes) {
            Intent intent = listener.getIntent();
            context.startActivity(intent);
            dismiss();
        } else if (id == R.id.cl_hsupdate) {
            dismiss();
        }
    }

    private String getVersionName() {
        String name = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public interface OnClickedListener {
        /**
         * Called when the button is clicked, and return the install intent to start install activity.
         *
         * @return install intent
         */
        Intent getIntent();
    }
}

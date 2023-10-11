package cn.pivotstudio.moduleb.rebase.lib.base.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.pivotstudio.moduleb.rebase.lib.util.permission.PermissionUtil;

/**
 * @classname: BaseActivity
 * @description:
 * @date: 2022/4/28 19:49
 * @version:1.0
 * @author:
 */
public class BaseActivity extends AppCompatActivity {

    protected AppCompatActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;

        PermissionUtil.getInstance();
    }

    protected void showMsg(CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showLongMsg(CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}

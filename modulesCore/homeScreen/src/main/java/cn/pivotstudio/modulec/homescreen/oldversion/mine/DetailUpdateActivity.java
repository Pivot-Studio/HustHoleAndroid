package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.model.CommonUtils;
import cn.pivotstudio.modulec.homescreen.oldversion.model.NotificationSetUtil;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import com.githang.statusbar.StatusBarCompat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Retrofit;

public class DetailUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    Retrofit retrofit;
    RequestInterface request;
    private RelativeLayout update, checkupdate;
    private ImageView back;
    private Notification notification;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private String versionName;
    private String AndroidUpdateUrl = "";
    private Boolean updateCondition = false;
    private final Boolean dialogCondition = false;

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailupdate);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);
        initView();

        //Log.d("sadsfa",packageName(Main3Activity.this)+"");
    }

    private void initView() {
        update = (RelativeLayout) findViewById(R.id.update);
        checkupdate = (RelativeLayout) findViewById(R.id.checkupdate);
        back = (ImageView) findViewById(R.id.settings_img);
        update.setOnClickListener(this);
        checkupdate.setOnClickListener(this);
        back.setOnClickListener(this);
        versionName = packageName(DetailUpdateActivity.this);
    }

    private void permissiongen() {
        //处理需要动态申请的权限
        PermissionGen.with(DetailUpdateActivity.this)
            .addRequestCode(200)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .request();
    }

    //申请权限结果的返回
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //权限申请成功
    @PermissionSuccess(requestCode = 200)
    public void doSomething() {
        //在这个方法中做一些权限申请成功的事情
        // Toast.makeText(getApplication(), "成功", Toast.LENGTH_SHORT).show();

    }

    //申请失败
    @PermissionFail(requestCode = 200)
    public void doFailSomething() {
        Toast.makeText(getApplication(), "失败", Toast.LENGTH_SHORT).show();
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.checkupdate) {
            new Thread(() -> {
                retrofit2.Call<ResponseBody> call = request.checkupdate2();
                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call,
                                           retrofit2.Response<ResponseBody> response) {
                        try {
                            JSONObject mode = new JSONObject(response.body().string());
                            String Androidversion = mode.getString("Androidversion");
                            AndroidUpdateUrl = mode.getString("AndroidUpdateUrl");
                            if (Androidversion.equals(versionName)) {
                                Toast.makeText(DetailUpdateActivity.this, "您的应用为最新版本",
                                    Toast.LENGTH_SHORT).show();
                            } else {
                                View mView =
                                    View.inflate(DetailUpdateActivity.this, R.layout.dialog_update,
                                        null);
                                // mView.setBackgroundResource(R.drawable.homepage_notice);
                                //设置自定义的布局
                                //mBuilder.setView(mView);
                                Dialog dialog = new Dialog(DetailUpdateActivity.this);
                                dialog.setContentView(mView);
                                dialog.getWindow().setBackgroundDrawableResource(R.drawable.notice);
                                TextView no =
                                    (TextView) mView.findViewById(R.id.dialog_delete_tv_cancel);
                                TextView yes =
                                    (TextView) mView.findViewById(R.id.dialog_delete_tv_yes);
                                TextView textView =
                                    (TextView) mView.findViewById(R.id.tv_dialogupdaate_content);
                                textView.setText("叮咚~洞洞子发现新版本："
                                    + "\n您的当前版本为"
                                    + versionName
                                    + "\n1037树洞的最新版本为"
                                    + Androidversion
                                    + "\n请确定是否进行更新");
                                no.setOnClickListener(v12 -> {
                                    dialog.dismiss();
                                });
                                yes.setOnClickListener(v1 -> {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ContextCompat.checkSelfPermission(
                                            DetailUpdateActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                            //没有权限则申请权限
                                            ActivityCompat.requestPermissions(
                                                DetailUpdateActivity.this,
                                                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                                1);
                                        } else {
                                            //有权限直接执行,docode()不用做处理
                                            if (AndroidUpdateUrl.equals("")) {
                                                Toast.makeText(DetailUpdateActivity.this,
                                                    "获取的下载链接为空", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                NotificationSetUtil.OpenNotificationSetting(
                                                    DetailUpdateActivity.this,
                                                    new NotificationSetUtil.OnNextLitener() {
                                                        @Override
                                                        public void onNext() {
                                                            initialNotification();
                                                            download();
                                                            dialog.dismiss();
                                                            //Toast.makeText(HomeScreenActivity,"已开启通知权限",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            }
                                        }
                                    } else {
                                        //小于6.0，不用申请权限，直接执行
                                        if (AndroidUpdateUrl.equals("")) {
                                            Toast.makeText(DetailUpdateActivity.this, "获取的下载链接为空",
                                                Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {
                                            initialNotification();
                                            download();
                                            updateCondition = true;
                                            // updateCondition = true;
                                            dialog.dismiss();
                                        }
                                    }






/*
                                        if(updateCondition){
                                            Toast.makeText(DetailUpdateActivity.this, "正在下载,请稍等", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();

                                        }else {
                                            if (AndroidUpdateUrl.equals("")) {
                                                Toast.makeText(DetailUpdateActivity.this, "获取的下载链接为空", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            } else {
                                                permissiongen();
                                                initialNotification();
                                              download();
                                                updateCondition = true;
                                                dialog.dismiss();

                                            }
                                        }

 */
                                });

                                if (CommonUtils.isFastDoubleClick()) {
                                    return;
                                } else {
                                    dialog.show();
                                }
                            }
                        } catch (IOException | JSONException e) {

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(DetailUpdateActivity.this, "请检查网络", Toast.LENGTH_SHORT)
                            .show();
                    }
                });
            }).start();
        } else if (id == R.id.update) {
            Intent intent = new Intent(DetailUpdateActivity.this, UpdateActivity.class);
            startActivity(intent);
        } else if (id == R.id.settings_img) {
            finish();
        }
    }

    /*
     * 方法名：initialNotification()
     * 功    能：初始化通知管理器,创建Notification
     * 参    数：无
     * 返回值：无
     */
    private void initialNotification() {
        //Notification跳转页面
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, DetailUpdateActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
        String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            Log.d("ssss","1");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            Log.d("ssss","2");
        }
        //初始化通知管理器

        //NotificationChannel channel =notificationManager.getNotificationChannel(PUSH_CHANNEL_ID);
        notificationManager.deleteNotificationChannel(PUSH_CHANNEL_ID);
        //8.0及以上需要设置好“channelId”（没有特殊要求、唯一即可）、“channelName”（用户看得到的信息）、“importance”（重要等级）这三个重要参数，然后创建到NotificationManager。


        */

        //创建Notification
        builder = new NotificationCompat.Builder(this, "sss");
        builder.setContentTitle("正在更新...") //设置通知标题
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.icon)//设置通知的小图标(有些手机设置Icon图标不管用，默认图标就是Manifest.xml里的图标)
            .setLargeIcon(BitmapFactory.decodeResource(DetailUpdateActivity.this.getResources(),
                R.drawable.icon)) //设置通知的大图标
            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //设置通知的提醒方式： 呼吸灯
            .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
            .setAutoCancel(false)//设置通知被点击一次是否自动取消
            .setContentText("下载进度:0%")
            .setChannelId(PUSH_CHANNEL_ID)
            .setProgress(100, 0, false);
        //进度最大100，默认是从0开始

        Notification notify = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                new NotificationChannel("to-do", "待办消息", NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] { 500 });
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId("to-do");
            notify = builder.build();
        } else {
            notify = builder.build();
        }

        notify.flags |= Notification.FLAG_AUTO_CANCEL; // 但用户点击消息后，消息自动在通知栏自动消失

        notificationManager.notify(1, notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
    }

    private void download() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(AndroidUpdateUrl).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG-失败", e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCondition = false;
                        Toast.makeText(getApplication(), "网络请求失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response)
                throws FileNotFoundException {
                Log.e("TAG-下载成功", response.code() + "---" + response.body().toString());
                //设置apk存储路径和名称
                File file = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/1037树洞.apk");

                //保存文件到本地
                localStorage(response, file);
            }
        });
    }

    /*
     * 方法名：localStorage(final Response response, final File file)
     * 功    能：保存文件到本地
     * 参    数：Response response, File file
     * 返回值：无
     */

    private void localStorage(final Response response, final File file)
        throws FileNotFoundException {
        //拿到字节流
        InputStream is = response.body().byteStream();
        int len = 0;
        final FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[2048];
        try {
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("to-do2", "待办消息",
                        NotificationManager.IMPORTANCE_LOW);
                    channel.enableVibration(false);
                    channel.setSound(null, null);
                    // channel.setVibrationPattern(new long[]{500});
                    notificationManager.createNotificationChannel(channel);
                    builder.setChannelId("to-do2");
                } else {
                }
                //Log.e("TAG每次写入到文件大小", "onResponse: "+len);
                Log.e("TAG保存到文件进度：", file.length() + "/" + response.body().contentLength());

                //notification进度条和显示内容不断变化，并刷新。
                builder.setProgress(100,
                    (int) (file.length() * 100 / response.body().contentLength()), false);
                builder.setContentText(
                    "下载进度:" + (int) (file.length() * 100 / response.body().contentLength()) + "%");
                builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
                Notification notification = builder.build();
                notificationManager.notify(1, notification);
            }
            fos.flush();
            fos.close();
            is.close();

            //下载完成，点击通知，安装
            installingAPK(file);
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCondition = false;
                    Toast.makeText(getApplication(), "下载失败", Toast.LENGTH_SHORT).show();
                    notificationManager.cancel(1);

                    notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent notificationIntent =
                        new Intent(DetailUpdateActivity.this, DetailUpdateActivity.class);

                    PendingIntent contentIntent =
                        PendingIntent.getActivity(DetailUpdateActivity.this, 0, notificationIntent,
                            0);
                    String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
                    String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";

                    //创建Notification
                    builder = new NotificationCompat.Builder(DetailUpdateActivity.this, "sss2");
                    builder.setContentTitle("下载失败") //设置通知标题
                        .setContentIntent(contentIntent)
                        .setSmallIcon(
                            R.mipmap.icon)//设置通知的小图标(有些手机设置Icon图标不管用，默认图标就是Manifest.xml里的图标)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(DetailUpdateActivity.this.getResources(),
                                R.mipmap.icon)) //设置通知的大图标
                        .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                        .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                        .setAutoCancel(true)//设置通知被点击一次是否自动取消
                        .setContentText("请重试")
                        .setOnlyAlertOnce(true)
                        .setChannelId(PUSH_CHANNEL_ID)
                        .setProgress(100, 0, false);
                    //进度最大100，默认是从0开始

                    Notification notify = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("to-do", "待办消息",
                            NotificationManager.IMPORTANCE_HIGH);
                        channel.enableVibration(false);
                        channel.setSound(null, null);
                        // channel.setVibrationPattern(new long[]{500});
                        notificationManager.createNotificationChannel(channel);
                        builder.setChannelId("to-do");
                        notify = builder.build();
                    } else {
                        notify = builder.build();
                    }
                    //使用默认的声音
                    //  notify.defaults |= Notification.DEFAULT_SOUND;
                    //notify.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.doorbell);
                    //   notify.defaults |= Notification.DEFAULT_VIBRATE;
                    notify.flags |= Notification.FLAG_AUTO_CANCEL; // 但用户点击消息后，消息自动在通知栏自动消失

                    notificationManager.notify(1,
                        notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示

                    //构建通知对象
                    Notification notification = builder.build();
                    notificationManager.notify(1, notification);


                    /*

                //    builder.setContentTitle("下载失败");
                            builder.setContentText("请重试");
                  //          builder.setAutoCancel(true);//设置通知被点击一次是否自动取消


                    Notification notification = builder.build();
                    notificationManager.notify(1, notification);
                    Log.d("hahahahaha","hahahahahaha");

                     */
                }
            });
            e.printStackTrace();
        }
    }

    /*
     * 方法名：installingAPK(File file)
     * 功    能：下载完成，点击通知，安装apk,适配安卓6.0,7.0,8.0
     * 参    数：File file
     * 返回值：无
     */
    private void installingAPK(final File file) {
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //安卓7.0以上需要在在Manifest.xml里的application里，设置provider路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "cn.pivotstudio.husthole.fileprovider", new File(file.getPath()));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        Log.d("sss","4");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            Log.d("sss","5");
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //下载完成后，设置notification为点击一次就关闭，并设置完成标题内容。并设置跳转到安装页面。
        builder.setContentTitle("下载完成")
                .setContentText("点击安装")
                .setAutoCancel(true)//设置通知被点击一次是否自动取消
                .setContentIntent(contentIntent);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
*/

        if (NotificationManagerCompat.from(DetailUpdateActivity.this).areNotificationsEnabled()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //安卓7.0以上需要在在Manifest.xml里的application里，设置provider路径
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri =
                    FileProvider.getUriForFile(this, "cn.pivotstudio.husthole.fileprovider",
                        new File(file.getPath()));
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            }
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
            updateCondition = false;
            //下载完成后，设置notification为点击一次就关闭，并设置完成标题内容。并设置跳转到安装页面。
            builder.setContentTitle("下载完成")
                .setContentText("点击安装")
                .setAutoCancel(true)//设置通知被点击一次是否自动取消
                .setContentIntent(contentIntent);

            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        } else {
            Intent var2 = new Intent();
            var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            var2.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                var2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri =
                    FileProvider.getUriForFile(this, "cn.pivotstudio.husthole.fileprovider",
                        new File(file.getPath()));
                var2.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                var2.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            try {
                DetailUpdateActivity.this.startActivity(var2);
            } catch (Exception var5) {
                var5.printStackTrace();
                Toast.makeText(DetailUpdateActivity.this, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }
}

package cn.pivotstudio.moduleb.libbase.util.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import cn.pivotstudio.moduleb.libbase.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @classname:UrlDownloadUtil
 * @description:根据url下载apk
 * @date:2022/5/3 16:01
 * @version:1.0
 * @author:
 */
public class UrlDownloadUtil {
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private final Context context;
    private final String downloadUtl;
    private final Class<?> cls;

    /**
     * 构造函数
     *
     * @param downloadUtl 下载的链接
     * @param cls 点击notification时，跳转的界面
     */
    public UrlDownloadUtil(Context context, String downloadUtl, Class<?> cls) {
        this.context = context;
        this.downloadUtl = downloadUtl;
        this.cls = cls;
        initialNotification();
    }

    /**
     * 通知界面初始化
     */
    private void initialNotification() {
        //Notification跳转页面

        notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, cls);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
        String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";

        builder = new NotificationCompat.Builder(context, "sss");
        builder.setContentTitle("正在更新...") //设置通知标题
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.icon)//设置通知的小图标(有些手机设置Icon图标不管用，默认图标就是Manifest.xml里的图标)
            .setLargeIcon(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.icon)) //设置通知的大图标
            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //设置通知的提醒方式： 呼吸灯
            .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
            .setAutoCancel(false)//设置通知被点击一次是否自动取消
            .setContentText("下载进度:0%")
            .setChannelId(PUSH_CHANNEL_ID)
            .setProgress(100, 0, false);
        //进度最大100，默认是从0开始

        Notification notify;
        NotificationChannel channel =
            new NotificationChannel("to-do", "待办消息", NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[] { 500 });
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId("to-do");
        notify = builder.build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL; // 但用户点击消息后，消息自动在通知栏自动消失

        notificationManager.notify(1, notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
    }

    /**
     * 下载网络连接
     */
    public void startDownload() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUtl).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(context, "网络请求失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws FileNotFoundException {
                // Log.e("TAG-下载成功", response.code() + "---" + Objects.requireNonNull(response.body()).toString());
                File file;
                file =
                    new File(context.getExternalFilesDir(null).getAbsolutePath() + "/1037树洞.apk");
                Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
                //设置apk存储路径和名称

                //保存文件到本地
                localStorage(response, file);
            }
        });
    }

    /**
     * 下载流程，同时同步通知
     *
     * @throws FileNotFoundException
     */
    private void localStorage(final Response response, final File file)
        throws FileNotFoundException {
        //拿到字节流
        InputStream is = Objects.requireNonNull(response.body()).byteStream();
        int len;
        final FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[2048];
        try {
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                //Log.e("TAG保存到文件进度：", file.length() + "/" + response.body().contentLength());
                NotificationChannel channel =
                    new NotificationChannel("to-do2", "待办消息", NotificationManager.IMPORTANCE_LOW);
                channel.enableVibration(false);
                channel.setSound(null, null);
                // channel.setVibrationPattern(new long[]{500});
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId("to-do2");
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

            installingAPK(file);//启动通知
            startInstallation(file);//启动安装
        } catch (IOException e) {

            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            notificationManager.cancel(1);

            notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(context, cls);

            PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
            String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
            String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";

            //创建Notification
            builder = new NotificationCompat.Builder(context, "sss2");

            builder.setContentTitle("下载失败") //设置通知标题
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.icon)//设置通知的小图标(有些手机设置Icon图标不管用，默认图标就是Manifest.xml里的图标)
                .setLargeIcon(
                    BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon)) //设置通知的大图标

                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setAutoCancel(true)//设置通知被点击一次是否自动取消
                .setContentText("请重试")
                .setOnlyAlertOnce(true)
                .setProgress(100, 0, false);
            //进度最大100，默认是从0开始

            Notification notify;
            NotificationChannel channel =
                new NotificationChannel("to-do3", "待办消息", NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId("to-do3");
            notify = builder.build();
            notify.flags |= Notification.FLAG_AUTO_CANCEL; // 但用户点击消息后，消息自动在通知栏自动消失
            notificationManager.notify(1, notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
        }
    }

    /**
     * 启动安装通知
     */
    private void installingAPK(final File file) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //安卓7.0以上需要在在Manifest.xml里的application里，设置provider路径
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri =
                FileProvider.getUriForFile(context, "cn.pivotstudio.husthole.fileprovider",
                    new File(file.getPath()));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

            //下载完成后，设置notification为点击一次就关闭，并设置完成标题内容。并设置跳转到安装页面。
            builder.setContentTitle("下载完成")
                .setContentText("点击安装")
                .setAutoCancel(true)//设置通知被点击一次是否自动取消
                .setContentIntent(contentIntent);

            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        }
    }

    /**
     * 启动安装
     */
    private void startInstallation(File file) {
        Intent var2 = new Intent();
        var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var2.setAction(Intent.ACTION_VIEW);
        var2.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(context, "cn.pivotstudio.husthole.fileprovider",
            new File(file.getPath()));
        var2.setDataAndType(contentUri, "application/vnd.android.package-archive");
        try {
            context.startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }
}

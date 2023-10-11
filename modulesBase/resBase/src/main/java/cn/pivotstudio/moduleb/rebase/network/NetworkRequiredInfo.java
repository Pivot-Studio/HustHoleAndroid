package cn.pivotstudio.moduleb.rebase.network;

import android.app.Application;

import cn.pivotstudio.moduleb.resbase.BuildConfig;

public class NetworkRequiredInfo implements INetworkRequiredInfo {

    private final Application application;

    public NetworkRequiredInfo(Application application) {
        this.application = application;
    }

    /**
     * 是否为debug
     */
    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    /**
     * 应用全局上下文
     */
    @Override
    public Application getApplicationContext() {
        return application;
    }
}

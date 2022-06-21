package cn.pivotstudio.modulec.homescreen.repository;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;
import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;
import cn.pivotstudio.modulec.homescreen.network.VersionResponse;
import cn.pivotstudio.modulec.homescreen.network.HSRequestInterface;

/**
 * @classname HomeScreenRepository
 * @description:
 * @date 2022/5/3 0:50
 * @version:1.0
 * @author: lzt
 */
@SuppressLint("CheckResult")
public class HomeScreenRepository {
    public MutableLiveData<VersionResponse> pHomeScreenVersionMsg;//版本信息
    public final MutableLiveData<String> failed;

    /**
     * 初始化
     */
    public HomeScreenRepository() {
        pHomeScreenVersionMsg = new MutableLiveData<>();
        failed = new MutableLiveData<>();
    }

    /**
     * 获取版本号
     */
    public void getVersionMsgForNetwork() {
        NetworkApi.createService(HSRequestInterface.class, 2).
                checkUpdate().compose(NetworkApi.applySchedulers(new BaseObserver<VersionResponse>() {
                    @Override
                    public void onSuccess(VersionResponse versionResponse) {
                        pHomeScreenVersionMsg.setValue(versionResponse);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
                    }
                }));
    }
}

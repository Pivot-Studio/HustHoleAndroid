package cn.pivotstudio.modulec.homescreen.repository;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import cn.pivotstudio.modulec.homescreen.network.VersionResponse;

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
}

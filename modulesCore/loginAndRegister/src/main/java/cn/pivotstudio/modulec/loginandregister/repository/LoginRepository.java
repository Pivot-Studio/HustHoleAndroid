package cn.pivotstudio.modulec.loginandregister.repository;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;


import cn.pivotstudio.husthole.moduleb.network.BaseObserver;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;

import cn.pivotstudio.husthole.moduleb.network.errorhandler.ExceptionHandler;

import cn.pivotstudio.modulec.loginandregister.model.LoginResponse;
import cn.pivotstudio.modulec.loginandregister.network.LRRequestInterface;


/**
 * @classname:LoginRepository
 * @description:
 * @date:2022/4/29 14:20
 * @version:1.0
 * @author:
 */
@SuppressLint("CheckResult")
public class LoginRepository {
    public final MutableLiveData<String> failed = new MutableLiveData<>();
    public  MutableLiveData<LoginResponse> login = new MutableLiveData<>();
  //  private final MMKVUtil mvUtil;
   // @Inject
    public LoginRepository() {

        //获取mvUtils
     //   MMKVUtilEntryPoint entryPoint =
        //        EntryPointAccessors.fromApplication(getContext(), MMKVUtilEntryPoint.class);
     //   mvUtil = entryPoint.getMVUtils();
    }
    public void getLoginForNetwork(String id, String password) {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", id);
        map.put("password", password);
        NetworkApi.createService(LRRequestInterface.class, 2).
                mobileLogin(map).compose(NetworkApi.applySchedulers(new BaseObserver<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                login.postValue(loginResponse);
            }

            @Override
            public void onFailure(Throwable e) {
                failed.postValue(((ExceptionHandler.ResponseThrowable) e).message);
            }
        }));

    }
}

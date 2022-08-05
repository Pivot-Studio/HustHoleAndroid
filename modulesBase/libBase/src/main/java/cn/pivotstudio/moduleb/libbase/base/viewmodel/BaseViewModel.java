package cn.pivotstudio.moduleb.libbase.base.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

/**
 * @classname: BaseViewModel
 * @description:
 * @date: 2022/5/2 19:26
 * @version:1.0
 * @author:
 */
@Deprecated
public class BaseViewModel extends ViewModel {
    public LiveData<String> failed;//放置网络请求失败的数据
}

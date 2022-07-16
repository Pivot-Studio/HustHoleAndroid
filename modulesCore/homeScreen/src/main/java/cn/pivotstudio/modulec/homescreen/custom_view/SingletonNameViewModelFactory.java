package cn.pivotstudio.modulec.homescreen.custom_view;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel;

/**
 * @classname:SingletonNameViewModelFactory
 * @description:暂时弃用，因为没法触发viewmodel的clear，单例工厂模式
 * @date:2022/5/5 18:47
 * @version:1.0
 * @author:
 */
public class SingletonNameViewModelFactory extends ViewModelProvider.NewInstanceFactory {


    HomePageViewModel t;

    public SingletonNameViewModelFactory() {
        //  t = provideNameViewModelSomeHowUsingDependencyInjection
    }

    @NonNull
    @Override
    public <H extends ViewModel> H create(@NonNull Class<H> modelClass) {
        return (H) t;
    }


}

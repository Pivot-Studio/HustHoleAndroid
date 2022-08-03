package cn.pivotstudio.moduleb.database.repository;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @classname:CustomDisposable
 * @description:
 * @date:2022/5/12 14:49
 * @version:1.0
 * @author:
 */
public class CustomDisposable {
    private static final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Flowable
     */
    public static <T> void addDisposable(Flowable<T> flowable, Consumer<T> consumer) {
        compositeDisposable.add(flowable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer));
    }

    /**
     * Completable
     */
    public static <T> void addDisposable(Completable completable, Action action) {
        compositeDisposable.add(completable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action));
    }
}

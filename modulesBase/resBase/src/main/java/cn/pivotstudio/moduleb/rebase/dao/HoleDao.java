package cn.pivotstudio.moduleb.rebase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cn.pivotstudio.moduleb.rebase.bean.Hole;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface HoleDao {
    @Query("SELECT * FROM hole")
    Flowable<List<Hole>> getAll();

    @Query("SELECT * FROM hole WHERE uid=:uid")
    Flowable<Hole> findById(int uid);

    @Update
    Completable update(Hole hole);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Hole hole);

    @Delete
    Completable delete(Hole... hole);
}

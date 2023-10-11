package cn.pivotstudio.moduleb.rebase.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cn.pivotstudio.moduleb.rebase.bean.Hole;
import cn.pivotstudio.moduleb.rebase.dao.HoleDao;

@Database(entities = {Hole.class}, version = 5, exportSchema = true)
public abstract class HustHoleRoomDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "hust_hole";
    private static volatile HustHoleRoomDatabase mInstance;

    /**
     * 单例模式
     */
    public static HustHoleRoomDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (HustHoleRoomDatabase.class) {
                if (mInstance == null) {
                    mInstance =
                            Room.databaseBuilder(
                                            context.getApplicationContext(),
                                            HustHoleRoomDatabase.class,
                                            DATABASE_NAME)
                                    .fallbackToDestructiveMigration().build();
                }
            }
        }
        return mInstance;
    }

    public abstract HoleDao holeDao();
}

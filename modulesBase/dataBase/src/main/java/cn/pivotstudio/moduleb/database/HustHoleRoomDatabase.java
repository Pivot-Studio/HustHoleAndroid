package cn.pivotstudio.moduleb.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import cn.pivotstudio.moduleb.database.bean.Hole;
import cn.pivotstudio.moduleb.database.dao.HoleDao;

/**
 * @classname:AppDatabase
 * @description:数据库，存放每个树洞的相关存储信息
 * @date:2022/4/28 20:43
 * @version:1.0
 * @author:
 */
@Database(entities = {Hole.class}, version = 2, exportSchema = false)
public abstract class HustHoleRoomDatabase extends RoomDatabase {

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `hole` "
                    + "(uid INTEGER NOT NULL, "
                    + "content TEXT, "
                    + "alias TEXT, "
                    + "is_mine BOOLEAN, "
                    + "reply_local_id INTEGER NOT NULL, "
                    + "PRIMARY KEY(`uid`))");
        }
    };
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
                            Room.databaseBuilder(context.getApplicationContext(), HustHoleRoomDatabase.class,
                                    DATABASE_NAME).addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return mInstance;
    }

    public abstract HoleDao holeDao();
}

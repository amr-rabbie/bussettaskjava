package androiddeveloper.amrrabbie.buseettask.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Restrant.class},version = 2)
public abstract class RestrantDb extends RoomDatabase {
    public abstract RestrantDao restrantDao();
}

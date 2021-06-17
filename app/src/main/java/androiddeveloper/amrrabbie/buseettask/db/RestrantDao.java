package androiddeveloper.amrrabbie.buseettask.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RestrantDao {

    @Insert
    public void insertRestrant(Restrant restrant);

    @Query("delete from restrant_table where id=:mid")
    public void deleteRestrant(int mid);

    @Query("select * from restrant_table")
    public LiveData<List<Restrant>> getAllRestrants();
}

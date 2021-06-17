package androiddeveloper.amrrabbie.buseettask.di;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import androiddeveloper.amrrabbie.buseettask.db.RestrantDao;
import androiddeveloper.amrrabbie.buseettask.db.RestrantDb;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class DbModule {


    @Provides
    @Singleton
    public static RestrantDb provideRestrantDb(Application application){
        return Room.databaseBuilder(application.getApplicationContext(),
                RestrantDb.class,
                "restrant_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    @Provides
    @Singleton
    public static RestrantDao provideRestrantDao(RestrantDb restrantDb){
        return restrantDb.restrantDao();
    }
}

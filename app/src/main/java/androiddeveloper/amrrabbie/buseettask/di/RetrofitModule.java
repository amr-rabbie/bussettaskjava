package androiddeveloper.amrrabbie.buseettask.di;


import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import androiddeveloper.amrrabbie.buseettask.network.RestrantsApiService;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(ApplicationComponent.class)
public class RetrofitModule {


    public static final String Api_Url="https://maps.googleapis.com/maps/api/";


    @Provides
    @Singleton
    public static OkHttpClient provideOkhttpclient(){
        return new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }


    @Provides
    @Singleton
    public static RestrantsApiService provideRestrantsApiService(OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .baseUrl(Api_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(RestrantsApiService.class);
    }


}

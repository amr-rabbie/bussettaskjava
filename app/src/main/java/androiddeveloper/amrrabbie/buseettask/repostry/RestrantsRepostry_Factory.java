package androiddeveloper.amrrabbie.buseettask.repostry;

import androiddeveloper.amrrabbie.buseettask.network.RestrantsApiService;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class RestrantsRepostry_Factory implements Factory<RestrantsRepostry> {
  private final Provider<RestrantsApiService> restrantsApiServiceProvider;

  public RestrantsRepostry_Factory(Provider<RestrantsApiService> restrantsApiServiceProvider) {
    this.restrantsApiServiceProvider = restrantsApiServiceProvider;
  }

  @Override
  public RestrantsRepostry get() {
    return newInstance(restrantsApiServiceProvider.get());
  }

  public static RestrantsRepostry_Factory create(
      Provider<RestrantsApiService> restrantsApiServiceProvider) {
    return new RestrantsRepostry_Factory(restrantsApiServiceProvider);
  }

  public static RestrantsRepostry newInstance(RestrantsApiService restrantsApiService) {
    return new RestrantsRepostry(restrantsApiService);
  }
}

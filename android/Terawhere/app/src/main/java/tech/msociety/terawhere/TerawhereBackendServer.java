package tech.msociety.terawhere;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import tech.msociety.terawhere.intereptors.LoggingInterceptor;

public class TerawhereBackendServer {
    private static Api api;

    public static Api getApiInstance() {
        if (api != null) {
            return api;
        }

        Retrofit retrofit = new Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient())
                .build();

        return retrofit.create(Api.class);
    }

    private static OkHttpClient getHttpClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//        httpClientBuilder.addInterceptor(new QueryParamsInterceptor());
//        httpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        httpClientBuilder.addInterceptor(new LoggingInterceptor());
        return httpClientBuilder.build();
    }

    public interface Api {
        @Headers({
                "Authorization: Bearer " + Constants.JWT_TOKEN_AZIZ,
                "User-Agent: Retrofit-Sample-App"
        })
        @GET("api/v1/offers")
        Call<GetOffers> getOffers();
    }
}

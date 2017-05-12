package tech.msociety.terawhere;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class TerawhereBackendServer {
    private static Api api;
    String value;

    public static Api getApiInstance(final String token) {
        if (api != null) {
            return api;
        }
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        ongoing.addHeader("Accept", "application/json");

                        ongoing.addHeader("Authorization", "Bearer " + token);
                        ongoing.addHeader("Content-Type", "application/x-www-form-urlencoded");

                        return chain.proceed(ongoing.build());
                    }
                })
                .build();



        Retrofit retrofit = new Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();


        return retrofit.create(Api.class);
    }

    /*
    private static OkHttpClient getHttpClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new QueryParamsInterceptor());
        httpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        httpClientBuilder.addInterceptor(new LoggingInterceptor());
        return httpClientBuilder.build();
    }
*/
    public interface Api {

        @POST("api/v1/auth")
        Call<FacebookUser> createUser(@Body FacebookUser user);

        @GET("api/v1/me")
        Call<GetUser> getStatus();

        @GET("api/v1/offers-for-user")
        Call<GetOffers> getOffers();

        @GET("api/v1/offers")
        Call<GetOffers> getAllOffers();

        @POST("api/v1/offers")
        Call<OffersDatum> createOffer(@Body OffersDatum offers);

        @GET("api/v1/auth/refresh")
        Call<RefreshToken> getRefresh();

        @DELETE("api/v1/offers/{offer}")
        Call<OffersDatum> deleteOffer(@Path("offer") Integer id);

        @PUT("api/v1/offers/{offer}")
        Call<OffersDatum> editOffer(@Path("offer") Integer id, @Body OffersDatum offers);


    }
}

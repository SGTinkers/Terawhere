package tech.msociety.terawhere.networkcalls.server;

import okhttp3.OkHttpClient;
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
import tech.msociety.terawhere.BookingDatum;
import tech.msociety.terawhere.Constants;
import tech.msociety.terawhere.FacebookUser;
import tech.msociety.terawhere.GetBookings;
import tech.msociety.terawhere.GetOffers;
import tech.msociety.terawhere.GetUser;
import tech.msociety.terawhere.OffersDatum;
import tech.msociety.terawhere.RefreshToken;
import tech.msociety.terawhere.networkcalls.intereptors.ApiHeaderInterceptor;
import tech.msociety.terawhere.networkcalls.intereptors.LoggingInterceptor;

public class TerawhereBackendServer {
    public static Api getApiInstance(final String token) {
        Retrofit retrofit = new Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient(token))
                .build();
        
        return retrofit.create(Api.class);
    }
    
    private static OkHttpClient getHttpClient(String token) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//        httpClientBuilder.addInterceptor(new QueryParamsInterceptor());
//        httpClientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        httpClientBuilder.addInterceptor(new ApiHeaderInterceptor(token));
        httpClientBuilder.addInterceptor(new LoggingInterceptor());
        return httpClientBuilder.build();
    }
    
    public interface Api {
        @POST("api/v1/auth")
        Call<FacebookUser> createUser(@Body FacebookUser user);
        
        @GET("api/v1/me")
        Call<GetUser> getStatus();
        
        @GET("api/v1/offers-for-user")
        Call<GetOffers> getOffers();
        
        @GET("api/v1/offers")
        Call<GetOffers> getAllOffers();
        
        @GET("api/v1/bookings-for-user")
        Call<GetBookings> getAllBookings();
        
        @POST("api/v1/offers")
        Call<OffersDatum> createOffer(@Body OffersDatum offers);
        
        @POST("api/v1/bookings")
        Call<BookingDatum> createBooking(@Body BookingDatum booking);
        
        @GET("api/v1/auth/refresh")
        Call<RefreshToken> getRefresh();
        
        @DELETE("api/v1/offers/{offer}")
        Call<OffersDatum> deleteOffer(@Path("offer") Integer id);
        
        @PUT("api/v1/offers/{offer}")
        Call<OffersDatum> editOffer(@Path("offer") Integer id, @Body OffersDatum offers);
    }
}

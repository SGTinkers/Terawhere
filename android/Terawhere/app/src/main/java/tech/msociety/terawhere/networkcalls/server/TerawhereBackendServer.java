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
import tech.msociety.terawhere.TerawhereApplication;
import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.networkcalls.intereptors.AuthorizationRequestInterceptor;
import tech.msociety.terawhere.networkcalls.intereptors.AuthorizationResponseInterceptor;
import tech.msociety.terawhere.networkcalls.intereptors.ConnectivityInterceptor;
import tech.msociety.terawhere.networkcalls.intereptors.DefaultInterceptor;
import tech.msociety.terawhere.networkcalls.intereptors.LoggingInterceptor;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.createuser.FacebookUser;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getbookings.BookingDatum;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getbookings.GetBookings;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.GetOffersResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.PostOffers;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser.GetUserDetailsResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.setlocation.LocationDatum;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken.DeviceTokenDatum;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken.StoreDeviceToken;

public class TerawhereBackendServer {

    private static Api api;

    public static Api getApiInstance() {
        if (api == null) {
            // Faruq: Do not mess with the interceptor order
            Retrofit retrofit = new Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder()
                            .addInterceptor(new ConnectivityInterceptor(TerawhereApplication.ApplicationContext))
                            .addInterceptor(new DefaultInterceptor())
                            .addInterceptor(new AuthorizationRequestInterceptor())
                            .addInterceptor(new LoggingInterceptor())
                            .addInterceptor(new AuthorizationResponseInterceptor())
                            .build())
                    .build();

            api = retrofit.create(Api.class);
        }

        return api;
    }

    public static Api getAuthApiInstance() {
        Retrofit retrofit = new Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new ConnectivityInterceptor(TerawhereApplication.ApplicationContext))
                        .addInterceptor(new DefaultInterceptor())
                        .addInterceptor(new AuthorizationRequestInterceptor(false))
                        .addInterceptor(new LoggingInterceptor())
                        .addInterceptor(new AuthorizationResponseInterceptor())
                        .build())
                .build();

        return retrofit.create(Api.class);
    }

    public interface Api {
        @POST("api/v1/auth")
        Call<FacebookUser> createUser(@Body FacebookUser user);

        @GET("api/v1/auth/refresh")
        Call<Void> refreshToken();

        @POST("api/v1/devices")
        Call<StoreDeviceToken> storeDeviceToken(@Body DeviceTokenDatum deviceToken);

        @GET("api/v1/me")
        Call<GetUserDetailsResponse> getStatus();

        @GET("api/v1/offers-for-user")
        Call<GetOffersResponse> getOffers();

        @GET("api/v1/users/me/bookings")
        Call<GetBookings> getAllBookings();

        @POST("api/v1/nearby-offers")
        Call<GetOffersResponse> getNearbyOffers(@Body LocationDatum location);
    
        @POST("api/v1/offers")
        Call<Void> createOffer(@Body PostOffers offers);

        @POST("api/v1/bookings")
        Call<BookingDatum> createBooking(@Body BookingDatum booking);

        @DELETE("api/v1/offers/{offer}")
        Call<Void> deleteOffer(@Path("offer") Integer id);

        @PUT("api/v1/offers/{offer}")
        Call<Void> editOffer(@Path("offer") Integer id, @Body PostOffers offers);
    }
}

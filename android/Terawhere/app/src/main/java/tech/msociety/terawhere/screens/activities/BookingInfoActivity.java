package tech.msociety.terawhere.screens.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.adapters.BookingsInfoAdapter;
import tech.msociety.terawhere.events.ResponseNotSuccessfulEvent;
import tech.msociety.terawhere.exceptions.NetworkCallFailedException;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.models.factories.BookingFactory;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings.GetBookingsResponse;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;

/**
 * Created by musa on 24/5/17.
 */

public class BookingInfoActivity extends Activity {

    private BookingsInfoAdapter bookingsInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_info);
        initRecyclerView();
        Integer offerId = getIntent().getExtras().getInt("offerId");
        getBookingsFromServer(offerId);
    }


    private void initRecyclerView() {


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_bookings_info);


        bookingsInfoAdapter = new BookingsInfoAdapter(getApplicationContext());
        recyclerView.setAdapter(bookingsInfoAdapter);
    }

    private void getBookingsFromServer(Integer offerId) {

        Call<GetBookingsResponse> callGetBookings = TerawhereBackendServer.getApiInstance().getAllBookingsByOffer(offerId);
        callGetBookings.enqueue(new Callback<GetBookingsResponse>() {
            @Override
            public void onResponse(Call<GetBookingsResponse> call, Response<GetBookingsResponse> response) {
                if (response.isSuccessful()) {
                    GetBookingsResponse getBookingsResponse = response.body();
                    List<Booking> bookings = BookingFactory.createFromResponse(getBookingsResponse);
                    Log.i("BOOKING_SIZE", ":" + bookings.size());
                    bookingsInfoAdapter.setBookingsInfo(bookings);

                } else {
                    onFailure(call, new NetworkCallFailedException("Response not successful."));
                }
            }

            @Override
            public void onFailure(Call<GetBookingsResponse> call, Throwable t) {
                EventBus.getDefault().post(new ResponseNotSuccessfulEvent(t));
            }
        });
    }


}

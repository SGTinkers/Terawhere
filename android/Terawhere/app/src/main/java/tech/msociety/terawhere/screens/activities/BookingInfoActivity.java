package tech.msociety.terawhere.screens.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import tech.msociety.terawhere.screens.activities.abstracts.ToolbarActivity;


public class BookingInfoActivity extends ToolbarActivity {

    private static final String TOOLBAR_TITLE = "Your Passengers";
    public static final String INTENT_OFFER_ID = "INTENT_OFFER_ID";

    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_info);
        initToolbar(TOOLBAR_TITLE, true);

        Integer offerId = getIntent().getExtras().getInt(INTENT_OFFER_ID);
        getBookingsFromServer(offerId);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_bookings_info);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new BookingsInfoAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    private void getBookingsFromServer(Integer offerId) {
        Call<GetBookingsResponse> callGetBookings = TerawhereBackendServer.getApiInstance().getAllBookingsByOffer(offerId);
        callGetBookings.enqueue(new Callback<GetBookingsResponse>() {
            @Override
            public void onResponse(Call<GetBookingsResponse> call, Response<GetBookingsResponse> response) {
                if (response.isSuccessful()) {
                    initRecyclerView();
                    GetBookingsResponse getBookingsResponse = response.body();
                    List<Booking> bookings = BookingFactory.createFromResponseBookingInfo(getBookingsResponse);

                    ((BookingsInfoAdapter) adapter).setBookingsInfo(bookings);
                    adapter.notifyDataSetChanged();

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

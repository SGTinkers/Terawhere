package tech.msociety.terawhere.screens.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.adapters.BookingsAdapter;
import tech.msociety.terawhere.events.GetBookingsHasFinishedEvent;
import tech.msociety.terawhere.events.ResponseNotSuccessfulEvent;
import tech.msociety.terawhere.exceptions.NetworkCallFailedException;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getbookings.GetBookings;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.fragments.abstracts.BaseFragment;

public class MyBookingsFragment extends BaseFragment {
    private BookingsAdapter bookingsAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.needsProgressDialog = true;
        this.needsEventBus = true;
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_my_bookings, container, false);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        initRecyclerView();
        getBookingsFromServer();
    }
    
    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerViewMyBookings);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        
        bookingsAdapter = new BookingsAdapter();
        recyclerView.setAdapter(bookingsAdapter);
    }
    
    private void getBookingsFromServer() {
        progressDialog.setMessage("Getting bookings...");
        progressDialog.show();
        Call<GetBookings> callGetBookings = TerawhereBackendServer.getApiInstance().getAllBookings();
        callGetBookings.enqueue(new Callback<GetBookings>() {
            @Override
            public void onResponse(Call<GetBookings> call, Response<GetBookings> response) {
                progressDialog.cancel();
                if (response.isSuccessful()) {
                    GetBookings getBookings = response.body();
                    List<Booking> bookings = getBookings.getBookings();
                    EventBus.getDefault().post(new GetBookingsHasFinishedEvent(bookings));
                } else {
                    onFailure(call, new NetworkCallFailedException("Response not successful."));
                }
            }
    
            @Override
            public void onFailure(Call<GetBookings> call, Throwable t) {
                progressDialog.cancel();
                EventBus.getDefault().post(new ResponseNotSuccessfulEvent(t));
            }
        });
    }
    
    @Subscribe
    public void populateRecyclerView(GetBookingsHasFinishedEvent event) {
        progressDialog.cancel();
        bookingsAdapter.setBookings(event.getBookings());
        bookingsAdapter.notifyDataSetChanged();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void responseNotSuccessfulEvent(ResponseNotSuccessfulEvent event) throws Throwable {
        Log.e(TAG, "failed to fetch my offers via network call", event.getThrowable());
    }
}

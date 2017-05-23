package tech.msociety.terawhere.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import tech.msociety.terawhere.models.factories.BookingFactory;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings.GetBookingsResponse;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.fragments.abstracts.BaseFragment;

public class MyBookingsFragment extends BaseFragment {
    private BookingsAdapter bookingsAdapter;
    
    private SwipeRefreshLayout swipeRefreshLayoutBookings;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.needsEventBus = true;
        setHasOptionsMenu(false);
        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);
        swipeRefreshLayoutBookings = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_bookings);
        return view;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        initRecyclerView();
        getBookingsFromServer();
    }
    
    private void initRecyclerView() {
        swipeRefreshLayoutBookings.setColorSchemeResources(R.color.colorTerawherePrimary);
    
        swipeRefreshLayoutBookings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBookingsFromServer();
            }
        });
    
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_my_bookings);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        
        bookingsAdapter = new BookingsAdapter(getContext());
        recyclerView.setAdapter(bookingsAdapter);
    }
    
    private void getBookingsFromServer() {
        swipeRefreshLayoutBookings.setRefreshing(true);

        Call<GetBookingsResponse> callGetBookings = TerawhereBackendServer.getApiInstance().getAllBookings();
        callGetBookings.enqueue(new Callback<GetBookingsResponse>() {
            @Override
            public void onResponse(Call<GetBookingsResponse> call, Response<GetBookingsResponse> response) {
                if (response.isSuccessful()) {
                    GetBookingsResponse getBookingsResponse = response.body();
                    List<Booking> bookings = BookingFactory.createFromResponse(getBookingsResponse);
                    EventBus.getDefault().post(new GetBookingsHasFinishedEvent(bookings));

                } else {
                    onFailure(call, new NetworkCallFailedException("Response not successful."));
                }
                swipeRefreshLayoutBookings.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<GetBookingsResponse> call, Throwable t) {
                EventBus.getDefault().post(new ResponseNotSuccessfulEvent(t));
            }
        });
    }

    @Subscribe
    public void populateRecyclerView(GetBookingsHasFinishedEvent event) {
        bookingsAdapter.setBookings(event.getBookings());
        bookingsAdapter.notifyDataSetChanged();
    
        RecyclerView recyclerViewMyOffers = (RecyclerView) getActivity().findViewById(R.id.recycler_view_my_bookings);
        TextView textViewEmptyRecyclerView = (TextView) getActivity().findViewById(R.id.text_view_empty_recycler_view_bookings);
    
        if (bookingsAdapter.getItemCount() == 0) {
            recyclerViewMyOffers.setVisibility(View.GONE);
            textViewEmptyRecyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewMyOffers.setVisibility(View.VISIBLE);
            textViewEmptyRecyclerView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void responseNotSuccessfulEvent(ResponseNotSuccessfulEvent event) throws Throwable {
        Log.e(TAG, "failed to fetch my bookings via network call", event.getThrowable());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getBookingsFromServer();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();


        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

        getBookingsFromServer();
    }
}

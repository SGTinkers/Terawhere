package tech.msociety.terawhere.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import tech.msociety.terawhere.adapters.OffersAdapter;
import tech.msociety.terawhere.events.GetOffersHasFinishedEvent;
import tech.msociety.terawhere.events.ResponseNotSuccessfulEvent;
import tech.msociety.terawhere.exceptions.NetworkCallFailedException;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.models.factories.OfferFactory;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers.GetOffersResponse;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.CreateOfferActivity;
import tech.msociety.terawhere.screens.fragments.abstracts.BaseFragment;

public class MyOffersFragment extends BaseFragment {
    private static final int REQUEST_CODE = 1;
    public static final String IS_EDIT = "isEdit";
    public static final String START_TERAWHERE_LOCATION = "startTerawhereLocation";
    public static final String END_TERAWHERE_LOCATION = "endTerawhereLocation";
    public static final String VEHICLE = "vehicle";
    public static final String OFFER_ID = "offerId";
    public static final String DRIVER_ID = "driverId";
    public static final String MEET_UP_TIME = "meetUpTime";
    public static final String DRIVER_REMARKS = "driverRemarks";
    public static final String SEATS_AVAILABLE = "seatsAvailable";

    private OffersAdapter offersAdapter;
    
    private SwipeRefreshLayout swipeRefreshLayoutOffers;

    private Offer lastOffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.needsEventBus = true;
        View view = inflater.inflate(R.layout.fragment_my_offers, container, false);
        swipeRefreshLayoutOffers = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_offers);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initFab();
        initRecyclerView();
        getOffersFromServer();
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fabAddRecord);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOffersFromServer();
                TerawhereLocation startTerawhereLocation = null;
                TerawhereLocation endTerawhereLocation = null;
                Vehicle vehicle = null;
                Intent intent = new Intent(v.getContext(), CreateOfferActivity.class);

                if (lastOffer != null) {
                    // start location
                    startTerawhereLocation = new TerawhereLocation(
                            lastOffer.getStartTerawhereLocation().getName(),
                            lastOffer.getStartTerawhereLocation().getAddress(),
                            lastOffer.getStartTerawhereLocation().getLatitude(),
                            lastOffer.getStartTerawhereLocation().getLongitude(),
                            lastOffer.getStartTerawhereLocation().getGeohash());

                    // end location
                    endTerawhereLocation = new TerawhereLocation(
                            lastOffer.getEndTerawhereLocation().getName(),
                            lastOffer.getEndTerawhereLocation().getAddress(),
                            lastOffer.getEndTerawhereLocation().getLatitude(),
                            lastOffer.getEndTerawhereLocation().getLongitude(),
                            lastOffer.getEndTerawhereLocation().getGeohash());

                    // vehicle info
                    vehicle = new Vehicle(
                            lastOffer.getVehicle().getPlateNumber(),
                            lastOffer.getVehicle().getDescription(),
                            lastOffer.getVehicle().getModel());

                    // store values for create offer activity
                    intent.putExtra(IS_EDIT, true);
                    intent.putExtra(START_TERAWHERE_LOCATION, startTerawhereLocation);
                    intent.putExtra(END_TERAWHERE_LOCATION, endTerawhereLocation);
                    intent.putExtra(VEHICLE, vehicle);
                    intent.putExtra(OFFER_ID, lastOffer.getOfferId());
                    intent.putExtra(DRIVER_ID, lastOffer.getOffererId());
                    intent.putExtra(MEET_UP_TIME, lastOffer.getMeetupTime());
                    intent.putExtra(DRIVER_REMARKS, lastOffer.getRemarks());
                    intent.putExtra(SEATS_AVAILABLE, lastOffer.getVacancy());

                }


                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void initRecyclerView() {
        swipeRefreshLayoutOffers.setColorSchemeResources(R.color.colorTerawherePrimary);
    
        swipeRefreshLayoutOffers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOffersFromServer();
            }
        });
    
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_my_offers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        offersAdapter = new OffersAdapter();
        recyclerView.setAdapter(offersAdapter);
    }

    private void getOffersFromServer() {
        swipeRefreshLayoutOffers.setRefreshing(true);
        Call<GetOffersResponse> callGetOffers = TerawhereBackendServer.getApiInstance().getOffers();
        callGetOffers.enqueue(new Callback<GetOffersResponse>() {
            @Override
            public void onResponse(Call<GetOffersResponse> call, Response<GetOffersResponse> response) {
                if (response.isSuccessful()) {
                    GetOffersResponse getOffersResponse = response.body();
                    List<Offer> offers = OfferFactory.createFromResponse(getOffersResponse);
                    Log.i("SIZE", ":" + offers.size());

                    if (!offers.isEmpty()) {
                        lastOffer = offers.get(offers.size() - 1);
                    }
                    EventBus.getDefault().post(new GetOffersHasFinishedEvent(offers));
                } else {
                    onFailure(call, new NetworkCallFailedException("Response not successful."));
                }
                swipeRefreshLayoutOffers.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<GetOffersResponse> call, Throwable t) {
                EventBus.getDefault().post(new ResponseNotSuccessfulEvent(t));
            }
        });
    }

    @Subscribe
    public void populateRecyclerView(GetOffersHasFinishedEvent event) {
        offersAdapter.setOffers(event.getOffers());
        offersAdapter.notifyDataSetChanged();
    
        RecyclerView recyclerViewMyOffers = (RecyclerView) getActivity().findViewById(R.id.recycler_view_my_offers);
        TextView textViewEmptyRecyclerView = (TextView) getActivity().findViewById(R.id.text_view_empty_recycler_view_offers);
    
        if (offersAdapter.getItemCount() == 0) {
            recyclerViewMyOffers.setVisibility(View.GONE);
            textViewEmptyRecyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewMyOffers.setVisibility(View.VISIBLE);
            textViewEmptyRecyclerView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void responseNotSuccessfulEvent(ResponseNotSuccessfulEvent event) throws Throwable {
        Log.e(TAG, "failed to fetch my offers via network call", event.getThrowable());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getOffersFromServer();
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

        getOffersFromServer();
    }
}

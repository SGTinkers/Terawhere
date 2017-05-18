package tech.msociety.terawhere.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import tech.msociety.terawhere.R.layout;
import tech.msociety.terawhere.adapters.OffersAdapter;
import tech.msociety.terawhere.events.GetOffersHasFinishedEvent;
import tech.msociety.terawhere.events.ResponseNotSuccessfulEvent;
import tech.msociety.terawhere.exceptions.NetworkCallFailedException;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.GetOffers;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.CreateOfferActivity;
import tech.msociety.terawhere.screens.fragments.abstracts.BaseFragment;

public class MyOffersFragment extends BaseFragment {
    private static final int REQUEST_CODE = 1;
    private OffersAdapter offersAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.needsProgressDialog = true;
        this.needsEventBus = true;
        return inflater.inflate(layout.fragment_my_offers, container, false);
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
                Intent intent = new Intent(v.getContext(), CreateOfferActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }
    
    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerViewMyOffers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        
        offersAdapter = new OffersAdapter();
        recyclerView.setAdapter(offersAdapter);
    }
    
    private void getOffersFromServer() {
        progressDialog.show();
        Call<GetOffers> callGetOffers = TerawhereBackendServer.getApiInstance().getOffers();
        callGetOffers.enqueue(new Callback<GetOffers>() {
            @Override
            public void onResponse(Call<GetOffers> call, Response<GetOffers> response) {
                progressDialog.cancel();
                if (response.isSuccessful()) {
                    GetOffers getOffers = response.body();
                    List<Offer> offers = getOffers.getOffers();
                    EventBus.getDefault().post(new GetOffersHasFinishedEvent(offers));
                } else {
                    Log.e(TAG, "inside onResponse's notSuccessful");
                    onFailure(call, new NetworkCallFailedException("Response not successful."));
                }
            }
    
            @Override
            public void onFailure(Call<GetOffers> call, Throwable t) {
                progressDialog.cancel();
                Log.e(TAG, "inside onFailure");
                EventBus.getDefault().post(new ResponseNotSuccessfulEvent(t));
            }
        });
    }
    
    @Subscribe
    public void populateRecyclerView(GetOffersHasFinishedEvent event) {
        progressDialog.cancel();
        offersAdapter.setOffers(event.getOffers());
        offersAdapter.notifyDataSetChanged();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void responseNotSuccessfulEvent(ResponseNotSuccessfulEvent event) throws Throwable {
        Log.e(TAG, "failed to fetch my offers via network call", event.getThrowable());
    }
}

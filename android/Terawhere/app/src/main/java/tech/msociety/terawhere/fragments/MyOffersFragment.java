package tech.msociety.terawhere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R.id;
import tech.msociety.terawhere.R.layout;
import tech.msociety.terawhere.TerawhereBackendServer;
import tech.msociety.terawhere.TerawhereBackendServer.Api;
import tech.msociety.terawhere.activities.CreateOfferActivity;
import tech.msociety.terawhere.adapters.OffersAdapter;
import tech.msociety.terawhere.mocks.BackendMock;
import tech.msociety.terawhere.models.Offer;

import static android.app.Activity.RESULT_OK;

public class MyOffersFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    private Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layout.fragment_my_offers, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initFab();
        initRecyclerView();
        populateListFromDatabase();

        makeNetworkCall();
    }

    private static void makeNetworkCall() {
        Api api = TerawhereBackendServer.getApiInstance();
        Call<ResponseBody> call = api.getOffers();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d("response: ", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(Arrays.toString(t.getStackTrace()));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                populateListFromDatabase();
            }
        }
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(id.fabAddRecord);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), CreateOfferActivity.class), REQUEST_CODE);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(id.recyclerViewMyOffers);

        LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new OffersAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void populateListFromDatabase() {
        List<Offer> offers = BackendMock.getOffers();
        ((OffersAdapter) adapter).setOffers(offers);
        adapter.notifyDataSetChanged();
    }
}

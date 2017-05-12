package tech.msociety.terawhere.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.GetOffers;
import tech.msociety.terawhere.GetUser;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.R.layout;
import tech.msociety.terawhere.TerawhereBackendServer;
import tech.msociety.terawhere.Token;
import tech.msociety.terawhere.activities.CreateOfferActivity;
import tech.msociety.terawhere.adapters.OffersAdapter;
import tech.msociety.terawhere.models.Offer;

import static android.app.Activity.RESULT_OK;

public class MyOffersFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    private Adapter adapter;
    private String header;
    ProgressDialog loading = null;
    boolean flag = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layout.fragment_my_offers, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        loading = new ProgressDialog(getContext());
        loading.setCancelable(true);
        loading.setMessage("FETCHING DATA");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();

        initFab();
        initRecyclerView();


        makeNetworkCall();


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getContext() != null) {
                loading = new ProgressDialog(getContext());
                loading.setCancelable(true);
                loading.setMessage("FETCHING DATA");
                loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loading.show();
                Log.i("VISIBLE", "REFRESHING");
                makeNetworkCall();
            }

        } else {
            Log.i("VISIBLE", "NO");

        }
    }

    public void onResume() {
        super.onResume();


    }

    private void makeNetworkCall() {
        Log.i("MAKING NETWORK", ":");

        Call<GetUser> callUser = TerawhereBackendServer.getApiInstance(Token.getToken()).getStatus();

        callUser.enqueue(new Callback<GetUser>() {
            @Override
            public void onResponse(Call<GetUser> call, Response<GetUser> response) {

                if (response.isSuccessful()) {
                    Log.i("RESPONSE", response.body().toString());
                    Log.i("user id", response.body().getUser().getId());

                    fetchOffersFromServer();

                } else {
                    Log.i("RESPONSE", response.errorBody().toString());

                   /* try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.i("ERROR", ":" + jObjError.getString("error"));
                        if (jObjError.getString("error").equals("token_expired")) {
                            //refresh token
                        }

                    } catch (Exception e) {
                    }*/
                }
            }

            @Override
            public void onFailure(Call<GetUser> call, Throwable t) {
                Log.i("FAILURE", Arrays.toString(t.getStackTrace()));

                System.out.println(Arrays.toString(t.getStackTrace()));

            }
        });


    }

    private void fetchOffersFromServer() {
        Call<GetOffers> callGetOffers = TerawhereBackendServer.getApiInstance(Token.getToken()).getOffers();
        callGetOffers.enqueue(new Callback<GetOffers>() {
            @Override
            public void onResponse(Call<GetOffers> call, Response<GetOffers> response2) {

                if (response2.isSuccessful()) {
                    GetOffers getOffers = response2.body();

                    List<Offer> offers = getOffers.getOffers();

                    populateListFromDatabase(offers);
                    Log.i("response: ", getOffers.toString());
                    loading.dismiss();


                } else {
                    loading.dismiss();

                    try {
                        Log.i("ERROR_OFFER", ": " + response2.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<GetOffers> call, Throwable t) {
                System.out.println(Arrays.toString(t.getStackTrace()));

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            }
        }
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

        adapter = new OffersAdapter();
        recyclerView.setAdapter(adapter);
    }

    void populateListFromDatabase(List<Offer> offers) {
//        List<Offer> offers = BackendMock.getOffers();
        ((OffersAdapter) adapter).setOffers(offers);
        adapter.notifyDataSetChanged();
    }
}

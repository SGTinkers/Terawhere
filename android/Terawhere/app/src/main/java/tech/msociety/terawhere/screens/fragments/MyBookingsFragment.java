package tech.msociety.terawhere.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import tech.msociety.terawhere.GetBookings;
import tech.msociety.terawhere.GetUser;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.TerawhereBackendServer;
import tech.msociety.terawhere.Token;
import tech.msociety.terawhere.adapters.BookingsAdapter;
import tech.msociety.terawhere.mocks.BackendMock;
import tech.msociety.terawhere.models.Booking;

import static android.app.Activity.RESULT_OK;

public class MyBookingsFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);

        return inflater.inflate(R.layout.fragment_my_bookings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initRecyclerView();

        makeNetworkCall();
        makeNetworkCall();
        //populateListFromDatabase();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                populateListFromDatabase();
            }
        }
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerViewMyBookings);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new BookingsAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void populateListFromDatabase() {
        List<Booking> bookings = BackendMock.getBookings();

        ((BookingsAdapter) adapter).setBookings(bookings);
        adapter.notifyDataSetChanged();
    }

    private void fetchOffersFromServer() {
        Call<GetBookings> callGetBookings = TerawhereBackendServer.getApiInstance(Token.getToken()).getAllBookings();
        callGetBookings.enqueue(new Callback<GetBookings>() {
            @Override
            public void onResponse(Call<GetBookings> call, Response<GetBookings> response2) {

                if (response2.isSuccessful()) {
                    GetBookings getBookings = response2.body();

                    List<Booking> bookings = getBookings.getBookings();

                    Log.i("response: ", getBookings.toString());


                } else {

                    try {
                        Log.i("ERROR_OFFER", ": " + response2.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onFailure(Call<GetBookings> call, Throwable t) {
                System.out.println(Arrays.toString(t.getStackTrace()));

            }
        });
    }
}

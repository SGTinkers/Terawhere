package tech.msociety.terawhere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.activities.CreateOfferActivity;
import tech.msociety.terawhere.adapters.OffersAdapter;
import tech.msociety.terawhere.models.Offer;

import static android.app.Activity.RESULT_OK;

public class MyOffersFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_offers, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initFab();
        initRecyclerView();
        populateListFromDatabase();
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
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fabAddRecord);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), CreateOfferActivity.class), REQUEST_CODE);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerViewMyOffers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new OffersAdapter();

        recyclerView.setAdapter(adapter);
    }

    private void populateListFromDatabase() {
        //List<Offer> offers = BackendMock.getOffers();


        final List<Offer> offersList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");

        query.addAscendingOrder("PickUpTime");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject offer : objects) {
                            Offer objOffer = new Offer(offer.getObjectId(), offer.getString("Name"), offer.getString("Destination"), offer.getInt("SeatsAvailable"), offer.getDate("PickUpTime"), offer.getString("Remarks"));
                            offersList.add(objOffer);
                        }
                        //Log.i("ListSize" , Integer.toString(offersList.size()));
                        ((OffersAdapter) adapter).setOffers(offersList);
                        adapter.notifyDataSetChanged();

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });


    }




}

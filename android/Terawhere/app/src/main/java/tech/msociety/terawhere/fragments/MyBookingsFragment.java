package tech.msociety.terawhere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tech.msociety.terawhere.R;
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
}

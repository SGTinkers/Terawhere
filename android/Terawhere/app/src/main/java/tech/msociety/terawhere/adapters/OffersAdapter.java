package tech.msociety.terawhere.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Offer;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    private List<Offer> offers;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Offer offer = offers.get(position);

        viewHolder.textViewOfferId.setText(offer.getNumberOfSeats() + " Seats Available ");
        viewHolder.textViewDestination.setText(offer.getDestination());

        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm a");

        if(offer.getTimestamp() != null) {
            viewHolder.textViewTimestamp.setText(ft.format(offer.getTimestamp()));
        }
        else {
            viewHolder.textViewTimestamp.setText("");

        }

    }

    @Override
    public int getItemCount() {
        return offers == null ? 0 : offers.size();    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewOfferId;
        private TextView textViewDestination;
        private TextView textViewTimestamp;

        private ViewHolder(View view) {
            super(view);
            textViewOfferId = (TextView) view.findViewById(R.id.textViewOfferId);
            textViewDestination = (TextView) view.findViewById(R.id.textViewDestination);
            textViewTimestamp = (TextView) view.findViewById(R.id.textViewTimestamp);
        }
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}

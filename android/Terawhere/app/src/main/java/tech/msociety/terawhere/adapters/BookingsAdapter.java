package tech.msociety.terawhere.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Booking;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {
    private List<Booking> bookings;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Booking booking = bookings.get(position);

        viewHolder.textViewBookingId.setText(booking.getId());
        viewHolder.textViewPassengerId.setText(booking.getPassengerId());
        viewHolder.textViewTimestamp.setText(booking.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewBookingId;
        private TextView textViewPassengerId;
        private TextView textViewTimestamp;

        private ViewHolder(View view) {
            super(view);
            textViewBookingId = (TextView) view.findViewById(R.id.textViewBookingId);
            textViewPassengerId = (TextView) view.findViewById(R.id.textViewPassengerId);
            textViewTimestamp = (TextView) view.findViewById(R.id.textViewTimestamp);
        }
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}

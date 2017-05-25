package tech.msociety.terawhere.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Booking;

/**
 * Created by musa on 24/5/17.
 */

public class BookingsInfoAdapter extends RecyclerView.Adapter<BookingsInfoAdapter.ViewHolder> {
    private Context context;
    List<Booking> bookings;
    ViewGroup viewGroup;
    
    public BookingsInfoAdapter(Context context) {
        this.context = context;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking, parent, false);
        viewGroup = parent;
        
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Booking booking = bookings.get(position);
        viewHolder.textViewPassengerName.setText(booking.getUserName());
        viewHolder.textViewSeatsBooked.setText(booking.getSeatsBooked());
        
    }
    
    @Override
    public int getItemCount() {
        return 0;
    }
    
    public void setBookingsInfo(List<Booking> bookings) {
        this.bookings = bookings;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSeatsBooked;
        private TextView textViewPassengerName;
        
        private ViewHolder(View view) {
            super(view);
            
            textViewPassengerName = (TextView) view.findViewById(R.id.text_view_passenger_name);
            textViewSeatsBooked = (TextView) view.findViewById(R.id.text_view_seats_booked);
            
        }
    }
    
}

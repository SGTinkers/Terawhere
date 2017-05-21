package tech.msociety.terawhere.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.utils.DateUtils;

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

        String meetUpTime = DateUtils.toFriendlyTimeString(booking.getMeetupTime());
        String day = DateUtils.toString(booking.getMeetupTime(), DateUtils.DAY_OF_MONTH_FORMAT);
        String month = DateUtils.toString(booking.getMeetupTime(), DateUtils.MONTH_ABBREVIATED_FORMAT);

        viewHolder.endingLocationTextView.setText("Destination: " + booking.getEndTerawhereLocation().getName());
        viewHolder.startingLocationTextView.setText("Meeting Point: " + booking.getStartTerawhereLocation().getName());
        viewHolder.bookingDayTextView.setText(day);
        viewHolder.bookingMonthTextView.setText(month);
        viewHolder.bookingMeetUpTimeTextView.setText("Pick Up Time: " + meetUpTime);

        viewHolder.seatsBookedTextView.setText("Seats Booked: " + booking.getSeatsRemaining());

    }

    @Override
    public int getItemCount() {
        return bookings == null ? 0 : bookings.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView endingLocationTextView;
        private TextView startingLocationTextView;
        private TextView seatsBookedTextView;
        private TextView bookingDayTextView;
        private TextView bookingMonthTextView;
        private TextView bookingMeetUpTimeTextView;


        private ViewHolder(View view) {
            super(view);
            endingLocationTextView = (TextView) view.findViewById(R.id.text_view_booking_end_location);
            startingLocationTextView = (TextView) view.findViewById(R.id.text_view_booking_start_location);
            seatsBookedTextView = (TextView) view.findViewById(R.id.text_view_booking_seats_booked);
            bookingDayTextView = (TextView) view.findViewById(R.id.text_view_booking_day);
            bookingMonthTextView = (TextView) view.findViewById(R.id.text_view_booking_month);
            bookingMeetUpTimeTextView = (TextView) view.findViewById(R.id.text_view_booking_meet_up_time);
        }
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}

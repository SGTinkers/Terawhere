package tech.msociety.terawhere.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Booking;

public class BookingsInfoAdapter extends RecyclerView.Adapter<BookingsInfoAdapter.ViewHolder> {

    private Context context;

    private List<Booking> bookings;

    public BookingsInfoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Booking booking = bookings.get(position);
        viewHolder.textViewPassengerName.setText(booking.getUserName());
        viewHolder.textViewSeatsBookedInfo.setText(Integer.toString(booking.getSeatsBooked()));
        Picasso.with(context)
                .load(booking.getUserDp())
                .transform(new CropCircleTransformation())
                .into(viewHolder.imageViewPassengerAvatar);
    }

    @Override
    public int getItemCount() {
        return bookings == null ? 0 : bookings.size();
    }

    public void setBookingsInfo(List<Booking> bookings) {
        this.bookings = bookings;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSeatsBookedInfo;
        private TextView textViewPassengerName;
        private ImageView imageViewPassengerAvatar;

        private ViewHolder(View view) {
            super(view);

            textViewPassengerName = (TextView) view.findViewById(R.id.text_view_passenger_name);
            textViewSeatsBookedInfo = (TextView) view.findViewById(R.id.text_view_seats_booked_info);
            imageViewPassengerAvatar = (ImageView) view.findViewById(R.id.image_view_passenger_avatar);
        }
    }
}

package tech.msociety.terawhere.adapters;


import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.utils.DateUtils;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {
    public static final String DELETE_BOOKING = "Delete Booking?";
    private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_YOUR_BOOKING = "Are you sure you want to delete your booking?";
    public static final String LOG_ERROR_DELETE_MESSAGE = "ERROR_DELETE_MESSAGE";
    private static final String CANCEL = "Cancel";
    private static final String CONFIRM = "Confirm";
    public static final String TERAWHERE_PRIMARY_COLOR = "#54d8bd";

    private List<Booking> bookings;
    private ViewGroup viewGroup;




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking, parent, false);
        viewGroup = parent;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Booking booking = bookings.get(position);

        String meetUpTime = DateUtils.toFriendlyTimeString(booking.getMeetupTime());
        String day = DateUtils.toString(booking.getMeetupTime(), DateUtils.DAY_OF_MONTH_FORMAT);
        String month = DateUtils.toString(booking.getMeetupTime(), DateUtils.MONTH_ABBREVIATED_FORMAT);

        viewHolder.endingLocationTextView.setText(setTextBold("Destination: ", booking.getEndTerawhereLocation().getName()));
        viewHolder.startingLocationTextView.setText(setTextBold("Meeting Point: ", booking.getStartTerawhereLocation().getName()));
        viewHolder.bookingDayTextView.setText(day);
        viewHolder.bookingMonthTextView.setText(month);
        viewHolder.bookingMeetUpTimeTextView.setText(setTextBold("Pick Up Time: ", meetUpTime));

        viewHolder.seatsBookedTextView.setText(setTextBold("Seats Booked: ", Integer.toString(booking.getSeatsBooked())));

        viewHolder.deleteBookingButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder deleteConfirmationDialog = new AlertDialog.Builder(viewGroup.getContext());

                deleteConfirmationDialog.setTitle(DELETE_BOOKING);
                deleteConfirmationDialog.setMessage(ARE_YOU_SURE_YOU_WANT_TO_DELETE_YOUR_BOOKING);
                deleteConfirmationDialog.setNegativeButton("Cancel", null); // dismisses by default
                deleteConfirmationDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Void> deleteRequest = TerawhereBackendServer.getApiInstance().deleteBooking(bookings.get(position).getBookingId());
                        deleteRequest.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    deleteBooking(position);

                                } else {
                                    try {
                                        Log.i(LOG_ERROR_DELETE_MESSAGE, ": " + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                            }
                        });
                    }
                });

                AlertDialog alert = deleteConfirmationDialog.create();
                alert.show();

                createAlertDialog(alert);
            }
        });

    }

    private void createAlertDialog(AlertDialog alert) {
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        nbutton.setText(CANCEL);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor(TERAWHERE_PRIMARY_COLOR));
        pbutton.setText(CONFIRM);
    }

    private Spanned setTextBold(String title, String text) {
        return Html.fromHtml(title + "<b>" + text + "</b>");
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
        private ImageButton deleteBookingButton;


        private ViewHolder(View view) {
            super(view);
            endingLocationTextView = (TextView) view.findViewById(R.id.text_view_booking_end_location);
            startingLocationTextView = (TextView) view.findViewById(R.id.text_view_booking_start_location);
            seatsBookedTextView = (TextView) view.findViewById(R.id.text_view_booking_seats_booked);
            bookingDayTextView = (TextView) view.findViewById(R.id.text_view_booking_day);
            bookingMonthTextView = (TextView) view.findViewById(R.id.text_view_booking_month);
            bookingMeetUpTimeTextView = (TextView) view.findViewById(R.id.text_view_booking_meet_up_time);

            deleteBookingButton = (ImageButton) view.findViewById(R.id.image_button_booking_delete);

        }
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    private void deleteBooking(int position) {
        bookings.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }
}

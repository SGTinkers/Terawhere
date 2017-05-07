package tech.msociety.terawhere.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.activities.CreateOfferActivity;
import tech.msociety.terawhere.models.Offer;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    private List<Offer> offers;
    ViewGroup viewGroup;

    String[] value = new String[]{
            "Edit", "Delete"
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_child, parent, false);
        viewGroup = parent;

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final Offer offer = offers.get(position);

        viewHolder.textViewSeatsAvailable.setText(offer.getNumberOfSeats() + " LEFT ");
        viewHolder.textViewDestination.setText(offer.getDestination());
        viewHolder.textViewRemarks.setText(offer.getRemarks());
        viewHolder.textViewVehicleColor.setText(offer.getVehicleColor());
        viewHolder.textViewVehiclePlateNumber.setText(offer.getVehiclePlateNumber());

        SimpleDateFormat ft = new SimpleDateFormat("hh:mm a");

        if (offer.getTimestamp() != null) {
            viewHolder.textViewTimestamp.setText(ft.format(offer.getTimestamp()));
        } else {
            viewHolder.textViewTimestamp.setText("");

        }

        final boolean[] shouldExpand = {viewHolder.expandedView.getVisibility() == View.GONE};
        ChangeBounds transition = new ChangeBounds();
        transition.setDuration(125);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldExpand[0]) {
                    viewHolder.expandedView.setVisibility(View.VISIBLE);
                    viewHolder.expandCollapse.setText("- LESS DETAILS");

                    shouldExpand[0] = false;
                } else {
                    viewHolder.expandedView.setVisibility(View.GONE);
                    viewHolder.expandCollapse.setText("+ MORE DETAILS");

                    shouldExpand[0] = true;
                }

                TransitionManager.beginDelayedTransition(viewGroup);
                viewHolder.itemView.setActivated(shouldExpand[0]);

            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                Log.i("LONG", "LONG: " + position);
                AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(viewGroup.getContext());


                alertdialogbuilder.setItems(value, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedText = Arrays.asList(value).get(which);


                        if (selectedText.equals("Delete")) {

                        }

                        if (selectedText.equals("Edit")) {
                            Intent intent = new Intent(new Intent(viewGroup.getContext(), CreateOfferActivity.class));
                            intent.putExtra("isEdit", true);
                            intent.putExtra("id", offer.getId());
                            intent.putExtra("destination", offer.getDestination());
                            intent.putExtra("seatsAvailable", offer.getNumberOfSeats());
                            intent.putExtra("pickUpTime", offer.getTimestamp());
                            intent.putExtra("remarks", offer.getRemarks());
                            intent.putExtra("vehicleColor", offer.getVehicleColor());
                            intent.putExtra("vehiclePlateNumber", offer.getVehiclePlateNumber());

                            //((Activity) viewGroup.getContext()).finish();
                            viewGroup.getContext().startActivity(intent);


                        }

                    }
                });

                AlertDialog dialog = alertdialogbuilder.create();

                dialog.show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return offers == null ? 0 : offers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSeatsAvailable;
        private TextView textViewDestination;
        private TextView textViewTimestamp;
        private TextView textViewRemarks;
        private TextView textViewVehicleColor;
        private TextView textViewVehiclePlateNumber;


        private LinearLayout expandedView;
        private TextView expandCollapse;

        private ViewHolder(View view) {
            super(view);
            textViewSeatsAvailable = (TextView) view.findViewById(R.id.textViewSeatsAvailable);
            textViewDestination = (TextView) view.findViewById(R.id.textViewDestination);
            textViewTimestamp = (TextView) view.findViewById(R.id.textViewTimestamp);
            textViewRemarks = (TextView) view.findViewById(R.id.remarks);
            textViewVehicleColor = (TextView) view.findViewById(R.id.vehicleColor);
            textViewVehiclePlateNumber = (TextView) view.findViewById(R.id.vehiclePlateNumber);
            expandedView = (LinearLayout) view.findViewById(R.id.expandView);
            expandCollapse = (TextView) view.findViewById(R.id.toggle);
        }
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}

package tech.msociety.terawhere;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import tech.msociety.terawhere.models.Offer;

public class GetOffers {
    @SerializedName("data")
    @Expose
    private List<OffersDatum> data = null;

    @Override
    public String toString() {
        return "GetOffers{" +
                "data=" + data +
                '}';
    }

    public GetOffers(OffersDatum offer) {
        data = new ArrayList<>();
        data.add(offer);

    }

    public List<Offer> getOffers() {
        List<Offer> offers = new ArrayList<>();

        for (OffersDatum datum : data) {
            Offer offer = new Offer(datum.getId(), datum.getUserId(), datum.getMeetupTime(), datum.getStartAddr(), datum.getStartName(), datum.getStartLat(), datum.getStartLng(), datum.getEndName(), datum.getEndAddr(), datum.getEndLat(), datum.getEndLng(), datum.getVacancy(), datum.getVehicleModel(), datum.getVehicleNumber(), datum.getRemarks(), datum.getPrefGender(), datum.getVehicleDesc());
            offers.add(offer);
        }

        return offers;
    }
}

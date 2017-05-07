package tech.msociety.terawhere;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import tech.msociety.terawhere.models.Offer;

public class GetOffers {
    @SerializedName("data")
    @Expose
    private final List<Datum> data = null;

    @Override
    public String toString() {
        return "GetOffers{" +
                "data=" + data +
                '}';
    }

    public List<Offer> getOffers() {
        List<Offer> offers = new ArrayList<>();

        for (Datum datum : data) {
            Offer offer = new Offer(String.valueOf(datum.getId()), datum.getEndName());
            offers.add(offer);
        }

        return offers;
    }
}

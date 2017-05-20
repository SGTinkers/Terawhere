package tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetOffersResponse {
    @SerializedName("data")
    @Expose
    public List<OfferDatum> data = null;
}

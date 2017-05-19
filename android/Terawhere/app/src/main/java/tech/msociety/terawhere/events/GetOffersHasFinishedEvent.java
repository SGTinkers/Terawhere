package tech.msociety.terawhere.events;

import java.util.List;

import tech.msociety.terawhere.models.OfferRevamp;

public class GetOffersHasFinishedEvent {
    private List<OfferRevamp> offers;
    
    public GetOffersHasFinishedEvent(List<OfferRevamp> offers) {
        this.offers = offers;
    }
    
    public List<OfferRevamp> getOffers() {
        return offers;
    }
}

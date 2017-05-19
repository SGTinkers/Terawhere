package tech.msociety.terawhere.events;

import java.util.List;

import tech.msociety.terawhere.models.Offer;

public class GetOffersHasFinishedEvent {
    private List<Offer> offers;
    
    public GetOffersHasFinishedEvent(List<Offer> offers) {
        this.offers = offers;
    }
    
    public List<Offer> getOffers() {
        return offers;
    }
}

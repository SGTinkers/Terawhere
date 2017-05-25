package tech.msociety.terawhere.events;

import tech.msociety.terawhere.models.Offer;

public class OfferDeletedEvent {

    private Offer offer;

    public OfferDeletedEvent(Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }
}

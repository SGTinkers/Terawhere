package tech.msociety.terawhere.mocks;

import java.util.ArrayList;
import java.util.List;

import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.utils.faker.Faker;

public class BackendMock {
    private static Faker faker = new Faker();

    public static List<Offer> getOffers() {
        List<Offer> offers = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String offerId = String.valueOf(faker.number(3));
            String driverId = String.valueOf(faker.number(5));
            String destination = faker.words(2);
            Integer numberOfSeats = (int) faker.number(1);
            String timestamp = faker.words(2);
            String remarks = faker.words(2);

            Offer offer = new Offer(offerId, driverId, destination, numberOfSeats, timestamp, remarks);
            offers.add(offer);
        }

        return offers;
    }
}

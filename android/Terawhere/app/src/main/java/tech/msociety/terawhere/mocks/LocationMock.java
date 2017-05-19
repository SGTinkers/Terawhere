package tech.msociety.terawhere.mocks;

import tech.msociety.terawhere.models.Location;
import tech.msociety.terawhere.utils.faker.Faker;

public class LocationMock {
    private static Faker faker = new Faker();
    
    public static Location getRandomLocation() {
        return new Location(faker.words(1), faker.words(2), (double) faker.number(2), (double) faker.number(2), faker.words(3));
    }
}

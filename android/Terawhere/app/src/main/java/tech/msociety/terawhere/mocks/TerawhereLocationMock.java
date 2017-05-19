package tech.msociety.terawhere.mocks;

import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.utils.faker.Faker;

public class TerawhereLocationMock {
    private static Faker faker = new Faker();
    
    public static TerawhereLocation getRandomLocation() {
        return new TerawhereLocation(faker.words(1), faker.words(2), (double) faker.number(2), (double) faker.number(2), faker.words(3));
    }
}

package tech.msociety.terawhere.mocks;

import java.util.Date;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.utils.faker.Faker;

public class BackendTimestampMock {
    private static Faker faker = new Faker();
    
    public static BackendTimestamp getRandomBackendTimestamp() {
        return new BackendTimestamp(faker.dateBeforeToday(), new Date(), null);
    }
}

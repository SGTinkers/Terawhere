package tech.msociety.terawhere.globals;

import tech.msociety.terawhere.TerawhereApplication;

public class Constants {
    //    public static final String BASE_URL = "https://api.terawhere.com";  // prod
    public static final String BASE_URL = "http://139.59.224.66";   // dev

    private static String bearerToken;

    public static String getBearerToken() {
        if (bearerToken == null) {
            bearerToken = AppPrefs.with(TerawhereApplication.ApplicationContext).getBearerToken();
        }

        return bearerToken;
    }

    public static void setBearerToken(String token) {
        AppPrefs.with(TerawhereApplication.ApplicationContext).setBearerToken(token);
        bearerToken = token;
    }
}

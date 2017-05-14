package tech.msociety.terawhere.globals;

import tech.msociety.terawhere.TerawhereApplication;

public class Constants {
    public static final String BASE_URL = "https://api.terawhere.com";

    public static String GetBearerToken() {
        return AppPrefs.with(TerawhereApplication.ApplicationContext).getBearerToken();
    }

    public static void SetBearerToken(String token) {
        AppPrefs.with(TerawhereApplication.ApplicationContext).setBearerToken(token);
    }
}

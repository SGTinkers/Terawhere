package tech.msociety.terawhere.models;

public class Token {
    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String value) {
        token = value;
    }
}

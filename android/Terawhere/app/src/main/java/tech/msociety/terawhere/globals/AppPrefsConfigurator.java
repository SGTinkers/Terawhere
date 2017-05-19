package tech.msociety.terawhere.globals;

import ds.gendalf.PrefsConfig;

@PrefsConfig("AppPrefs")
public interface AppPrefsConfigurator {
    String bearerToken = null;
    String userId = null;
    String userName = null;
    String userEmail = null;
}

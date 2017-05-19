package tech.msociety.terawhere;

import android.os.Build;

public class AndroidSdkChecker {
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}

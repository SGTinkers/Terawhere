package tech.msociety.terawhere.utils;

import android.os.Build;

public class AndroidSdkCheckerUtils {
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}

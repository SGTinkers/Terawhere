package tech.msociety.terawhere;

import android.app.Application;
import android.content.Context;

public class TerawhereApplication extends Application {

    public static Context ApplicationContext;

    public TerawhereApplication() {
        super();
        ApplicationContext = this;
    }

}

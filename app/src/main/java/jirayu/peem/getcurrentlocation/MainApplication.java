package jirayu.peem.getcurrentlocation;

import android.app.Application;

/**
 * Created by Dev on 9/5/2016 AD.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Init Contextor
        Contextor.getInstance().init(getApplicationContext());
    }
}

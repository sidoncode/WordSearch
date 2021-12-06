package word.search;

import android.app.Activity;
import android.content.Context;

import word.search.platform.AppEvents;

public class AppEventsAndroid implements AppEvents {

    private Activity activity;


    public AppEventsAndroid(Activity activity){
        this.activity = activity;
    }


    @Override
    public void exitApp() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.onBackPressed();
            }
        });
    }




    @Override
    public void toggleFullScreen() {}
}

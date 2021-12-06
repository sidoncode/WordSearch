package word.search;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import word.search.platform.analytics.Analytics;

public class FirebaseAnalyticsAndroid implements Analytics {


    private FirebaseAnalytics firebaseAnalytics;

    public FirebaseAnalyticsAndroid(FirebaseAnalytics firebaseAnalytics){
        this.firebaseAnalytics = firebaseAnalytics;
    }


    @Override
    public void logEvent(String eventName, String name, String value) {
        if(firebaseAnalytics != null) {
            Bundle params = new Bundle();
            params.putString(name, value);
            firebaseAnalytics.logEvent(eventName, params);
        }
    }


    @Override
    public void logEvent(String eventName, String name1, String value1, String name2, String value2) {
        if(firebaseAnalytics != null) {
            Bundle params = new Bundle();
            params.putString(name1, value1);
            params.putString(name2, value2);
            firebaseAnalytics.logEvent(eventName, params);
        }
    }


    @Override
    public void logEvent(String eventName, String name, int value) {
        if(firebaseAnalytics != null) {
            Bundle params = new Bundle();
            params.putInt(name, value);
            firebaseAnalytics.logEvent(eventName, params);
        }
    }


    @Override
    public void logEarnedCoinEvent(int value) {
        if(firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
            bundle.putString(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME, "XAU");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.EARN_VIRTUAL_CURRENCY, bundle);
        }
    }


    @Override
    public void logSpendCoinEvent(String itemName, int value) {
        if(firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, value);
            bundle.putString(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME, "XAU");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SPEND_VIRTUAL_CURRENCY, bundle);
        }
    }


    @Override
    public void logLevelEndEvent(int levelId) {
        if(firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.LEVEL, levelId + "");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_END, bundle);
        }
    }


    @Override
    public void logLevelStartEvent(int levelId) {
        if(firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.LEVEL, levelId + "");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle);
        }
    }


    @Override
    public void logMileStone(int level) {
        if(firebaseAnalytics != null){
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID, level + "");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);
        }
    }



    @Override
    public void logScreenChangedViewEvent(String name) {
        if(firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, name);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        }
    }



    @Override
    public void logShare() {
        if(firebaseAnalytics != null){
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "none");
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "item");
            bundle.putString(FirebaseAnalytics.Param.METHOD, "method");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
        }
    }



    @Override
    public void logTutorialBegin() {
        if(firebaseAnalytics != null) firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null);
    }



    @Override
    public void logTutorialComplete() {
        if(firebaseAnalytics != null) firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null);
    }



    @Override
    public void setUserProperty(String name, String value) {
        if(firebaseAnalytics != null) firebaseAnalytics.setUserProperty(name, value);
    }



    @Override
    public void resetAnalyticsData() {
        if(firebaseAnalytics != null) firebaseAnalytics.resetAnalyticsData();
    }



    @Override
    public void setAnalyticsCollectionEnabled(boolean enabled) {
        if(firebaseAnalytics != null) firebaseAnalytics.setAnalyticsCollectionEnabled(enabled);
    }
}

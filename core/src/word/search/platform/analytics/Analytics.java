package word.search.platform.analytics;

public interface Analytics {

    void logEvent(String eventName, String name, String value);
    void logEvent(String eventName, String name1, String value1, String name2, String value2);
    void logEvent(String eventName, String name, int value);

    void logEarnedCoinEvent(int value);
    void logSpendCoinEvent(String itemName, int value);
    void logLevelEndEvent(int levelId);
    void logLevelStartEvent(int levelId);
    void logMileStone(int level);
    void logScreenChangedViewEvent(String name);
    void logShare();
    void logTutorialBegin();
    void logTutorialComplete();


    void setUserProperty(String name, String value);
    void resetAnalyticsData();
    void setAnalyticsCollectionEnabled(boolean enabled);
}

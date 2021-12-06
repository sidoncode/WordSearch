package word.search.managers;


import word.search.platform.ads.RewardedVideoCloseCallback;

public interface AdManager {


    boolean isInterstitialAdEnabled();
    boolean isInterstitialAdLoaded();
    void showInterstitialAd(Runnable interstitialAdClosedCallback);

    boolean isRewardedAdEnabled();
    boolean isRewardedAdLoaded();
    boolean allowOnlyOneRewardedInaLevel();
    void setRewardedVideoStartedCallback(Runnable callback);
    void showRewardedAd(RewardedVideoCloseCallback finishedCallback);


    void openGDPRForm();
    boolean isUserInEU();


}

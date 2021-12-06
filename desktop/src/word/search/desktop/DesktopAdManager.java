package word.search.desktop;

import word.search.managers.AdManager;
import word.search.platform.ads.RewardedVideoCloseCallback;

public class DesktopAdManager implements AdManager {
    @Override
    public boolean isInterstitialAdEnabled() {
        return false;
    }

    @Override
    public boolean isRewardedAdLoaded() {
        return false;
    }

    @Override
    public boolean allowOnlyOneRewardedInaLevel() {
        return false;
    }

    @Override
    public void setRewardedVideoStartedCallback(Runnable callback) {

    }

    @Override
    public boolean isInterstitialAdLoaded() {
        return false;
    }

    @Override
    public void showInterstitialAd(Runnable interstitialAdClosedCallback) {

    }

    @Override
    public boolean isRewardedAdEnabled() {
        return false;
    }

    @Override
    public void showRewardedAd(RewardedVideoCloseCallback finishedCallback) {

    }

    @Override
    public void openGDPRForm() {

    }

    @Override
    public boolean isUserInEU() {
        return false;
    }
}

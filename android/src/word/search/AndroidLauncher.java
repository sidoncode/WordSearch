package word.search;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.violetapple.durbeen.R;

import java.util.HashMap;
import java.util.Map;

import word.search.config.GameConfig;
import word.search.platform.AppEvents;
import word.search.platform.LinkOpener;
import word.search.platform.RateUs;
import word.search.platform.dict.WordMeaningProvider;


public class AndroidLauncher extends AdActivity implements LinkOpener, RateUs {


    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (GameConfig.ENABLE_ANALYTICS) mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;

        Map<String, WordMeaningProvider> provider = new HashMap<>();
        if (GameConfig.ENABLE_ENGLISH_LANGUAGE_DICTIONARY)
            provider.put("en", new WordMeaningProviderAndroid());

        WordGame wordGame = new WordGame(new NetworkAndroid(this), provider);
        WordGame.analytics = new FirebaseAnalyticsAndroid(mFirebaseAnalytics);
        wordGame.appEvents = new AppEventsAndroid(this);
        wordGame.adManager = this;
        wordGame.shoppingProcessor = this;
        wordGame.appShare = new AppShareAndroid(this);
        wordGame.rateUsLauncher = this;
        wordGame.supportRequest = new SupportRequestAndroid(this);
        wordGame.privacyUrl = getString(R.string.privacy_policy_url);
        wordGame.tosUrl = getString(R.string.terms_of_use_url);
        wordGame.linkOpener = this;
        wordGame.menuConfig = new MenuConfigAndroid(this);
        initialize(wordGame, config);
    }


    @Override
    public void openLink(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    @Override
    public void launch() {
        String param = getPackageName();
        Uri uri = Uri.parse("market://details?id=" + param);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + param)));
        }
    }


}

package word.search;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;


import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.managers.AdManager;
import word.search.managers.ConnectionManager;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.AppEvents;
import word.search.platform.AppShare;
import word.search.platform.LinkOpener;
import word.search.platform.MenuConfig;
import word.search.platform.Network;


import word.search.platform.PreloaderInterface;
import word.search.platform.RateUs;
import word.search.platform.SupportRequest;
import word.search.platform.analytics.Analytics;
import word.search.platform.dict.WordMeaningProvider;
import word.search.platform.iap.ShoppingProcessor;
import word.search.screens.BaseScreen;
import word.search.screens.IntroScreen;
import word.search.screens.SplashScreen;
import java.util.Map;

public class WordGame extends Game {

    public ResourceManager resourceManager = new ResourceManager();
    public ShoppingProcessor shoppingProcessor;
    private BaseScreen currentScreen;
    public AdManager adManager;
    public AppEvents appEvents;
    private Music music;
    public LinkOpener linkOpener;
    public String privacyUrl;
    public String tosUrl;
    public static Analytics analytics;
    public RateUs rateUsLauncher;
    public AppShare appShare;
    public SupportRequest supportRequest;
    public MenuConfig menuConfig;
    public PreloaderInterface preloaderInterface;


    public WordGame(Network network, Map<String, WordMeaningProvider> providerMap) {
        ConnectionManager.network = network;
        Language.wordMeaningProviderMap = providerMap;
    }



    @Override
    public void create() {
        setScreen(new SplashScreen(this));
    }



    @Override
    public void dispose() {
        super.dispose();
        if(currentScreen != null) currentScreen.dispose();
        if(music != null) music.dispose();
        resourceManager.clear();
        resourceManager.dispose();
    }



    @Override
    public void setScreen(Screen screen){
        if(currentScreen != null)
            currentScreen.dispose();

        currentScreen = (BaseScreen)screen;
        super.setScreen(screen);
        if(screen instanceof IntroScreen && music == null) startMusic();
    }




    private void startMusic(){
        FileHandle fileHandle = Gdx.files.internal(GameConfig.PATH_TO_LOOP_MUSIC_FILE);
        if(fileHandle.exists()) {
            music = Gdx.audio.newMusic(fileHandle);
            music.setLooping(true);
            ConfigProcessor.enableBackgroundMusic = true;

            if(!DataManager.get(Constants.KEY_MUSIC_MUTED, false)) music.play();
        }
    }




    public void playMusic(boolean muted){
        if(muted) music.stop();
        else music.play();
    }
    

}

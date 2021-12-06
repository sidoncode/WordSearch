package word.search.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.I18NBundle;

import word.search.WordGame;
import word.search.config.ColorConfig;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.ui.dialogs.PrivacyDialog;
import word.search.ui.hud.splash_hud.SplashHud;

import static com.badlogic.gdx.Application.LOG_INFO;


public class SplashScreen extends BaseScreen{

    private boolean loading;
    private SplashHud splashHud;
    private PrivacyDialog privacyDialog;



    public SplashScreen(WordGame modernWordSearchGame) {
        super(modernWordSearchGame);
    }



    @Override
    public void show() {
        super.show();

        Gdx.app.setLogLevel(LOG_INFO);

        if(GameConfig.ENABLE_LOGGING_SCREEN_VIEW_EVENT)
            WordGame.analytics.logScreenChangedViewEvent("splash_screen");

        ResourceManager.introBackground = UIConfig.INTRO_SCREEN_BACKGROUND_IMAGE;
        if(ResourceManager.introBackground != null){
            wordGame.resourceManager.load(ResourceManager.introBackground, Texture.class);
            wordGame.resourceManager.finishLoading();
            setBackground(ColorConfig.INTRO_SCREEN_BACKGROUND_COLOR, ResourceManager.introBackground);
        }

        splashHud = new SplashHud(this);
        loadAssets();
    }





    private void loadAssets(){
        I18NBundle.setSimpleFormatter(true);
        String localeCode = Language.getSelectedLocaleCode();
        if(localeCode != null) setNewLanguage(localeCode);

        wordGame.resourceManager.load(ResourceManager.ATLAS_1, TextureAtlas.class);
        wordGame.resourceManager.load(ResourceManager.ATLAS_2, TextureAtlas.class);

        BitmapFontLoader.BitmapFontParameter params = new BitmapFontLoader.BitmapFontParameter();
        params.genMipMaps = true;
        params.minFilter = Texture.TextureFilter.Nearest;
        params.magFilter = Texture.TextureFilter.Linear;

        wordGame.resourceManager.load(ResourceManager.fontSignikaBoldBoard, BitmapFont.class, params);
        wordGame.resourceManager.load(ResourceManager.fontSignikaBoldShadow, BitmapFont.class, params);
        wordGame.resourceManager.load(ResourceManager.fontSignikaBold, BitmapFont.class, params);

        wordGame.resourceManager.load(ResourceManager.level_start, Sound.class);
        wordGame.resourceManager.load(ResourceManager.letter_select_01, Sound.class);
        wordGame.resourceManager.load(ResourceManager.letter_select_02, Sound.class);
        wordGame.resourceManager.load(ResourceManager.letter_select_03, Sound.class);
        wordGame.resourceManager.load(ResourceManager.letter_select_04, Sound.class);
        wordGame.resourceManager.load(ResourceManager.letter_select_05, Sound.class);
        wordGame.resourceManager.load(ResourceManager.letter_select_06, Sound.class);
        wordGame.resourceManager.load(ResourceManager.letter_select_07, Sound.class);
        wordGame.resourceManager.load(ResourceManager.letter_select_08, Sound.class);
        wordGame.resourceManager.load(ResourceManager.fail, Sound.class);
        wordGame.resourceManager.load(ResourceManager.success, Sound.class);
        wordGame.resourceManager.load(ResourceManager.level_complete, Sound.class);
        wordGame.resourceManager.load(ResourceManager.word_reveal, Sound.class);
        wordGame.resourceManager.load(ResourceManager.board_reveal, Sound.class);
        wordGame.resourceManager.load(ResourceManager.magic, Sound.class);
        wordGame.resourceManager.load(ResourceManager.rotate, Sound.class);
        wordGame.resourceManager.load(ResourceManager.bonus_word_dup, Sound.class);
        wordGame.resourceManager.load(ResourceManager.daily_reward, Sound.class);
        wordGame.resourceManager.load(ResourceManager.box_touch, Sound.class);
        wordGame.resourceManager.load(ResourceManager.purchased_iap, Sound.class);
        wordGame.resourceManager.load(ResourceManager.bonus_word, Sound.class);
        wordGame.resourceManager.load(ResourceManager.milestone_pop, Sound.class);
        wordGame.resourceManager.load(ResourceManager.coin_add, Sound.class);
        wordGame.resourceManager.load(ResourceManager.daily_pick, Sound.class);
        wordGame.resourceManager.load(ResourceManager.duble, Sound.class);
        wordGame.resourceManager.load(ResourceManager.daily_item_hit, Sound.class);
        wordGame.resourceManager.load(ResourceManager.word_found_before, Sound.class);

        loading = true;
    }



    private void update() {
        wordGame.resourceManager.update();
        float progress = wordGame.resourceManager.getProgress();
        splashHud.pbar.setPercent(progress);

        if(progress == 1f){
            loading = false;
            stage.addAction(Actions.sequence(
                    Actions.delay(0.01f),
                    Actions.run(loaded)
            ));
        }
    }



    private Runnable loaded = new Runnable() {
        @Override
        public void run() {
            splashHud.pbar.setVisible(false);
            NinePatches.init(wordGame.resourceManager);
            AtlasRegions.init(wordGame.resourceManager);
            if(wordGame.shoppingProcessor != null) wordGame.shoppingProcessor.init();
            ConfigProcessor.mutedSfx = DataManager.get(Constants.KEY_SFX_MUTED, false);

            if(!DataManager.get(Constants.KEY_PRIVACY_ACCEPTED, false)) checkPrivacy();
            else checkLanguage();
        }
    };




    private void checkPrivacy(){
        if(wordGame.menuConfig.showPrivacyDialogOnFirstRun()) {
            privacyDialog = new PrivacyDialog(this, privacyDialogRemover, true);
            stage.addActor(privacyDialog);
            privacyDialog.show();
        }else{
            checkLanguage();
        }
    }



    private Runnable privacyDialogRemover = new Runnable() {
        @Override
        public void run() {
            privacyDialog = null;
            checkLanguage();
        }
    };




    private void checkLanguage(){
        if(GameConfig.availableLanguages == null || (GameConfig.availableLanguages.size() == 0)){
            Gdx.app.log("game.log", "No language has been configured in GameConfig.");
            return;
        }

        String localeCode = Language.getSelectedLocaleCode();
        if(localeCode == null){
            if(GameConfig.availableLanguages.size() > 1) {
                splashHud.showLanguageDialog(languageSelectionComplete);
            }else{
                for(String code : GameConfig.availableLanguages.keySet()) {
                    setNewLanguage(code);
                    languageSelectionComplete.run();
                }
            }
        }else{

            Language.bundle = wordGame.resourceManager.get(ResourceManager.LOCALE_PROPERTIES_FILE, I18NBundle.class);
            stage.addAction(Actions.sequence(
                    Actions.delay(0.1f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            wordGame.setScreen(new IntroScreen(wordGame));
                        }
                    })
            ));
        }
    }




    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if(splashHud != null) splashHud.resize();
    }




    @Override
    public void render(float delta) {
        super.render(delta);

        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();

        if(loading){
            update();
        }
    }




    @Override
    public void dispose() {
        super.dispose();
        splashHud.progressbar.dispose();
        splashHud.progressbar_track.dispose();
    }



}

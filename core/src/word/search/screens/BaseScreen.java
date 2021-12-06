package word.search.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Stack;

import word.search.WordGame;
import word.search.app;
import word.search.config.UIConfig;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.ui.dialogs.BackNavigator;

import word.search.ui.hud.Hud;
import word.search.ui.tutorial.Tutorial;
import word.search.util.Text;
import word.search.util.TextLoader;

public class BaseScreen extends ScreenAdapter {


    public WordGame wordGame;

    private Viewport viewport;
    private OrthographicCamera camera;
    public Stage stage;
    public Stack<BackNavigator> backNavQueue = new Stack<>();
    protected float r, g, b;
    public Texture backgroundTexture;
    public Hud hud;
    public Tutorial tutorial;
    private String lastBgTexture;
    private SpriteBatch batch = new SpriteBatch();
    private float bgX, bgY, bgWidth, bgHeight;


    public BaseScreen(WordGame modernWordSearchGame){
        this.wordGame = modernWordSearchGame;

        camera = new OrthographicCamera(Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        viewport = new ExtendViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera);
        viewport.apply();
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        InputProcessor backProcessor = new InputAdapter() {

            @Override
            public boolean keyUp(int keycode) {
                if ((keycode == Input.Keys.BACK) || (keycode == Input.Keys.BACKSPACE)){
                    onBackPress();
                }
                return false;
            }
        };

        InputMultiplexer multiplexer = new InputMultiplexer(stage, backProcessor);
        Gdx.input.setInputProcessor(multiplexer);

        if(wordGame.adManager != null) wordGame.adManager.setRewardedVideoStartedCallback(null);
    }




    public void modalOpened(){}


    public void modalClosed(){}



    public void setNewLanguage(String code){
        Language.setLocale(code, wordGame);

        ResourceManager.LOCALE_PROPERTIES_FILE = "data/" + code + "/strings";
        wordGame.resourceManager.load(ResourceManager.LOCALE_PROPERTIES_FILE, I18NBundle.class);
        wordGame.resourceManager.setLoader(Text.class, new TextLoader(new InternalFileHandleResolver()));
        wordGame.resourceManager.load( "data/" + Language.locale.code + "/words.txt", Text.class, new TextLoader.TextParameter());
        wordGame.resourceManager.finishLoading();
        Language.bundle = wordGame.resourceManager.get(ResourceManager.LOCALE_PROPERTIES_FILE, I18NBundle.class);
    }




    public Runnable languageSelectionComplete = new Runnable() {
        @Override
        public void run() {
            wordGame.setScreen(new IntroScreen(wordGame));
        }
    };


    public void unloadBackgrounds(){
        for(int i = 0; i < UIConfig.GAME_BACKGROUND_COUNT; i++){
            String path = "bg/game_" + i + ".jpg";
            if(wordGame.resourceManager.contains(path)){
                wordGame.resourceManager.unload(path);
            }
        }
    }


    protected void setBackground(Color bgColor, final String path){
        r = bgColor.r;
        g = bgColor.g;
        b = bgColor.b;

        if(path != null){
            //don't load the same image again
            if(lastBgTexture != null && lastBgTexture.equals(path)) return;
            if(backgroundTexture != null) backgroundTexture.dispose();

            if(!path.equals(UIConfig.INTRO_SCREEN_BACKGROUND_IMAGE) && wordGame.resourceManager.contains(path)) {
                wordGame.resourceManager.unload(path);
            }

            if(wordGame.resourceManager.contains(path)){
                backgroundTexture = wordGame.resourceManager.get(path, Texture.class);
            }else{
                wordGame.resourceManager.load(path, Texture.class);
                wordGame.resourceManager.finishLoading();
                backgroundTexture = wordGame.resourceManager.get(path, Texture.class);
            }

            backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            calculateBgScale(backgroundTexture.getWidth(), backgroundTexture.getHeight(), stage.getWidth(), stage.getHeight());
            calculateBgOffset();
            lastBgTexture = path;
        }
    }




    private void calculateBgScale(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
        float targetRatio = targetHeight / targetWidth;
        float sourceRatio = sourceHeight / sourceWidth;
        float scale = targetRatio < sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
        bgWidth = sourceWidth * scale;
        bgHeight = sourceHeight * scale;
    }




    private void calculateBgOffset(){
        if(bgWidth < stage.getWidth()) bgX = (stage.getWidth() - bgWidth) * 0.5f;
        else bgX = - (bgWidth - stage.getWidth()) * 0.5f;

        if(bgHeight < stage.getHeight()) bgY = (stage.getHeight() - bgHeight) * 0.5f;
        else bgY = - (bgHeight - stage.getHeight()) * 0.5f;
    }




    public Runnable tutorialRemover = new Runnable() {
        @Override
        public void run() {
            clearTutorial();
        }
    };



    protected void clearTutorial(){
        tutorial.remove();
        tutorial = null;
    }


    protected boolean onBackPress(){
        if(!stage.getRoot().isTouchable()) return false;
        if (!backNavQueue.empty()){
            BackNavigator backNavigator = backNavQueue.peek();

            if(backNavigator != null) {
                Actor actor = (Actor)backNavigator;
                if(actor.getStage() != null) {
                    return backNavigator.navigateBack();
                }
            }
            return false;
        }else{
            return false;
        }
    }



    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.update();

        stage.getViewport().update(width, height, true);

        if(backgroundTexture != null) {
            calculateBgScale(backgroundTexture.getWidth(), backgroundTexture.getHeight(), stage.getWidth(), stage.getHeight());
            calculateBgOffset();
        }

    }




    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(r, g, b, 1.0f);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        if(batch != null && backgroundTexture != null){
            batch.setProjectionMatrix(camera.combined);
            batch.setColor(1,1,1, stage.getRoot().getColor().a);
            batch.begin();
            batch.draw(backgroundTexture, bgX, bgY, bgWidth, bgHeight);
            batch.end();
        }
    }




    @Override
    public void dispose() {
        super.dispose();
        if(batch != null) batch.dispose();
    }


}

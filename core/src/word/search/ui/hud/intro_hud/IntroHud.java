package word.search.ui.hud.intro_hud;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import word.search.app;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.screens.GameScreen;
import word.search.screens.IntroScreen;
import word.search.ui.game.buttons.DarkeningTextButton;

import word.search.ui.game.particle.SparkleParticle;
import word.search.ui.hud.Hud;
import word.search.ui.util.UiUtil;

public class IntroHud extends Hud {


    private IntroScreen introScreen;
    public DarkeningTextButton btnPlay;
    private SpriteBatch batch;
    private Array<SparkleParticle> sparkles = new Array<>();
    public Image logo;
    private float btnTargetY;
    private Label gameEndLabel;

    public IntroHud(IntroScreen introScreen){
        super(introScreen);
        this.introScreen = introScreen;
        setUI();
    }



    @Override
    protected void setUI() {
        btnTargetY = baseScreen.stage.getHeight() * 0.28f;
        setSettingsButton();
        btnSettings.addListener(onButtonClick);

        setCoinView();

        TextureAtlas atlas = baseScreen.wordGame.resourceManager.get(ResourceManager.ATLAS_2, TextureAtlas.class);

        logo = new Image(atlas.findRegion("logo_" + Language.locale.code));

        logo.setY(baseScreen.stage.getHeight() * 0.68f);
        logo.getColor().a = 0;
        introScreen.stage.addActor(logo);

        TextButton.TextButtonStyle playStyle = new TextButton.TextButtonStyle();
        playStyle.font = introScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        playStyle.fontColor = Color.WHITE;
        playStyle.up = new TextureRegionDrawable(AtlasRegions.play);
        playStyle.down = playStyle.up;

        int nextLevel = 0;
        if(GameConfig.LEVEL_INDEX_TO_DEBUG == -1) nextLevel = DataManager.get(DataManager.getLocaleAwareKey(Constants.NEXT_LEVEL_INDEX), 0) + 1;
        else nextLevel = GameConfig.LEVEL_INDEX_TO_DEBUG + 1;

        btnPlay = new DarkeningTextButton(String.valueOf(nextLevel), playStyle);
        btnPlay.getLabelCell().padRight(15f);
        btnPlay.setOrigin(Align.center);
        btnPlay.setY(baseScreen.stage.getHeight() * 0.17f);
        btnPlay.getColor().a = 0;
        btnPlay.setTransform(true);
        introScreen.stage.getRoot().addActor(btnPlay);
        btnPlay.addListener(onButtonClick);

        if(nextLevel == Language.locale.levelCount + 1){
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = introScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);

            gameEndLabel = new Label(Language.get("game_end"), labelStyle);
            gameEndLabel.setAlignment(Align.bottom);
            float maxWidth = introScreen.stage.getWidth() * 0.9f;
            if(gameEndLabel.getPrefWidth() > maxWidth) gameEndLabel.setFontScale(maxWidth / gameEndLabel.getPrefWidth());

            gameEndLabel.getColor().a = 0;
            introScreen.stage.addActor(gameEndLabel);
        }
    }




    @Override
    public void resize() {
        super.resize();

        btnTargetY = baseScreen.stage.getHeight() * 0.28f;

        btnSettings.setX(marginH);

        logo.setX((introScreen.stage.getWidth() - logo.getWidth()) * 0.5f);

        btnPlay.setX((introScreen.stage.getWidth() - btnPlay.getWidth()) * 0.5f);

        if(gameEndLabel != null) {
            gameEndLabel.setX((introScreen.stage.getWidth() - gameEndLabel.getPrefWidth()) * 0.5f);
            gameEndLabel.setY(btnTargetY + btnPlay.getHeight() - 5);
        }
    }




    public void animateIn(Runnable callback){
        float time = 0.4f;
        logo.addAction(Actions.parallel(
                Actions.fadeIn(time, Interpolation.fastSlow),
                Actions.moveTo((introScreen.stage.getWidth() - logo.getWidth()) * 0.5f, baseScreen.stage.getHeight() * 0.62f, time, Interpolation.fastSlow)
        ));

        float alpha = 1f;

        int nextLevel = DataManager.get(DataManager.getLocaleAwareKey(Constants.NEXT_LEVEL_INDEX), 0);
        if(nextLevel == Language.locale.levelCount){
            btnPlay.setTouchable(Touchable.disabled);
           alpha = 0.7f;
           if(gameEndLabel != null) gameEndLabel.addAction(Actions.fadeIn(time));
        }

        btnPlay.addAction(
                Actions.sequence(
                    Actions.parallel(
                            Actions.alpha(alpha, time, Interpolation.fastSlow),
                            Actions.moveTo((introScreen.stage.getWidth() - btnPlay.getWidth()) * 0.5f, btnTargetY, time, Interpolation.fastSlow)
                    ),
                    Actions.run(callback)
                )
        );
    }





    private Runnable animateOutEnd = new Runnable() {

        @Override
        public void run() {

            baseScreen.unloadBackgrounds();

            introScreen.wordGame.setScreen(new GameScreen(introScreen.wordGame));
        }

    };



    private ChangeListener onButtonClick = new ChangeListener() {

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if(actor == btnSettings) {
                openSettings();
            }else if(actor == btnPlay){
                if(introScreen.tutorial != null) {
                    DataManager.set(Constants.KEY_TUTORIAL_STEP, Constants.TUTORIAL_INTRO_PLAY_BUTTON);
                }
                screenFadeOut(animateOutEnd);
            }
        }

    };




    public void animatePlayButton(){
        if(!ConfigProcessor.mutedSfx) {
            Sound sound = baseScreen.wordGame.resourceManager.get(ResourceManager.daily_item_hit, Sound.class);
            sound.play();
        }
        btnPlay.addAction(Actions.sequence(
                Actions.scaleTo(1.1f, 1.1f, 0.1f, Interpolation.fastSlow),
                Actions.scaleTo(1, 1, 0.07f, Interpolation.slowFast),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Object reward = baseScreen.stage.getRoot().getUserObject();
                        if(reward != null){
                            Actor actor = (Actor)reward;
                            actor.remove();
                            actor = null;
                        }
                        baseScreen.stage.getRoot().setTouchable(Touchable.enabled);
                        UiUtil.pulsate(btnPlay);
                    }
                })
        ));

        batch = new SpriteBatch();
        batch.setProjectionMatrix(introScreen.stage.getCamera().combined);

        float radius = btnPlay.getWidth() * 0.5f;

        for(int i = 0; i < 20; i++){
            SparkleParticle p = Pools.obtain(SparkleParticle.class);
            float angle = MathUtils.random() * MathUtils.PI2;

            p.x = btnPlay.getX() + btnPlay.getWidth() * 0.5f - p.getWidth() * 0.5f + radius * MathUtils.cos(angle);
            p.y = btnPlay.getY() + btnPlay.getHeight() * 0.5f - p.getHeight() * 0.5f + radius * MathUtils.sin(angle);
            p.radius = MathUtils.random(0.7f, 1.0f);
            p.rotation = angle;
            p.speed = MathUtils.random(10f, 25f);
            p.friction = 0.9f;
            p.opacity = MathUtils.random(0.7f, 0.9f);
            sparkles.add(p);
        }
    }




    public void onUpdate(){
        for(int i = 0; i < sparkles.size; i++){
            SparkleParticle p = sparkles.get(i);
            p.x += p.speed * MathUtils.cos(p.rotation);
            p.y += p.speed * MathUtils.sin(p.rotation);
            p.speed *= p.friction;
            p.radius *= 0.97f;
            p.setPosition(p.x, p.y);
            p.setScale(p.radius, p.radius);
            p.setRotation(p.getRotation());

            if (p.getScaleX() < 0.3f){
                p.opacity -= 0.01f;
                p.setAlpha(p.opacity);
            }
            batch.begin();
            p.draw(batch);
            batch.end();

            if (p.getColor().a <= 0 || p.getScaleX() <= 0) {
                sparkles.removeIndex(i);
                Pools.free(p);
            }
        }
    }



    public void dispose(){
        if(batch != null) batch.dispose();
    }


}

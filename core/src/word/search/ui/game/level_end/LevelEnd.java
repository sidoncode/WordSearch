package word.search.ui.game.level_end;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import word.search.WordGame;
import word.search.app;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.screens.GameScreen;
import word.search.screens.IntroScreen;
import word.search.ui.game.buttons.DarkeningImageButton;
import word.search.ui.game.buttons.DarkeningTextButton;
import word.search.ui.game.category.CategoryRibbon;
import word.search.ui.dialogs.DictionaryDialog;
import word.search.ui.dialogs.Modal;

import word.search.ui.game.particle.Particle;
import word.search.ui.game.particle.SparkleParticleSmall;
import word.search.ui.game.particle.Star3DParticle;
import word.search.ui.game.wordsview.PlaceHolderCoin;
import word.search.ui.hud.CoinView;
import word.search.ui.hud.game_hud.GameScreenHud;

public class LevelEnd extends Group {

    private Image cup;
    private Group ribbonContainer;
    private GameScreen gameScreen;
    private Group giftBox;
    private Image giftBoxBottom;
    private Image giftBoxLid;
    private Image coins, rays;
    private Group reward;
    private DarkeningTextButton btnOpen, btnClaim;
    private DarkeningImageButton btnDict;
    private Modal modal;
    private IncrementalProgressbar incrementalProgressbar;
    private Label gameEndLabel;

    public LevelEnd(GameScreen gameScreen){
        this.gameScreen = gameScreen;
        setSize(gameScreen.stage.getWidth(), gameScreen.stage.getHeight());

        modal = new Modal(getWidth(), getHeight());
        addActor(modal);

        cup = new Image(AtlasRegions.cup);
        addActor(cup);

        Image ribbon = new Image(AtlasRegions.cup_ribbon);
        ribbon.setOrigin(Align.center);

        TextureAtlas atlas = gameScreen.wordGame.resourceManager.get(ResourceManager.ATLAS_2, TextureAtlas.class);
        Image cupText = new Image(atlas.findRegion("cup_text_" + Language.locale.code));

        ribbonContainer = new Group();
        ribbonContainer.setSize(ribbon.getWidth(), ribbon.getHeight());
        ribbonContainer.setOrigin(Align.center);
        ribbonContainer.addActor(ribbon);
        ribbonContainer.addActor(cupText);
        cupText.setX((ribbonContainer.getWidth() - cupText.getWidth()) * 0.5f);
        cupText.setY(86);
        addActor(ribbonContainer);

        incrementalProgressbar = new IncrementalProgressbar(GameConfig.NUMBER_OF_LEVELS_TO_SOLVE_FOR_MILESTONE_REWARD);
        incrementalProgressbar.setCallback(mileStoneReward1);
        addActor(incrementalProgressbar);

        giftBoxBottom = new Image(AtlasRegions.level_end_box);
        giftBoxLid = new Image(AtlasRegions.level_end_box_lid);

        giftBox = new Group();
        giftBox.addActor(giftBoxBottom);
        giftBox.addActor(giftBoxLid);
        giftBox.setWidth(giftBoxLid.getWidth());
        giftBox.setHeight(giftBoxLid.getY() + giftBoxLid.getHeight());
        giftBox.setOrigin(Align.bottom);

        positionProgressBar();

        addActor(giftBox);

        NinePatch ninePatch = new NinePatch(NinePatches.btn_green_large);

        TextButton.TextButtonStyle greenStyle = new TextButton.TextButtonStyle();
        greenStyle.font = gameScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        greenStyle.up = new NinePatchDrawable(ninePatch);
        greenStyle.down = greenStyle.up;

        float buttonFontScale = 0.85f;
        btnOpen = new DarkeningTextButton(Language.get("open"), greenStyle);
        btnOpen.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
        btnOpen.setWidth(350f);
        btnOpen.getLabel().setFontScale(buttonFontScale);

        addActor(btnOpen);
        btnOpen.addListener(changeListener);

        NinePatch orange = new NinePatch(NinePatches.btn_orange_large);

        TextButton.TextButtonStyle orangeStyle = new TextButton.TextButtonStyle();
        orangeStyle.font = gameScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        orangeStyle.up = new NinePatchDrawable(orange);
        orangeStyle.down = orangeStyle.up;

        btnClaim = new DarkeningTextButton(Language.get("claim"), orangeStyle);
        btnClaim.getLabel().setFontScale(buttonFontScale);
        btnClaim.getLabelCell().padBottom(btnOpen.getLabelCell().getPadBottom());
        btnClaim.setWidth(btnOpen.getWidth());

        addActor(btnClaim);
        btnClaim.addListener(changeListener);

        if(Language.wordMeaningProviderMap.containsKey(Language.locale.code)){
            btnDict = new DarkeningImageButton(new TextureRegionDrawable(AtlasRegions.dictionary_icon));
            positionDictButton();
            btnDict.addListener(changeListener);
            addActor(btnDict);
        }
    }




    private void positionProgressBar(){
        if(incrementalProgressbar != null){
            float giftHalf = giftBox.getWidth() * 0.5f;
            float width = incrementalProgressbar.getWidth() + giftHalf;
            incrementalProgressbar.setX((getWidth() - width) * 0.5f);
        }
    }




    private void positionDictButton(){
        if(btnDict != null){
            btnDict.setX((getWidth() - btnDict.getWidth()) * 0.5f);
            btnDict.setY(getHeight() - btnDict.getHeight() * 1.3f);
        }
    }




    public void resize(){
        setSize(gameScreen.stage.getWidth(), gameScreen.stage.getHeight());
        if(modal != null) modal.resize(getWidth(), getHeight());

        positionDictButton();

        if(ribbonContainer != null) ribbonContainer.setX((getWidth() - ribbonContainer.getWidth()) * 0.5f);
        if(cup != null) cup.setX((getWidth() - cup.getWidth()) * 0.5f);

        if(btnOpen != null){
            btnOpen.setX((getWidth() - btnOpen.getWidth()) * 0.5f);
        }

        if(btnClaim != null){
            btnClaim.setX(btnOpen.getX());
        }

        positionProgressBar();
        positionGiftBox();
    }




    public void resetView(){
        cup.getColor().a = 1f;
        modal.getColor().a = 1f;
        incrementalProgressbar.getColor().a = 0f;
        giftBox.getColor().a = 0f;
        btnOpen.getColor().a = 0f;
        btnOpen.setTouchable(Touchable.disabled);
        btnClaim.getColor().a = 0f;
        btnClaim.setTouchable(Touchable.disabled);

        btnOpen.setX((getWidth() - btnOpen.getWidth()) * 0.5f);
        btnOpen.setY(getHeight() * 0.156f);
        btnClaim.setPosition(btnOpen.getX(), btnOpen.getY());
        if(rays != null) rays.clearActions();
        if(btnDict != null) btnDict.getColor().a = 0f;

        incrementalProgressbar.setY(700);

        giftBox.setScale(0.5f);
        positionGiftBox();

        giftBoxLid.setRotation(0);
        giftBoxLid.setX(0);
        giftBoxLid.setY(giftBoxBottom.getHeight() - 80f);

        giftBoxBottom.setX((giftBox.getWidth() - giftBoxBottom.getWidth()) * 0.5f);
    }





    private void positionGiftBox(){
        if(giftBox != null){
            float giftHalf = giftBox.getWidth() * giftBox.getScaleX() ;
            giftBox.setX(incrementalProgressbar.getX() + incrementalProgressbar.getWidth() - giftHalf * 0.5f);
            giftBox.setY(incrementalProgressbar.getY() - (giftBox.getHeight() * 0.5f - incrementalProgressbar.getHeight()) );
        }
    }




    public void animate(){
        float start = getHeight() * 0.578320f;
        float top = getHeight() * 0.657421f;
        float bottom = getHeight() * 0.63f;
        float stop = getHeight() * 0.637206f;

        resetView();
        cup.setX((getWidth() - cup.getWidth()) * 0.5f);
        cup.setY(start);

        float time = 0.25f;
        if(btnDict != null) btnDict.addAction(Actions.fadeIn(time * 2));

        cup.addAction(
                Actions.sequence(
                        Actions.moveTo(cup.getX(), top, time, Interpolation.fastSlow),
                        Actions.moveTo(cup.getX(), bottom, time * 0.6928f, Interpolation.slowFast),
                        Actions.moveTo(cup.getX(), stop, time * 0.53117f, Interpolation.fastSlow)
                )

        );

        ribbonContainer.getColor().a = 0f;
        ribbonContainer.setX((getWidth() - ribbonContainer.getWidth()) * 0.5f);
        ribbonContainer.setY(stop - 160);
        ribbonContainer.addAction(Actions.sequence(
                Actions.delay(time),
                Actions.parallel(
                        Actions.scaleTo(1.1f, 1, time * 0.6928f, Interpolation.slowFast),
                        Actions.fadeIn(time * 0.6928f, Interpolation.slowFast)
                ),

                Actions.scaleTo(1, 1, time * 0.53117f, Interpolation.fastSlow),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        startParticles = true;
                        showProgressBar();
                    }
                })
                )
        );

        incrementalProgressbar.setHighlightedCount((gameScreen.gameController.level.index % GameConfig.NUMBER_OF_LEVELS_TO_SOLVE_FOR_MILESTONE_REWARD), false);

        if(!ConfigProcessor.mutedSfx) {
            Sound sound = gameScreen.wordGame.resourceManager.get(ResourceManager.level_complete, Sound.class);
            sound.play();
        }
    }





    private void showProgressBar(){
        startParticles = true;
        incrementalProgressbar.addAction(Actions.fadeIn(0.3f));
        giftBox.addAction(Actions.sequence(Actions.fadeIn(0.3f), Actions.delay(0.1f), Actions.run(new Runnable() {
            @Override
            public void run() {
                increaseProgressBar();
            }
        })));
    }




    private void increaseProgressBar(){
        incrementalProgressbar.setHighlightedCount((gameScreen.gameController.level.index % GameConfig.NUMBER_OF_LEVELS_TO_SOLVE_FOR_MILESTONE_REWARD + 1), true);
    }




    private Runnable mileStoneReward1 = new Runnable() {

        @Override
        public void run() {
            if(gameScreen.gameController.level.index > 0 && (gameScreen.gameController.level.index + 1) % GameConfig.NUMBER_OF_LEVELS_TO_SOLVE_FOR_MILESTONE_REWARD == 0){
                startParticles = false;
                ribbonContainer.addAction(Actions.fadeOut(0.5f));
                incrementalProgressbar.addAction(Actions.fadeOut(0.5f));
                cup.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.run(mileStoneReward2)));
            }else{
                showNextButton(false);
            }
        }

    };



    private Runnable mileStoneReward2 = new Runnable() {
        @Override
        public void run() {
            incrementalProgressbar.reset();
            giftBox.addAction(
                    Actions.sequence(
                        Actions.parallel(
                                Actions.moveTo((getWidth() - giftBox.getWidth()) * 0.5f, getHeight() * 0.45f, 1f, Interpolation.fastSlow),
                                Actions.scaleTo(1f, 1f, 1f, Interpolation.fastSlow)
                        ),
                        Actions.run(mileStoneReward3),
                        Actions.repeat(RepeatAction.FOREVER, Actions.sequence(Actions.scaleTo(1.05f, 1.05f, 0.4f, Interpolation.sineOut), Actions.scaleTo(1f, 1f, 0.4f, Interpolation.sineIn)))
                    )
            );

            if(GameConfig.ENABLE_LOGGING_MILESTONE_EVENT){
                WordGame.analytics.logMileStone(gameScreen.gameController.level.index);
            }
        }


    };




    private Runnable mileStoneReward3 = new Runnable() {
        @Override
        public void run() {
            btnOpen.setText(Language.get("open"));
            btnOpen.addAction(Actions.fadeIn(0.3f, Interpolation.fastSlow));
            btnOpen.setTouchable(Touchable.enabled);
            getStage().getRoot().setTouchable(Touchable.enabled);
        }
    };




    private ChangeListener changeListener = new ChangeListener() {

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            gameScreen.stage.getRoot().setTouchable(Touchable.disabled);
            if(actor == btnOpen) {
                String label = btnOpen.getText().toString();
                if(label.equals(Language.get("open"))) {
                    openBox();
                }else if(label.equals(Language.get("back"))){
                    hide();
                }else{
                    hide();
                }
            }
            if(actor == btnClaim){
                claimReward();
            }
            if(actor == btnDict) {
                ((GameScreenHud)gameScreen.hud).showDictionary(null);
            }
        }

    };








    private void openBox(){
        btnOpen.setTouchable(Touchable.disabled);
        btnOpen.addAction(Actions.fadeOut(0.3f));
        giftBox.clearActions();
        giftBox.addAction(
                Actions.sequence(
                        Actions.scaleTo(1.2f, 0.6f, 0.3f, Interpolation.fastSlow),
                        Actions.delay(0.2f),
                        Actions.run(mileStoneReward4)
                )
        );
    }





    private Runnable mileStoneReward4 = new Runnable() {

        @Override
        public void run() {
            giftBox.addAction(
                    Actions.sequence(
                            Actions.scaleTo(1.0f, 1.2f, 0.3f, Interpolation.slowFast),
                            Actions.scaleTo(1.0f, 1.0f, 0.2f, Interpolation.fastSlow)
                    )
            );
            mileStoneReward5.run();
        }

    };




    private Runnable mileStoneReward5 = new Runnable() {

        @Override
        public void run() {
            giftBoxLid.setOrigin(Align.left);
            giftBoxLid.addAction(Actions.rotateBy(110, 0.3f, Interpolation.slowFast));
            giftBoxLid.addAction(
                    Actions.sequence(
                            Actions.moveBy(-giftBoxLid.getHeight() * 0.5f, 0, 0.3f, Interpolation.slowFast)
                    )
            );
            mileStoneReward6.run();
        }

    };




    private Runnable mileStoneReward6 = new Runnable() {

        @Override
        public void run() {
            if(reward == null){
                rays = new Image(AtlasRegions.level_end_rays);
                rays.setOrigin(Align.center);
                coins = new Image(AtlasRegions.coins_level_end);

                reward = new Group();
                reward.getColor().a = 0f;
                reward.setSize(rays.getWidth(), rays.getHeight());
                reward.setOrigin(Align.center);
                reward.addActor(rays);
                reward.addActor(coins);
                reward.setScale(0.7f);
                coins.setX((reward.getWidth() - coins.getWidth()) * 0.5f);
                coins.setY(coins.getX());

                reward.setX(giftBox.getX() + (giftBox.getWidth() - reward.getWidth()) * 0.5f);
                reward.setY(giftBox.getY());
                addActor(reward);
            }
            reward.setScale(0.7f);

            rays.addAction(Actions.forever(Actions.rotateBy(360, 6f)));

            reward.addAction(
                    Actions.sequence(
                            Actions.delay(0.2f),
                            Actions.parallel(
                                    Actions.moveTo(reward.getX(), giftBox.getY() + giftBox.getHeight() * 0.3f, 0.1f, Interpolation.slowFast),
                                    Actions.fadeIn(0.1f, Interpolation.slowFast)
                            ),
                            Actions.delay(0.5f),
                            Actions.run(mileStoneReward7)
                    )
            );
            if(!ConfigProcessor.mutedSfx) {
                Sound sound = gameScreen.wordGame.resourceManager.get(ResourceManager.milestone_pop, Sound.class);
                sound.play();
            }
        }

    };



    private Runnable mileStoneReward7 = new Runnable() {

        @Override
        public void run() {
            float time = 0.4f;
            giftBox.addAction(Actions.fadeOut(time, Interpolation.fastSlow));

            reward.addAction(
                    Actions.parallel(
                            Actions.moveBy(0, -rays.getHeight() * 0.3f, time, Interpolation.fastSlow),
                            Actions.scaleTo(1f, 1f, time, Interpolation.fastSlow)
                    )
            );

            btnClaim.addAction(Actions.fadeIn(time, Interpolation.fastSlow));
            btnClaim.setTouchable(Touchable.enabled);
            gameScreen.stage.getRoot().setTouchable(Touchable.enabled);
        }

    };



    private void claimReward(){
        btnClaim.setTouchable(Touchable.disabled);
        btnClaim.addAction(Actions.fadeOut(0.3f));

        int count = GameConfig.NUMBER_OF_REWARD_COINS_FOR_LEVEL_MILESTONE;

        if(GameConfig.ENABLE_LOGGING_EARN_VIRTUAL_CURRENCY_EVENT) WordGame.analytics.logEarnedCoinEvent(count);

        CoinView coinView = gameScreen.hud.coinView;
        coinView.remove();
        addActor(coinView);
        Vector2 vec2 = reward.localToActorCoordinates(coinView, new Vector2((reward.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f, (reward.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f));

        Actor targetCoin = coinView.coin;
        float tx = targetCoin.getX() + (targetCoin.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
        float ty = targetCoin.getY() + (targetCoin.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

        for(int i = 0; i < count; i++){
            PlaceHolderCoin coin = Pools.obtain(PlaceHolderCoin.class);
            coin.setPosition(vec2.x, vec2.y);
            coinView.addActor(coin);

            boolean last = i == count - 1;
            coin.animate((i) * 0.1f, tx, ty, coinView, i == 0, last, last ? mileStoneReward8 : null, gameScreen.wordGame.resourceManager);
            coin.run = true;
        }
    }



    private Runnable mileStoneReward8 = new Runnable() {

        @Override
        public void run() {
            gameScreen.hud.coinView.remove();
            gameScreen.stage.getRoot().addActorBefore(LevelEnd.this, gameScreen.hud.coinView);
            showNextButton(true);
        }

    };





    private void showNextButton(boolean mileStone){
        if(mileStone) reward.addAction(Actions.fadeOut(0.3f));

        int nextLevel = DataManager.get(DataManager.getLocaleAwareKey(Constants.NEXT_LEVEL_INDEX), 0);
        if(nextLevel == Language.locale.levelCount){
            btnOpen.setText(Language.get("back"));
            gameEnd();
        }else{
            btnOpen.setText(Language.get("next"));
        }

        btnOpen.addAction(Actions.fadeIn(0.3f));
        btnOpen.setTouchable(Touchable.enabled);
        getStage().getRoot().setTouchable(Touchable.enabled);
    }




    private void gameEnd(){
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = gameScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);

        gameEndLabel = new Label(Language.get("game_end"), labelStyle);
        gameEndLabel.setAlignment(Align.bottom);
        float maxWidth = getWidth() * 0.9f;
        if(gameEndLabel.getPrefWidth() > maxWidth) gameEndLabel.setFontScale(maxWidth / gameEndLabel.getPrefWidth());
        gameEndLabel.setX((getWidth() - gameEndLabel.getPrefWidth()) * 0.5f);
        gameEndLabel.setY(btnOpen.getY() + btnOpen.getHeight() + 20);
        gameEndLabel.getColor().a = 0;
        addActor(gameEndLabel);

        gameEndLabel.addAction(Actions.fadeIn(0.3f));
    }



    private void hide(){
        DictionaryDialog.words = null;
        startParticles = false;

        int nextLevel = DataManager.get(DataManager.getLocaleAwareKey(Constants.NEXT_LEVEL_INDEX), 0);
        if(nextLevel != Language.locale.levelCount) gameScreen.gameController.setNewLevel();

        if(cup.getColor().a == 1f) {
            cup.addAction(
                    Actions.parallel(
                            Actions.fadeOut(CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow),
                            Actions.moveTo(cup.getX(), cup.getY() - CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow)
                    )
            );

            ribbonContainer.addAction(
                    Actions.parallel(
                            Actions.fadeOut(CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow),
                            Actions.moveTo(ribbonContainer.getX(), ribbonContainer.getY() - CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow)
                    )
            );

            incrementalProgressbar.addAction(
                    Actions.parallel(
                            Actions.fadeOut(CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow),
                            Actions.moveTo(incrementalProgressbar.getX(), incrementalProgressbar.getY() + CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow)
                    )
            );

            giftBox.addAction(
                    Actions.parallel(
                            Actions.fadeOut(CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow),
                            Actions.moveTo(giftBox.getX(), giftBox.getY() + CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow)
                    )
            );

        }

        btnOpen.addAction(
                Actions.parallel(
                        Actions.fadeOut(CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow),
                        Actions.moveTo(btnOpen.getX(), btnOpen.getY() + CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow)
                )
        );

        if(btnDict != null) btnDict.addAction(Actions.fadeOut(CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow));

        modal.addAction(
                Actions.sequence(
                        Actions.fadeOut(CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow),
                        Actions.run(hideAnimFinished)
                )
        );

        if(gameEndLabel != null) gameEndLabel.addAction(Actions.fadeOut(CategoryRibbon.ANIM_TIME_1, Interpolation.fastSlow));
    }



    private Runnable hideAnimFinished = new Runnable() {

        @Override
        public void run() {
            remove();
            int nextLevel = DataManager.get(DataManager.getLocaleAwareKey(Constants.NEXT_LEVEL_INDEX), 0);
            if(nextLevel == Language.locale.levelCount) gameScreen.wordGame.setScreen(new IntroScreen(gameScreen.wordGame));
        }

    };






    private boolean startParticles;
    private int starTimer = 0;
    private int starTimerMax = 10;
    private float gravity = 0.13f;
    private float friction = 0.995f;
    private Array<Star3DParticle> starParticles = new Array<>();


    private void createStarParticle(){
        Star3DParticle particle = Pools.obtain(Star3DParticle.class);
        starParticles.add(particle);
        resetStarParticle(particle);
    }




    private void resetStarParticle(Star3DParticle particle){
        if(!startParticles){
            for(Particle p : starParticles) Pools.free(p);
            starParticles.clear();
            return;
        }

        particle.x = cup.getX() + cup.getWidth() * 0.5f - particle.getWidth() * 0.5f;
        particle.y = cup.getY() + cup.getHeight() * 0.9f;
        particle.vx = MathUtils.random(-9f, 9f) ;
        particle.vy = MathUtils.random(13f, 18f);
        particle.radius = MathUtils.random(0.7f, 1.0f);
        particle.setRotation(MathUtils.random(360f));
    }




    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(startParticles) {
            if (starParticles.size < 10) {
                starTimer++;
                if (starTimer == starTimerMax) {
                    createStarParticle();
                    starTimer = 0;
                }
            }

            for (int i = 0; i < starParticles.size; i++) {
                Star3DParticle particle = starParticles.get(i);

                particle.x += particle.vx;
                particle.setX(particle.x);
                particle.y += particle.vy;
                particle.y *= friction;
                particle.setY(particle.y);
                particle.vy -= gravity;
                particle.radius *= 0.98f;
                particle.setScale(particle.radius);
                if (particle.radius < 0.1f) resetStarParticle(particle);
                particle.draw(batch);
            }

            if(smallParticles.size < 15){
                smallTimer++;
                if(smallTimer == smallTimerMax) {
                    createSmallParticle();
                    smallTimer = 0;
                }
            }

            for(int i = 0; i < smallParticles.size; i++){
                SparkleParticleSmall particle = smallParticles.get(i);
                particle.setX(particle.x);
                particle.setY(particle.y);
                particle.setAlpha(particle.opacity);
                if(!particle.flag) particle.opacity += 0.1f;
                if(particle.opacity >= 1.0f) particle.flag = true;
                if(particle.flag){
                    particle.opacity -= 0.05f;
                    particle.y += particle.vy;
                }

                particle.draw(batch);

                if(particle.flag && particle.opacity <= 0.1f) resetSmallParticle(particle);
            }
        }
    }


    private int smallTimer;
    private int smallTimerMax = 10;

    private Array<SparkleParticleSmall> smallParticles = new Array<>();

    private void createSmallParticle(){
        SparkleParticleSmall particle = Pools.obtain(SparkleParticleSmall.class);
        particle.setColor(new Color( MathUtils.random(0.3f, 1f),  MathUtils.random(0.3f, 1f),  MathUtils.random(0.3f, 1f), 1f));
        particle.setScale(0.5f);
        resetSmallParticle(particle);
        smallParticles.add(particle);
    }



    private void resetSmallParticle(Particle particle){
        if(!startParticles){
            for(Particle p : smallParticles) Pools.free(p);
            smallParticles.clear();
        }
        particle.x = MathUtils.random(0, getWidth());
        particle.y = getHeight() * MathUtils.random();
        particle.vy = MathUtils.random(1f, 2f);
        particle.opacity = 0f;
        particle.flag = false;
    }


}

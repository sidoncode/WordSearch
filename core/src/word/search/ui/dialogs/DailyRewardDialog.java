package word.search.ui.dialogs;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

import word.search.WordGame;
import word.search.actions.BezierToAction;
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
import word.search.platform.ads.RewardedVideoCloseCallback;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.screens.BaseScreen;
import word.search.screens.IntroScreen;
import word.search.ui.game.buttons.DarkeningTextButton;

import word.search.ui.game.particle.Star3DParticle;
import word.search.ui.game.wordsview.PlaceHolderCoin;
import word.search.ui.hud.CoinView;
import word.search.ui.hud.intro_hud.IntroHud;

public class DailyRewardDialog extends BaseDialog {

    private enum Reward{
        COIN(AtlasRegions.reward_coin, GameConfig.DAILY_COIN_GIFT_COUNT, Constants.KEY_COINS),
        SINGLE_LETTER_REVEAL_ON_WORD(AtlasRegions.reward_single_letter_word, 1, Constants.KEY_LIGHT_BULB),
        SINGLE_LETTER_REVEAL_ON_BOARD(AtlasRegions.reward_single_letter_board, 1, Constants.KEY_MAGNIFIER),
        MAGIC_WAND(AtlasRegions.reward_magic_wand, 1, Constants.KEY_MAGIC_WAND);

        public TextureAtlas.AtlasRegion atlasRegion;
        public int quantity;
        public String name;

        Reward(TextureAtlas.AtlasRegion region, int quantity, String name){
            atlasRegion = region;
            this.quantity = quantity;
            this.name = name;
        }
    }

    private Array<Box> boxes = new Array();
    private Box box1, box2, box3;
    private Box selectedBox;
    private Reward selectedReward;
    private Group reward;
    private Image rays;
    private float marginH = 50f;
    private Image bg, bg2;
    private Label msg, quantity;
    private Label.LabelStyle labelStyle;
    private DarkeningTextButton btnPick, btnVideo;
    private int videoMultiplier = 1;



    public DailyRewardDialog(BaseScreen screen) {
        super(screen);

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;

        content.setSize(screen.stage.getHeight() * aspectRatio * 0.75f, screen.stage.getHeight() * 0.5f);
        setContentBackground();
        setContentBackgroundColor(new Color(0xd08d49ff));

        bg = new Image(NinePatches.round_rect_shadow);
        bg.setSize(content.getWidth() - 50f, content.getHeight() - 60f);
        bg.setColor(new Color(0xfceccbff));
        bg.setX((content.getWidth() - bg.getWidth()) * 0.5f);
        bg.setY(30f);
        content.addActor(bg);

        bg2 = new Image(NinePatches.iap_content);
        bg2.setWidth(bg.getWidth() * 0.93f);
        bg2.setHeight(bg.getHeight() * 0.66f);
        bg2.setColor(new Color(0xefd2a1ff));
        bg2.setX((content.getWidth() - bg2.getWidth()) * 0.5f);
        bg2.setY(bg.getHeight() * 0.25f);
        content.addActor(bg2);

        setTitleLabel(Language.get("daily_reward"));

        labelStyle = new Label.LabelStyle();
        labelStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
        labelStyle.fontColor = ColorConfig.DIALOG_TEXT_COLOR;

        msg = new Label(Language.get("pick"), labelStyle);
        float maxTextWidth = content.getWidth() * 0.9f;
        if(msg.getWidth() > maxTextWidth) msg.setFontScale(maxTextWidth / msg.getWidth());
        msg.setX((content.getWidth() - msg.getWidth()) * 0.5f);
        msg.setY(content.getHeight() * 0.1f);
        content.addActor(msg);

        setBoxes();
    }




    private void setBoxes(){
        float remaning = content.getWidth() - marginH * 2f - AtlasRegions.box1_a.getRegionWidth() - AtlasRegions.box2_a.getRegionWidth() - AtlasRegions.box3_a.getRegionWidth();
        float space = remaning * 0.25f;

        box1 = new Box(AtlasRegions.box1_a, AtlasRegions.box1_b);
        box1.setX(space + marginH);
        box1.setY(bg2.getY() + 30);
        content.addActor(box1);
        ////////////
        box2 = new Box(AtlasRegions.box2_a, AtlasRegions.box2_b);
        box2.setX(box1.getX() + box1.getWidth() + space);
        box2.setY(box1.getY());
        content.addActor(box2);
        //////////
        box3 = new Box(AtlasRegions.box3_a, AtlasRegions.box3_b);
        box3.setX(box2.getX() + box2.getWidth() + space);
        box3.setY(box2.getY());
        content.addActor(box3);

        float a = box1.getScaleX();
        float b = a * 1.1f;

        for(Actor actor : boxes){
            actor.addListener(inputListener);
            actor.addAction(Actions.forever(Actions.sequence(
                Actions.scaleTo(b, b, 0.5f, Interpolation.sineOut),
                Actions.scaleTo(a, a, 0.5f, Interpolation.sineOut)
            )));
        }
    }




    @Override
    public void show() {
        super.show();
    }



    private InputListener inputListener = new InputListener(){

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if(!ConfigProcessor.mutedSfx) {
                Sound sound = screen.wordGame.resourceManager.get(ResourceManager.box_touch, Sound.class);
                sound.play();
            }
            getStage().getRoot().setTouchable(Touchable.disabled);
            determineReward();
            boxTouched(event.getTarget().getParent());
            DataManager.set(Constants.KEY_LAST_GIFT_SHOW_TIME, TimeUtils.millis());
        }

    };



    private void determineReward(){
        int random = MathUtils.random(1, 10);
        if(random < 8) selectedReward = Reward.COIN;
        else if(random == 8) selectedReward = Reward.SINGLE_LETTER_REVEAL_ON_WORD;
        else if(random == 9) selectedReward = Reward.SINGLE_LETTER_REVEAL_ON_BOARD;
        else selectedReward = Reward.MAGIC_WAND;
    }




    private void boxTouched(Actor actor){
        if(!(actor instanceof Box)) return;
        selectedBox = (Box)actor;

        for(Actor a : boxes){
            a.setTouchable(Touchable.disabled);
            if(a != actor){
                a.addAction(Actions.fadeOut(0.15f));
            }
        }
        actor.clearActions();

        float time = 0.5f;
        actor.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.scaleTo(1,1, time, Interpolation.fastSlow),
                                Actions.moveTo((content.getWidth() - actor.getWidth()) * 0.5f, bg2.getY() + 70, time, Interpolation.fastSlow)
                        ),
                        Actions.delay(0.5f),
                        Actions.run(openBox1)
                )
        );
    }




    private Runnable openBox1 = new Runnable() {
        @Override
        public void run() {
            selectedBox.setOrigin(Align.bottom);
            selectedBox.addAction(
                    Actions.sequence(
                            Actions.scaleTo(1.2f, 0.6f, 0.5f, Interpolation.fastSlow),
                            Actions.delay(0.3f),
                            Actions.scaleTo(0.8f, 1.4f, 0.01f, Interpolation.slowFast),
                            Actions.scaleTo(1f, 1f, 0.2f, Interpolation.fastSlow)
                    )

            );
            selectedBox.top.addAction(Actions.sequence(Actions.delay(0.81f), Actions.run(openBox2)));
        }
    };



    private Runnable openBox2 = new Runnable() {
        @Override
        public void run() {
            showReward();

            float endX = (getX() + content.getX() + selectedBox.top.getX() + selectedBox.getHeight());
            float endY = 700;
            float time = 0.2f;

            BezierToAction bezier = new BezierToAction();
            bezier.setStartPosition(selectedBox.top.getX(), selectedBox.top.getY());
            bezier.setPointA(selectedBox.top.getX(), selectedBox.top.getY() + (endY - selectedBox.top.getY()) * 0.5f);
            bezier.setPointB(-endX * 0.5f, endY);
            bezier.setEndPosition(-endX, endY);
            bezier.setDuration(time);

            selectedBox.top.addAction(
                    Actions.sequence(
                            Actions.parallel(bezier, Actions.rotateTo(90, time)),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    selectedBox.top.setVisible(false);
                                }
                            })
                    )

            );
            runParticles();
        }
    };




    private void showReward(){
        Image rewardImg = new Image(selectedReward.atlasRegion);
        rays = new Image(AtlasRegions.daily_rays);
        rays.setOrigin(Align.center);
        rays.setScale(2.5f);

        reward = new Group();
        reward.setUserObject(rewardImg);
        reward.setSize(rays.getWidth(), rays.getHeight());
        reward.setOrigin(Align.bottom);
        reward.setScale(0.6f);
        reward.getColor().a = 0;
        reward.addActor(rays);
        reward.addActor(rewardImg);

        rewardImg.setX((reward.getWidth() - rewardImg.getWidth()) * 0.5f);
        rewardImg.setY((reward.getHeight() - rewardImg.getHeight()) * 0.5f);

        quantity = new Label("+" + selectedReward.quantity, labelStyle);
        quantity.setX((reward.getWidth() - quantity.getWidth()) * 0.5f);
        quantity.setY(-150);
        reward.addActor(quantity);

        reward.setX((content.getWidth() - reward.getWidth()) * 0.5f);
        reward.setY(selectedBox.getY() + selectedBox.bottom.getHeight() - reward.getHeight() * 0.5f);
        content.addActorBefore(selectedBox, reward);

        reward.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.moveTo(reward.getX(), bg2.getY() + bg2.getHeight() * 0.5f, 0.2f, Interpolation.slowFast),
                        Actions.fadeIn(0.2f, Interpolation.slowFast)
                ),
                Actions.delay(0.4f),
                Actions.parallel(
                        Actions.moveTo(reward.getX(), bg2.getY() + bg2.getHeight() * 0.3f, 0.5f, Interpolation.fastSlow),
                        Actions.scaleTo(1, 1, 0.5f, Interpolation.fastSlow)
                ),
                Actions.run(openBox3)
        ));

        selectedBox.addAction(
                Actions.sequence(
                        Actions.delay(0.6f),
                        Actions.run(openBox4),
                        Actions.fadeOut(0.5f, Interpolation.fastSlow)
                )
        );

        if(!ConfigProcessor.mutedSfx) {
            Sound sound = screen.wordGame.resourceManager.get(ResourceManager.daily_reward, Sound.class);
            sound.play();
        }
    }



    private Runnable openBox3 = new Runnable() {
        @Override
        public void run() {
            rays.addAction(Actions.forever(Actions.rotateBy(359, 8f)));
        }
    };





    private Runnable openBox4 = new Runnable() {
        @Override
        public void run() {
            if(btnPick == null){
                float remaining = bg2.getWidth() - NinePatches.btn_green_large.getTotalWidth() * 2f;
                float space = remaining * 0.33f;

                TextButton.TextButtonStyle green = new TextButton.TextButtonStyle();
                green.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
                green.up = new NinePatchDrawable(NinePatches.btn_green_large);
                green.down = green.up;

                btnPick = new DarkeningTextButton(Language.get("collect"), green);
                btnPick.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
                btnPick.getColor().a = 0;
                btnPick.setTransform(true);
                btnPick.setScale(0.8f);
                float scaleDiff = btnPick.getWidth() * (1f - 0.8f) * 0.5f;

                boolean rewardedAdEnabled = screen.wordGame.adManager != null && screen.wordGame.adManager.isRewardedAdEnabled();

                if(rewardedAdEnabled) {
                    btnPick.setX(bg2.getX() + scaleDiff + space);
                } else {
                    btnPick.setX((content.getWidth() - btnPick.getWidth() * btnPick.getScaleX()) * 0.5f);
                }

                btnPick.setY(bg.getY() + bg.getHeight() * 0.055f);
                btnPick.addListener(changeListener);
                content.addActor(btnPick);

                if(rewardedAdEnabled) {
                    TextButton.TextButtonStyle orange = new TextButton.TextButtonStyle();
                    orange.font = green.font;
                    orange.up = new NinePatchDrawable(NinePatches.btn_orange_large);
                    orange.down = orange.up;

                    Image icon = new Image(AtlasRegions.ic_video_small);

                    btnVideo = new DarkeningTextButton("x2", orange);
                    btnVideo.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
                    btnVideo.getColor().a = 0;
                    icon.setX(btnVideo.getWidth() * 0.5f - icon.getWidth() - 10);
                    icon.setY((btnVideo.getHeight() - icon.getHeight()) * 0.5f);
                    btnVideo.getLabelCell().padLeft(100);
                    btnVideo.setTransform(true);
                    btnVideo.setScale(btnPick.getScaleX());
                    btnVideo.setX(btnPick.getX() + btnPick.getWidth() + space);
                    btnVideo.setY(btnPick.getY());
                    btnVideo.addActor(icon);
                    btnVideo.addListener(changeListener);
                    btnVideo.addAction(Actions.fadeIn(0.5f));
                    content.addActor(btnVideo);
                }

                msg.addAction(Actions.fadeOut(0.5f));
                btnPick.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.run(openBox5)));
            }
        }
    };




    private Runnable openBox5 = new Runnable() {
        @Override
        public void run() {
            getStage().getRoot().setTouchable(Touchable.enabled);
        }
    };



    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if(actor == btnPick){
                if(selectedReward == Reward.COIN) giveCoinReward();
                else giveOtherHintReward();
            }else if(actor == btnVideo) doubleReward();
        }
    };




    private void doubleReward(){
        //show ad
        if(!screen.wordGame.adManager.isRewardedAdLoaded()){
            screen.hud.showToast(screen.wordGame.resourceManager, Language.get("no_video"));
        }else{
            getStage().getRoot().setTouchable(Touchable.disabled);
            screen.wordGame.adManager.showRewardedAd(rewardedVideoCloseCallback);
        }
    }



    private RewardedVideoCloseCallback rewardedVideoCloseCallback = new RewardedVideoCloseCallback() {
        @Override
        public void closed(boolean earnedReward) {
            if(earnedReward){
                if(GameConfig.ENABLE_LOGGING_EARN_VIRTUAL_CURRENCY_EVENT) {
                    WordGame.analytics.logEarnedCoinEvent(GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO);
                }

                videoMultiplier = 2;
                btnVideo.setVisible(false);
                btnVideo.setTouchable(Touchable.disabled);
                TextureAtlas atlas2 = screen.wordGame.resourceManager.get(ResourceManager.ATLAS_2, TextureAtlas.class);
                Image stamp = new Image(atlas2.findRegion("stamp_" + Language.locale.code));
                stamp.setOrigin(Align.center);
                stamp.setX(btnVideo.getX() + 40);
                stamp.setY(btnVideo.getY());
                stamp.setScale(5f);
                stamp.getColor().a = 0;
                content.addActor(stamp);
                stamp.addAction(Actions.sequence(
                        Actions.parallel(
                                Actions.fadeIn(0.3f, Interpolation.slowFast),
                                Actions.scaleTo(1,1, 0.3f, Interpolation.slowFast)
                        ),
                        Actions.run(stampAnimFinished)
                ));

                quantity.setText("+" + (selectedReward.quantity * videoMultiplier));

                if(!ConfigProcessor.mutedSfx) {
                    Sound sound = screen.wordGame.resourceManager.get(ResourceManager.duble, Sound.class);
                    sound.play();
                }
            }else{
                if(getStage() != null) getStage().getRoot().setTouchable(Touchable.enabled);
            }
        }
    };





    private Runnable stampAnimFinished = new Runnable() {
        @Override
        public void run() {
            getStage().getRoot().setTouchable(Touchable.enabled);
        }
    };



    private void giveCoinReward(){
        getStage().getRoot().setTouchable(Touchable.disabled);

        CoinView coinView = screen.hud.coinView;
        coinView.remove();
        addActor(coinView);

        Vector2 offset = reward.localToActorCoordinates(
                coinView,
                new Vector2(
                        reward.getWidth() * 0.5f - AtlasRegions.placeholder_coin.getRegionWidth() * 0.5f,
                        reward.getHeight() * 0.5f - AtlasRegions.placeholder_coin.getRegionHeight() * 0.5f
                )
        );

        Actor targetCoin = coinView.coin;
        float tx = targetCoin.getX() + (targetCoin.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
        float ty = targetCoin.getY() + (targetCoin.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

        int count = selectedReward.quantity * videoMultiplier;

        for(int i = 0; i < count; i++){
            PlaceHolderCoin coin = Pools.obtain(PlaceHolderCoin.class);

            coin.setPosition(offset.x, offset.y);
            coinView.addActor(coin);

            boolean last = i == count - 1;
            coin.animate((i) * 0.1f, tx, ty, coinView, i == 0, last, last ? coinAnimFinished : null, screen.wordGame.resourceManager);
            coin.run = true;
        }
    }




    private Runnable coinAnimFinished = new Runnable() {
        @Override
        public void run() {
            CoinView coinView = screen.hud.coinView;
            coinView.remove();
            screen.stage.getRoot().addActorBefore(DailyRewardDialog.this, coinView);
            hide();
        }
    };





    private void giveOtherHintReward(){
        getStage().getRoot().setTouchable(Touchable.disabled);

        if(GameConfig.ENABLE_LOGGING_EARNED_EARNED_HINT_EVENT)
            WordGame.analytics.logEvent(AnalyticsEvent.EVENT_EARNED_HINT, AnalyticsParam.TYPE, selectedReward.name);

        if(!ConfigProcessor.mutedSfx) {
            Sound sound = screen.wordGame.resourceManager.get(ResourceManager.daily_pick, Sound.class);
            sound.play();
        }

        if(selectedReward == Reward.MAGIC_WAND) {
            int count = DataManager.get(Constants.KEY_MAGIC_REVEAL_COUNT, GameConfig.DEFAULT_MAGIC_REVEAL_COUNT);
            DataManager.set(Constants.KEY_MAGIC_REVEAL_COUNT, count + selectedReward.quantity * videoMultiplier);
        }

        if(selectedReward == Reward.SINGLE_LETTER_REVEAL_ON_WORD){
            int count = DataManager.get(Constants.KEY_SINGLE_WORD_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_WORD_REVEAL_COUNT);
            DataManager.set(Constants.KEY_SINGLE_WORD_REVEAL_COUNT, count + selectedReward.quantity * videoMultiplier);
        }

        if(selectedReward == Reward.SINGLE_LETTER_REVEAL_ON_BOARD){
            int count = DataManager.get(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_BOARD_REVEAL_COUNT);
            DataManager.set(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT, count + selectedReward.quantity * videoMultiplier);
        }

        Image rewardImg = (Image) reward.getUserObject();
        Vector2 vec2 = rewardImg.localToStageCoordinates(new Vector2());
        rewardImg.remove();
        rewardImg.setPosition(vec2.x, vec2.y);
        getStage().addActor(rewardImg);
        getStage().getRoot().setUserObject(rewardImg);
        hide();
    }




    private void animateHintReward(){
        IntroHud introHud = (IntroHud)screen.hud;
        Image rewardImg = (Image) reward.getUserObject();
        rewardImg.setZIndex(1);
        rewardImg.setOrigin(Align.bottom);
        rewardImg.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.moveBy(0, rewardImg.getHeight() * 0.5f, 0.5f, Interpolation.fastSlow),
                        Actions.scaleTo(1.2f, 1.2f, 0.5f, Interpolation.fastSlow)
                ),

                Actions.parallel(
                        Actions.moveTo(rewardImg.getX(), introHud.btnPlay.getY() + introHud.btnPlay.getHeight() * 0.5f, 0.3f, word.search.actions.Interpolation.cubicIn),
                        Actions.scaleTo(0.5f, 0.5f, 0.3f, word.search.actions.Interpolation.cubicIn)
                ),
                Actions.run(hintAnimEnd)
        ));
    }



    private Runnable hintAnimEnd = new Runnable() {
        @Override
        public void run() {
            IntroHud introHud = (IntroHud)screen.hud;
            introHud.animatePlayButton();
            ((IntroScreen) screen).nullifyDailyDialog();
        }
    };




    @Override
    protected void hideAnimFinished() {
        remove();

        if(selectedReward == Reward.COIN) {
            screen.stage.getRoot().setTouchable(Touchable.enabled);
            ((IntroScreen) screen).nullifyDailyDialog();
        }else{
            animateHintReward();
        }
    }




    private void runParticles(){
        startParticles = true;
        for(int i = 0; i < 15; i++){
            createStarParticle();
        }
    }



    private boolean startParticles;
    private float gravity = 1.5f;
    private float friction = 0.999f;
    private Array<Star3DParticle> starParticles = new Array<>();


    private void createStarParticle(){
        Star3DParticle particle = Pools.obtain(Star3DParticle.class);
        starParticles.add(particle);

        particle.x = getX() + content.getX() + reward.getX() + reward.getWidth() * 0.5f - particle.getWidth() * 0.5f;
        particle.y = getY() + content.getY() + reward.getY() + reward.getHeight() * 0.5f;
        particle.vx = MathUtils.random(-6f, 6f) ;
        particle.vy = MathUtils.random(20f, 35f);
        particle.radius = MathUtils.random(1.0f, 1.2f);
        particle.setRotation(MathUtils.random(360f));

    }



    private void resetStarParticle(Star3DParticle particle){
        Pools.free(particle);
        starParticles.removeValue(particle, false);

        if(starParticles.size == 0){
            startParticles = false;
        }
    }




    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        for (int i = 0; i < starParticles.size && startParticles; i++) {
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
    }




    @Override
    protected void setTitleBackground() {
        titleBackground = new Image(NinePatches.word_cat_ribbon);
        titleBackground.setColor(new Color(0xC35354ff));
        titleBackground.setWidth(content.getWidth() + 100);
        titleBackground.setX(-50f);
        titleBackground.setY(content.getHeight() - titleBackground.getHeight() * 0.8f);
        content.addActor(titleBackground);
    }




    @Override
    protected void positionTitleLabel(){
        titleLabel.setX((content.getWidth() - titleLabel.getWidth() ) * 0.5f);
        titleLabel.setY(titleBackground.getY() + (titleBackground.getHeight() - titleLabel.getHeight()) * 0.7f);
    }




    class Box extends Group{
        Image top, bottom;

        public Box(TextureAtlas.AtlasRegion b, TextureAtlas.AtlasRegion t){
            this.top = new Image(t);
            this.bottom = new Image(b);
            addActor(bottom);
            addActor(top);
            top.setX(-5);
            top.setY(bottom.getHeight());
            setSize(bottom.getWidth(), top.getY() + top.getHeight());
            setOrigin(Align.center);
            setScale(0.75f);
            boxes.add(this);
        }

    }




    @Override
    public boolean navigateBack() {
        return true;
    }
}

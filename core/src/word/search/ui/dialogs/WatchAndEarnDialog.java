package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.search.WordGame;
import word.search.config.ColorConfig;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.AdManager;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.ads.RewardedVideoCloseCallback;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.screens.BaseScreen;
import word.search.screens.GameScreen;
import word.search.ui.game.buttons.DarkeningTextButton;

import word.search.ui.game.wordsview.PlaceHolderCoin;
import word.search.ui.hud.CoinView;
import word.search.ui.hud.game_hud.AdButton;

public class WatchAndEarnDialog extends BaseDialog{


    private Image coinImage;
    private CheckBox checkBox;
    private DarkeningTextButton watch;
    private Label label;
    private Label count;


    public WatchAndEarnDialog(BaseScreen screen) {
        super(screen);

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;
        float size = screen.stage.getHeight() * aspectRatio * 0.7f;
        content.setSize(size, size);
        setContentBackground();

        setCloseButton();
        closeButton.setX(content.getWidth() - closeButton.getWidth() - 25);
        closeButton.setY(content.getHeight() - closeButton.getHeight() - 25);
        closeButton.setUpColor(ColorConfig.DIALOG_TEXT_COLOR.r, ColorConfig.DIALOG_TEXT_COLOR.g, ColorConfig.DIALOG_TEXT_COLOR.b);

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
        checkBoxStyle.fontColor = ColorConfig.DIALOG_TEXT_COLOR;
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(AtlasRegions.checkbox_unchecked);
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(AtlasRegions.checkbox_checked);

        checkBox = new CheckBox(Language.get("dont_show"), checkBoxStyle);

        float checkboxMargin = 25;
        checkBox.getLabel().setFontScale(0.7f);
        checkBox.getLabelCell().padLeft(20);

        checkBox.setChecked(DataManager.get(Constants.KEY_DONT_SHOW_AD_DIALOG, false));
        checkBox.setX(checkboxMargin);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                DataManager.set(Constants.KEY_DONT_SHOW_AD_DIALOG, checkBox.isChecked());
            }
        });

        content.addActor(checkBox);

        checkBox.left();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        buttonStyle.up = new NinePatchDrawable(NinePatches.btn_green_large);
        buttonStyle.down = buttonStyle.up;

        watch = new DarkeningTextButton(Language.get("watch"), buttonStyle);
        watch.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
        watch.setX((content.getWidth() - watch.getWidth()) * 0.5f);
        content.addActor(watch);

        watch.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showVideoAd();
            }
        });


        coinImage = new Image(AtlasRegions.watch_and_earn_coins);
        coinImage.setX((content.getWidth() - coinImage.getWidth()) * 0.5f);
        content.addActor(coinImage);

        Label.LabelStyle countStyle = new Label.LabelStyle();
        countStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);

        count = new Label(String.valueOf(GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO), countStyle);
        count.setX(coinImage.getX() + 120);
        content.addActor(count);

        Label.LabelStyle msgStyle = new Label.LabelStyle();
        msgStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
        msgStyle.fontColor = ColorConfig.DIALOG_TEXT_COLOR;

        label = new Label(Language.get("watch_and_earn"), msgStyle);
        label.setAlignment(Align.bottom);
        label.setOrigin(Align.bottom);
        label.setWrap(true);
        label.setWidth(content.getWidth() * 0.8f);
        label.setX((content.getWidth() - label.getWidth()) * 0.5f);
        content.addActor(label);
    }



    @Override
    public void show() {
        throw new RuntimeException("You should call showWithOption method for this dialog box");
    }



    public void showWithOption(boolean option){
        if(option){
            positionWithOption();
        }else{
            positionWithoutOption();
        }

        checkBox.setVisible(option);
        super.show();
    }




    private void positionWithOption(){
        checkBox.setY(marginBottom * 0.5f);
        watch.setY(checkBox.getY() + checkBox.getHeight() + 10);
        positionRest();
    }




    private void positionWithoutOption(){
        watch.setY(marginBottom);
        positionRest();
    }




    private void positionRest(){
        coinImage.setY(watch.getY() + watch.getHeight() + 50);
        count.setY(coinImage.getY() + (coinImage.getHeight() - count.getHeight()) * 0.5f);

        float coinsTop = coinImage.getY() + coinImage.getHeight();
        float dy = content.getHeight() - coinsTop;
        label.setY(coinsTop + (dy - label.getPrefHeight()) * 0.5f - 10f);
    }




    private void showVideoAd(){
        if(!screen.wordGame.adManager.isRewardedAdLoaded()){
            screen.hud.showToast(screen.wordGame.resourceManager, Language.get("no_video"));
        }else {
            getStage().getRoot().setTouchable(Touchable.disabled);
            screen.wordGame.adManager.showRewardedAd(rewardVideoForCoinsHasFinishedGameScreen);
        }
    }




    private RewardedVideoCloseCallback rewardVideoForCoinsHasFinishedGameScreen = new RewardedVideoCloseCallback() {
        @Override
        public void closed(boolean earnedReward) {
            if(earnedReward){
                if(GameConfig.ENABLE_LOGGING_EARN_VIRTUAL_CURRENCY_EVENT) {
                    WordGame.analytics.logEarnedCoinEvent(GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO);
                }

                if(screen != null && screen instanceof GameScreen){
                    AdManager adManager = screen.wordGame.adManager;
                    if(adManager != null && adManager.allowOnlyOneRewardedInaLevel() && AdButton.shownRewardedAdInThisLevel()) {
                        GameScreen gameScreen = (GameScreen) screen;
                        gameScreen.gameScreenHud.btnWatchAd.setGray(true);
                    }
                }

                CoinView coinView = screen.hud.coinView;
                coinView.remove();
                addActor(coinView);

                float x = ((coinImage.getWidth() * 0.4f) - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
                float y = (coinImage.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

                Vector2 offset = coinImage.localToActorCoordinates(coinView, new Vector2(x, y));

                Actor targetCoin = coinView.coin;
                float tx = targetCoin.getX() + (targetCoin.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
                float ty = targetCoin.getY() + (targetCoin.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

                for(int i = 0; i < GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO; i++){
                    PlaceHolderCoin coin = Pools.obtain(PlaceHolderCoin.class);
                    coin.setPosition(offset.x, offset.y);
                    coinView.addActor(coin);

                    boolean last = i == GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO - 1;
                    coin.animate((i) * 0.1f, tx, ty, coinView, i == 0, last, last ? coinAnimFinished : null, screen.wordGame.resourceManager);
                    coin.run = true;
                }
            }else{
                hide();
            }
        }
    };



    private Runnable coinAnimFinished = new Runnable() {
        @Override
        public void run() {
            getStage().addActor(screen.hud.coinView);
            hide();
        }
    };



}

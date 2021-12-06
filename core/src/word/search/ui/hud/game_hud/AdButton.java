package word.search.ui.hud.game_hud;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.search.WordGame;
import word.search.actions.Interpolation;
import word.search.app;
import word.search.config.ColorConfig;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.platform.ads.RewardedVideoCloseCallback;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.screens.GameScreen;
import word.search.ui.game.buttons.DarkeningImageButton;

import word.search.ui.game.wordsview.PlaceHolderCoin;
import word.search.ui.hud.CoinView;

public class AdButton extends DarkeningImageButton {


    private GameScreen screen;
    private Image rays;
    private Image btn;
    private float originalCoinViewX, originalCoinViewY;
    public Location location = Location.OUTSIDE;

    public enum Location{
        INSIDE,
        OUTSIDE
    }


    public AdButton(GameScreen screen){
        super(new TextureRegionDrawable(AtlasRegions.ad_btn_bg));
        this.screen = screen;

        setGray(false);

        rays = new Image(AtlasRegions.ad_rays);
        rays.setOrigin(Align.center);
        rays.setY((getHeight() - rays.getHeight()) * 0.5f);
        addActor(rays);
        rays.addAction(Actions.forever(Actions.rotateBy(360, 10)));

        btn = new Image(AtlasRegions.coins_ad_button);
        btn.setX(20);
        btn.setY((getHeight() - btn.getHeight()) * 0.5f);
        addActor(btn);
    }



    public void setGray(boolean gray){
        if(btn != null) btn.getColor().a = gray ? 0.3f : 1f;
        if(rays != null) rays.getColor().a = gray ? 0.3f : 1f;

        Color color = new Color(ColorConfig.REWARDED_VIDEO_BUTTON_COLOR);
        setUpColor(color.r, color.g, color.b, gray ? 0.3f : 1f);
    }


    public float getAppearedXDistance(){
        return -(getWidth() - 20);
    }


    public void appear(float x){
        location = Location.INSIDE;
        if(screen.wordGame.adManager != null && screen.wordGame.adManager.allowOnlyOneRewardedInaLevel()) resetAdButtonState();
        addAction(Actions.moveBy(getAppearedXDistance(), 0, 0.5f, Interpolation.backOut));
    }



    public void disappear(float x){
        location = Location.OUTSIDE;
        addAction(Actions.moveTo(x, getY(), 0.5f, Interpolation.cubicOut));
    }


    public void resetAdButtonState(){
        if (AdButton.shownRewardedAdInThisLevel()) {
            setTouchable(Touchable.disabled);
            setGray(true);
        } else {
            setTouchable(Touchable.enabled);
            setGray(false);
        }
    }



    public RewardedVideoCloseCallback giveRewardWhenNoDialog = new RewardedVideoCloseCallback() {

        @Override
        public void closed(boolean earnedReward) {
            if(earnedReward){
                if(GameConfig.ENABLE_LOGGING_EARN_VIRTUAL_CURRENCY_EVENT) {
                    WordGame.analytics.logEarnedCoinEvent(GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO);
                }

                CoinView coinView = screen.hud.coinView;
                originalCoinViewX = coinView.getX();
                originalCoinViewY = coinView.getY();

                Vector2 v2 = coinView.localToActorCoordinates(AdButton.this, new Vector2());
                addActor(coinView);
                coinView.setPosition(v2.x, v2.y);

                v2.x = btn.getX() + ((btn.getWidth() * 0.5f) - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
                v2.y = (btn.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

                Vector2 offset = btn.localToActorCoordinates(coinView, v2);

                Actor targetCoin = coinView.coin;
                float tx = targetCoin.getX() + (targetCoin.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
                float ty = targetCoin.getY() + (targetCoin.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

                for(int i = 0; i < GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO; i++){
                    PlaceHolderCoin coin = Pools.obtain(PlaceHolderCoin.class);

                    coin.setPosition(offset.x, offset.y);
                    coinView.addActor(coin);

                    boolean last = i == GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO - 1;
                    coin.animate((i) * 0.1f, tx, ty, coinView, i == 0, last, last ? coinAnimFinished : null, i == 0 ? firstCoinHit : null, screen.wordGame.resourceManager);
                    coin.run = true;
                }
            }else{
                if(getStage() != null) getStage().getRoot().setTouchable(Touchable.enabled);
            }
        }

    };



    private Runnable firstCoinHit = new Runnable() {

        @Override
        public void run() {
            if(!ConfigProcessor.mutedSfx) {
                Sound sound = screen.wordGame.resourceManager.get(ResourceManager.coin_add, Sound.class);
                sound.play();
            }
        }

    };



    private Runnable coinAnimFinished = new Runnable() {

        @Override
        public void run() {
            screen.hud.coinView.setPosition(originalCoinViewX, originalCoinViewY);
            screen.stage.addActor(screen.hud.coinView);
            screen.stage.getRoot().setTouchable(Touchable.enabled);
            if(screen.wordGame.adManager != null && screen.wordGame.adManager.allowOnlyOneRewardedInaLevel() && shownRewardedAdInThisLevel()) setGray(true);
        }

    };




    public static boolean shownRewardedAdInThisLevel(){
        return DataManager.get(DataManager.getLocaleAwareKey(Constants.KEY_SHOWN_AD_IN_THIS_LEVEL), false);
    }



    public static void shownRewardedAdInThisLevel(boolean flag){
        DataManager.set(DataManager.getLocaleAwareKey(Constants.KEY_SHOWN_AD_IN_THIS_LEVEL), flag);
    }

}

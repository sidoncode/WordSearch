package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.search.WordGame;
import word.search.config.ColorConfig;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.AdManager;
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

public class NoCoinsDialog extends BaseDialog{

    private Runnable selection;
    private Image coins;
    private DarkeningTextButton watch, shop;

    public NoCoinsDialog(BaseScreen screen, final Runnable shopCallback) {
        super(screen);

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;
        content.setSize(screen.stage.getHeight() * aspectRatio * 0.8f, screen.stage.getHeight() * 0.5f);
        setContentBackground();
        setTitleLabel(Language.get("not_enough_coins"));

        setCloseButton();
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });

        coins = new Image(AtlasRegions.coins_level_end);
        coins.setX((content.getWidth() - coins.getWidth()) * 0.5f);
        coins.setY(titleBackground.getY() - coins.getHeight() - 100);
        content.addActor(coins);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        buttonStyle.up = new NinePatchDrawable(NinePatches.btn_green_large);
        buttonStyle.down = buttonStyle.up;

        watch = new DarkeningTextButton(Language.get("watch"), buttonStyle);
        watch.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
        watch.setY(marginBottom);
        content.addActor(watch);
        watch.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showVideoAd();
            }
        });

        shop = new DarkeningTextButton(Language.get("shop"), buttonStyle);
        shop.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
        shop.setY(marginBottom);
        content.addActor(shop);
        shop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selection = shopCallback;
                hide();
            }
        });

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
        labelStyle.fontColor = ColorConfig.DIALOG_TEXT_COLOR;

        Label lblMsg = new Label(Language.format("suggestion", GameConfig.NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO), labelStyle);
        lblMsg.setAlignment(Align.center);
        lblMsg.setWrap(true);
        lblMsg.setWidth(content.getWidth() * 0.8f);

        lblMsg.setX((content.getWidth() - lblMsg.getWidth()) * 0.5f);

        float buttonTop = watch.getY() + watch.getHeight();
        float dy = coins.getY() - buttonTop;
        lblMsg.setY(buttonTop + (dy - lblMsg.getHeight()) * 0.5f);

        content.addActor(lblMsg);
    }



    private void adjustDefaultButtons(){
        watch.setVisible(true);
        shop.setVisible(true);
        float remaining = content.getWidth() - watch.getWidth() * 2;
        float space = remaining / 3f;

        watch.setX(space);
        shop.setX(watch.getX() + watch.getWidth() + space);
    }



    private void adjustForShopButton(){
        watch.setVisible(false);
        shop.setVisible(true);;
        shop.setX((content.getWidth() - shop.getWidth()) * 0.5f);
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

                float x = (coins.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
                float y = (coins.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

                Vector2 offset = coins.localToActorCoordinates(coinView,  new Vector2(x, y));

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




    @Override
    public void show() {
        super.show();
        if(screen.wordGame.adManager != null && screen.wordGame.adManager.allowOnlyOneRewardedInaLevel() && AdButton.shownRewardedAdInThisLevel()) adjustForShopButton();
        else adjustDefaultButtons();
        selection = null;
    }



    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();
        if(selection != null) selection.run();
    }
}

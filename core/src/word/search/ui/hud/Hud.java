package word.search.ui.hud;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.List;

import word.search.WordGame;
import word.search.app;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.AdManager;
import word.search.managers.ConnectionManager;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.platform.iap.ShoppingCallback;
import word.search.platform.iap.ShoppingItem;
import word.search.platform.iap.ShoppingItemBundle;
import word.search.platform.iap.ShoppingItemCoins;
import word.search.platform.iap.ShoppingItemRemoveAds;
import word.search.screens.BaseScreen;
import word.search.screens.GameScreen;
import word.search.ui.Toast;
import word.search.ui.game.buttons.DarkeningImageButton;
import word.search.ui.dialogs.AlertDialog;
import word.search.ui.dialogs.SettingsDialog;
import word.search.ui.dialogs.ShoppingDialog;
import word.search.ui.dialogs.WatchAndEarnDialog;
import word.search.ui.hud.game_hud.AdButton;
import word.search.ui.hud.game_hud.GameScreenHud;

public abstract class Hud{


    public Toast toast;
    public CoinView coinView;
    public DarkeningImageButton btnSettings;
    protected float marginH, marginV;
    protected BaseScreen baseScreen;
    private SettingsDialog settingsDialog;
    public ShoppingDialog shoppingDialog;
    private WatchAndEarnDialog watchAndEarnDialog;
    private AlertDialog alertDialog;


    public Hud(BaseScreen baseScreen){
        this.baseScreen = baseScreen;

    }



    protected abstract void setUI();

    public void resize(){
        marginH = baseScreen.stage.getWidth() * 0.03f;
        marginV = baseScreen.stage.getHeight() * 0.02f;

        if(btnSettings != null) btnSettings.setY(baseScreen.stage.getHeight() - btnSettings.getHeight() - marginV);

        if(coinView != null && coinView.getParent() == baseScreen.stage.getRoot()) {
            coinView.setX(baseScreen.stage.getWidth() - coinView.getWidth() - marginH);
            coinView.setY(baseScreen.stage.getHeight() - coinView.getHeight() - marginH - 10);
        }
    }



    public Toast showToast(ResourceManager resourceManager, String msg){
        if(toast == null) {
            toast = new Toast(resourceManager, baseScreen.stage.getWidth());
            toast.setZIndex(1000);
        }else{
            toast.clearActions();
        }

        toast.setX((baseScreen.stage.getWidth() - toast.getWidth()) * 0.5f);
        toast.setY((baseScreen.stage.getHeight() - toast.getHeight()) * 0.6f);

        toast.setVisible(true);
        baseScreen.stage.addActor(toast);

        toast.show(msg);
        return toast;
    }



    protected void setCoinView(){
        coinView = new CoinView(baseScreen);
        baseScreen.stage.addActor(coinView);
        if(coinView.plus != null) coinView.setPlusListener(iapDialogOpener);
    }



    protected void setSettingsButton(){
        btnSettings = new DarkeningImageButton(new TextureRegionDrawable(AtlasRegions.btn_settings));
        baseScreen.stage.addActor(btnSettings);
    }



    protected void openSettings(){
        if(settingsDialog == null) settingsDialog = new SettingsDialog(baseScreen);
        baseScreen.stage.addActor(settingsDialog);
        settingsDialog.show();
    }




    public ChangeListener iapDialogOpener = new ChangeListener() {

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if(!ConnectionManager.network.isConnected()){
                showToast(baseScreen.wordGame.resourceManager, Language.get("no_connection"));
                return;
            }

            baseScreen.stage.getRoot().setTouchable(Touchable.disabled);

            shoppingDialog = new ShoppingDialog(baseScreen.stage.getWidth(), baseScreen.stage.getHeight(), baseScreen, iapDialogOpenFinished, iapDialogClosed);
            shoppingDialog.setVisible(true);
            baseScreen.stage.addActor(shoppingDialog);
        }

    };




    private Runnable iapDialogOpenFinished = new Runnable() {

        @Override
        public void run() {
            ShoppingCallback callback = new ShoppingCallback() {

                @Override
                public void onShoppingItemsReady(List<ShoppingItem> items) {
                    if(shoppingDialog != null){
                        shoppingDialog.setShoppingItems(items);
                    }
                }

                @Override
                public void onShoppingItemsError(int code) {
                    if(shoppingDialog != null) {
                        shoppingDialog.remove();
                        shoppingDialog = null;
                    }
                    showAlertDialog(Language.get("iap_error"), Language.format("iap_error_text", code), Language.get("ok"), null);
                }

                @Override
                public void onPurchase(String sku) {
                    savePurchase(sku);
                    WordGame.analytics.logEvent(AnalyticsEvent.EVENT_PURCHASED_ITEM, AnalyticsParam.SKU, sku);
                }

                @Override
                public void onTransactionError(int code) {
                    shoppingDialog.onTransactionError(code);
                }
            };
            baseScreen.wordGame.shoppingProcessor.queryShoppingItems(callback);
        }

    };





    private Runnable iapDialogClosed = new Runnable() {

        @Override
        public void run() {
            coinView.plus.setTouchable(Touchable.enabled);

            if(baseScreen.wordGame.adManager != null && !baseScreen.wordGame.adManager.isRewardedAdEnabled()){
                baseScreen.stage.getRoot().setTouchable(Touchable.enabled);
                if(shoppingDialog != null) {
                    shoppingDialog.remove();
                    shoppingDialog = null;
                }
                return;
            }

            boolean madeAPurchase = shoppingDialog.madeAPurchase;

            if(shoppingDialog != null) {
                shoppingDialog.remove();
                shoppingDialog = null;
            }




            if(     Hud.this instanceof GameScreenHud &&
                    baseScreen.wordGame.adManager != null &&
                    !madeAPurchase &&
                    baseScreen.wordGame.adManager.isRewardedAdLoaded()){

                if(baseScreen.wordGame.adManager.allowOnlyOneRewardedInaLevel() && AdButton.shownRewardedAdInThisLevel()){
                    baseScreen.stage.getRoot().setTouchable(Touchable.enabled);
                    return;
                }

                openWatchAndEarnDialog(false);
            } else {
                baseScreen.stage.getRoot().setTouchable(Touchable.enabled);
            }


        }

    };




    private void savePurchase(String sku){
        if(!ConfigProcessor.mutedSfx) {
            Sound sound = baseScreen.wordGame.resourceManager.get(ResourceManager.purchased_iap, Sound.class);
            sound.play();
        }

        ShoppingItem shoppingItem = shoppingDialog.getShoppingItemBySKU(sku);

        if(shoppingItem == null){
            showAlertDialog(Language.get("iap_error"), sku + " not in IAP list", Language.get("ok"), null);
            return;
        }

        if(shoppingDialog != null) {
            shoppingDialog.madeAPurchase = true;
            shoppingDialog.close();
        }

        if(!(shoppingItem instanceof ShoppingItemRemoveAds)){
            ShoppingItemCoins shoppingItemCoins = (ShoppingItemCoins)shoppingItem;
            if(shoppingItemCoins.coins > 0){
                int remaining = DataManager.get(Constants.KEY_COIN_COUNT, GameConfig.DEFAULT_COIN_COUNT);
                int newCount = remaining + shoppingItemCoins.coins;
                DataManager.set(Constants.KEY_COIN_COUNT, newCount);
                coinView.update(newCount);
            }

            if(shoppingItem instanceof ShoppingItemBundle){
                ShoppingItemBundle shoppingItemBundle = (ShoppingItemBundle)shoppingItem;
                if(shoppingItemBundle.bulbs > 0){
                    int remaining = DataManager.get(Constants.KEY_SINGLE_WORD_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_WORD_REVEAL_COUNT);
                    int count = remaining + shoppingItemBundle.bulbs;
                    DataManager.set(Constants.KEY_SINGLE_WORD_REVEAL_COUNT, count);
                    updateHintStateAfterPurchase(Constants.KEY_SINGLE_WORD_REVEAL_COUNT, count);
                }
                if(shoppingItemBundle.magnifiers > 0){
                    int remaining = DataManager.get(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_BOARD_REVEAL_COUNT);
                    int count = remaining + shoppingItemBundle.magnifiers;
                    DataManager.set(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT, count);
                    updateHintStateAfterPurchase(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT, count);
                }
                if(shoppingItemBundle.magicWands > 0){
                    int remaining = DataManager.get(Constants.KEY_MAGIC_REVEAL_COUNT, GameConfig.DEFAULT_MAGIC_REVEAL_COUNT);
                    int count = remaining + shoppingItemBundle.magicWands;
                    DataManager.set(Constants.KEY_MAGIC_REVEAL_COUNT, count);
                    updateHintStateAfterPurchase(Constants.KEY_MAGIC_REVEAL_COUNT, count);
                }
            }
        }
    }




    protected void updateHintStateAfterPurchase(String which, int quantity){

    }




    public void openWatchAndEarnDialog(boolean option){
        if(watchAndEarnDialog == null){
            watchAndEarnDialog = new WatchAndEarnDialog(baseScreen);
        }

        baseScreen.stage.addActor(watchAndEarnDialog);
        watchAndEarnDialog.showWithOption(option);
    }



    public void showAlertDialog(String title, String msg, String buttonLabel, Runnable callback){
        if(alertDialog == null) alertDialog = new AlertDialog(baseScreen);
        baseScreen.stage.addActor(alertDialog);
        alertDialog.show(title, msg, buttonLabel, callback);
    }



    public void screenFadeOut(Runnable callback){
        baseScreen.stage.getRoot().setTouchable(Touchable.disabled);
        baseScreen.stage.getRoot().addAction(Actions.sequence(
                Actions.fadeOut(0.5f, Interpolation.fastSlow),
                Actions.delay(0.1f),
                Actions.run(callback)
        ));
    }


}

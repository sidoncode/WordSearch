package word.search.ui.hud.game_hud;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import word.search.WordGame;
import word.search.app;
import word.search.config.GameConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.ConnectionManager;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.screens.GameScreen;
import word.search.screens.IntroScreen;
import word.search.ui.game.board.Board;
import word.search.ui.game.buttons.DarkeningImageButton;
import word.search.ui.game.category.CategoryRibbon;
import word.search.ui.dialogs.BonusWordsDialog;
import word.search.ui.dialogs.DictionaryDialog;
import word.search.ui.dialogs.NoCoinsDialog;
import word.search.ui.hud.Hud;


public class GameScreenHud extends Hud {

    private float LABEL_Y;

    public RevealButton rotateButton;
    public RevealButton singleRevealOnWord;
    public RevealButton singleRevealOnBoard;
    public RevealButton magicReveal;
    public BonusWordButton bonusWordButton;
    private BonusWordsDialog bonusWordsDialog;

    private Group bottomContainer;
    private DarkeningImageButton btnBack;
    private DictionaryDialog dictionaryDialog;
    private GameScreen gameScreen;
    private NoCoinsDialog noCoinsDialog;
    public AdButton btnWatchAd;
    private LevelBoard levelBoard;

    public GameScreenHud(GameScreen gameScreen){
        super(gameScreen);
        this.gameScreen = gameScreen;
        setUI();
    }




    protected void setUI(){
        btnBack = new DarkeningImageButton(new TextureRegionDrawable(AtlasRegions.btn_back));
        btnBack.addListener(onButtonClick);
        gameScreen.stage.addActor(btnBack);

        setSettingsButton();

        btnSettings.addListener(onButtonClick);

        setCoinView();

        bottomContainer = new Group();
        bottomContainer.setSize(gameScreen.stage.getWidth(), AtlasRegions.game_button_word_letter.getRegionHeight());
        bottomContainer.setY(marginV - CategoryRibbon.ANIM_MOVE_DST);

        bonusWordButton = new BonusWordButton(gameScreen.gameController);
        bonusWordButton.addListener(onButtonClick);
        bottomContainer.addActor(bonusWordButton);
        bonusWordButton.update();
        int count = DataManager.get(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT), 0);
        bonusWordButton.setCount(count);

        Label.LabelStyle quantityStyle = new Label.LabelStyle();
        quantityStyle.font = gameScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);

        rotateButton = new RevealButton(AtlasRegions.game_button_rotate, null, null);
        rotateButton.addListener(onButtonClick);
        bottomContainer.addActor(rotateButton);

        magicReveal = new RevealButton(AtlasRegions.game_button_magic_wand, AtlasRegions.cost_100, quantityStyle);
        magicReveal.addListener(onButtonClick);
        bottomContainer.addActor(magicReveal);
        bottomContainer.getColor().a = 0f;

        gameScreen.stage.addActor(bottomContainer);

        singleRevealOnBoard = new RevealButton(AtlasRegions.game_button_board_letter, AtlasRegions.cost_50, quantityStyle);
        singleRevealOnBoard.addListener(onButtonClick);
        bottomContainer.addActor(singleRevealOnBoard);

        singleRevealOnWord = new RevealButton(AtlasRegions.game_button_word_letter, AtlasRegions.cost_25, quantityStyle);
        singleRevealOnWord.addListener(onButtonClick);
        bottomContainer.addActor(singleRevealOnWord);

        levelBoard = new LevelBoard(gameScreen.wordGame.resourceManager);
        levelBoard.getColor().a = 0f;
        gameScreen.stage.addActor(levelBoard);

        if(gameScreen.wordGame.adManager != null && gameScreen.wordGame.adManager.isRewardedAdEnabled()) {
            btnWatchAd = new AdButton(gameScreen);
            btnWatchAd.addListener(onButtonClick);
            gameScreen.stage.addActor(btnWatchAd);
        }
    }




    @Override
    public void resize() {
        super.resize();

        LABEL_Y = gameScreen.stage.getHeight() - levelBoard.getHeight();

        levelBoard.setX((gameScreen.stage.getWidth() - levelBoard.getWidth()) * 0.5f);
        levelBoard.setY(LABEL_Y);

        btnBack.setX(marginH);
        btnBack.setY(gameScreen.stage.getHeight() - btnBack.getHeight() - marginV);

        btnSettings.setX(btnBack.getX() + btnBack.getWidth() + marginH * 0.5f);

        float remaining = gameScreen.stage.getWidth() - AtlasRegions.game_button_word_letter.getRegionWidth() * 5;
        float space = remaining / (6);

        bonusWordButton.setX(space);
        rotateButton.setX(bonusWordButton.getX() + bonusWordButton.getWidth() + space);
        magicReveal.setX(rotateButton.getX() + rotateButton.getWidth() + space);
        singleRevealOnBoard.setX(magicReveal.getX() + magicReveal.getWidth() + space);
        singleRevealOnWord.setX(singleRevealOnBoard.getX() + singleRevealOnBoard.getWidth() + space);

        if(btnWatchAd != null) {
            btnWatchAd.setY(Board.POSITION_Y + Constants.GAME_CONTENT_WIDTH);

           if(btnWatchAd.location == AdButton.Location.OUTSIDE){
               btnWatchAd.setX(gameScreen.stage.getWidth());
           }else{
               btnWatchAd.setX(gameScreen.stage.getWidth() - Math.abs(btnWatchAd.getAppearedXDistance()));
           }

            if (gameScreen.wordGame.adManager != null && gameScreen.wordGame.adManager.allowOnlyOneRewardedInaLevel() && AdButton.shownRewardedAdInThisLevel()) {
                btnWatchAd.setTouchable(Touchable.disabled);
                btnWatchAd.setGray(true);
            }
        }
    }








    public void updateAllRevealButtons(){
        magicReveal.update(DataManager.get(Constants.KEY_MAGIC_REVEAL_COUNT, GameConfig.DEFAULT_MAGIC_REVEAL_COUNT));
        singleRevealOnBoard.update(DataManager.get(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_BOARD_REVEAL_COUNT));
        singleRevealOnWord.update(DataManager.get(Constants.KEY_SINGLE_WORD_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_WORD_REVEAL_COUNT));
    }


    public void setLevelNumber(int n){
        levelBoard.setLevel(n);
    }




    private ChangeListener onButtonClick = new ChangeListener() {

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if(actor == singleRevealOnWord){
                deliverSingleWordHint();
            }else if(actor == singleRevealOnBoard){
                deliverSingleBoardHint();
            }else if(actor == magicReveal){
                deliverMagicHint();
            }else if(actor == bonusWordButton){
                openBonusWordsDialog();
            }else if(actor == rotateButton){
                rotateBoard();
            }else if(actor == btnSettings){
                openSettings();
            }else if(actor == btnWatchAd){
                openWatchAndEarnDialog(true);
            }else if(actor == btnBack){
                backToHome();
            }
        }

    };


    public void backToHome(){
        screenFadeOut(fadeOutEnd);
    }



    private void rotateBoard(){
        if(gameScreen.gameController.level.index < Constants.ROTATE_BUTTON_APPEAR_LEVEL){
            showToast(gameScreen.wordGame.resourceManager, Language.format("hint_locked", Constants.ROTATE_BUTTON_APPEAR_LEVEL + 1));
            return;
        }
        if(gameScreen.tutorial != null && gameScreen.tutorial.getId() == Constants.TUTORIAL_ROTATE){
            gameScreen.tutorial.close();
        }
        gameScreen.board.rotate();
    }



    private Runnable fadeOutEnd = new Runnable() {

        @Override
        public void run() {
            baseScreen.wordGame.setScreen(new IntroScreen(baseScreen.wordGame));
        }

    };



    private void deliverSingleWordHint(){
        if(GameConfig.DEBUG_WORD_ORDER) {
            gameScreen.gameController.levelFinished();
            return;
        }

        if(gameScreen.gameController.level.index < Constants.SINGLE_WORD_LETTER_APPEAR_LEVEL){
            showToast(gameScreen.wordGame.resourceManager, Language.format("hint_locked", Constants.SINGLE_WORD_LETTER_APPEAR_LEVEL + 1));
            return;
        }

        if(gameScreen.tutorial != null && gameScreen.tutorial.getId() == Constants.TUTORIAL_SINGLE_WORD_LETTER){
            gameScreen.tutorial.close();
        }

        boolean hasEnoughCredit = false;
        boolean hasEnoughCoins = false;

        int remainingCoins = DataManager.get(Constants.KEY_COIN_COUNT, GameConfig.DEFAULT_COIN_COUNT);
        int remaningHints = DataManager.get(Constants.KEY_SINGLE_WORD_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_WORD_REVEAL_COUNT);

        if(remaningHints == 0){
            if(remainingCoins >= Constants.COIN_COST_OF_USING_SINGLE_WORD_REVEAL){
                hasEnoughCoins = true;
            }
        }else{
            hasEnoughCredit = true;
        }

        if(hasEnoughCredit || hasEnoughCoins){
            if(gameScreen.wordsView.deliverSingleHint()){
                if(GameConfig.ENABLE_LOGGING_USED_HINT_EVENT)
                    WordGame.analytics.logEvent(AnalyticsEvent.EVENT_USED_HINT, AnalyticsParam.TYPE, Constants.KEY_LIGHT_BULB);

                if(hasEnoughCredit){
                    int count = remaningHints - 1;
                    DataManager.set(Constants.KEY_SINGLE_WORD_REVEAL_COUNT, count);
                    singleRevealOnWord.update(count);
                }
                else{
                    int count = remainingCoins - Constants.COIN_COST_OF_USING_SINGLE_WORD_REVEAL;
                    DataManager.set(Constants.KEY_COIN_COUNT, count);
                    coinView.update(count);
                    if(GameConfig.ENABLE_LOGGING_SPEND_VIRTUAL_CURRENCY_EVENT)
                        WordGame.analytics.logSpendCoinEvent(Constants.KEY_LIGHT_BULB, Constants.COIN_COST_OF_USING_SINGLE_WORD_REVEAL);
                }
            }else{
                showToast(baseScreen.wordGame.resourceManager, Language.get("no_hint"));
            }
        }else{
            noCoinsLeft();
        }
    }



    private void deliverSingleBoardHint(){
        if(gameScreen.gameController.level.index < Constants.SINGLE_BOARD_LETTER_APPEAR_LEVEL){
            showToast(gameScreen.wordGame.resourceManager, Language.format("hint_locked", Constants.SINGLE_BOARD_LETTER_APPEAR_LEVEL + 1));
            return;
        }

        if(gameScreen.tutorial != null && gameScreen.tutorial.getId() == Constants.TUTORIAL_SINGLE_BOARD_LETTER){
            gameScreen.tutorial.close();
        }

        boolean hasEnoughCredit = false;
        boolean hasEnoughCoins = false;

        int remainingCoins = DataManager.get(Constants.KEY_COIN_COUNT, GameConfig.DEFAULT_COIN_COUNT);
        int remaningHints = DataManager.get(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_BOARD_REVEAL_COUNT);

        if(remaningHints == 0){
            if(remainingCoins >= Constants.COIN_COST_OF_USING_SINGLE_BOARD_REVEAL){
                hasEnoughCoins = true;
            }
        }else{
            hasEnoughCredit = true;
        }

        if(hasEnoughCredit || hasEnoughCoins){
            if(gameScreen.board.deliverSingleHint()){
                if(GameConfig.ENABLE_LOGGING_USED_HINT_EVENT)
                    WordGame.analytics.logEvent(AnalyticsEvent.EVENT_USED_HINT, AnalyticsParam.TYPE, Constants.KEY_MAGNIFIER);

                if(hasEnoughCredit){
                    int count = remaningHints - 1;
                    DataManager.set(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT, count);
                    singleRevealOnBoard.update(count);
                }
                else{
                    int count = remainingCoins - Constants.COIN_COST_OF_USING_SINGLE_BOARD_REVEAL;
                    DataManager.set(Constants.KEY_COIN_COUNT, count);
                    coinView.update(count);
                    WordGame.analytics.logSpendCoinEvent(Constants.KEY_MAGNIFIER, Constants.COIN_COST_OF_USING_SINGLE_BOARD_REVEAL);
                }
            }else{
                showToast(baseScreen.wordGame.resourceManager, Language.get("no_hint"));
            }
        }else{
            noCoinsLeft();
        }
    }





    private void deliverMagicHint(){
        if(gameScreen.gameController.level.index < Language.locale.magicWandTutorialLevel){
            showToast(gameScreen.wordGame.resourceManager, Language.format("hint_locked", Language.locale.magicWandTutorialLevel + 1));
            return;
        }

        if(gameScreen.tutorial != null && gameScreen.tutorial.getId() == Constants.TUTORIAL_MAGIC_WAND){
            gameScreen.tutorial.close();
            if(GameConfig.ENABLE_LOGGING_TUTORIAL_COMPLETE_EVENT)
                WordGame.analytics.logTutorialComplete();
        }

        boolean hasEnoughCredit = false;
        boolean hasEnoughCoins = false;

        int remainingCoins = DataManager.get(Constants.KEY_COIN_COUNT, GameConfig.DEFAULT_COIN_COUNT);
        int remaningHints = DataManager.get(Constants.KEY_MAGIC_REVEAL_COUNT, GameConfig.DEFAULT_MAGIC_REVEAL_COUNT);

        if(remaningHints == 0){
            if(remainingCoins >= Constants.COIN_COST_OF_USING_MAGIC_REVEAL){
                hasEnoughCoins = true;
            }
        }else{
            hasEnoughCredit = true;
        }

        if(hasEnoughCredit || hasEnoughCoins){
            if(gameScreen.board.magicReveal()){
                if(GameConfig.ENABLE_LOGGING_USED_HINT_EVENT)
                    WordGame.analytics.logEvent(AnalyticsEvent.EVENT_USED_HINT, AnalyticsParam.TYPE, Constants.KEY_MAGIC_WAND);

                if(hasEnoughCredit){
                    int count = remaningHints - 1;
                    DataManager.set(Constants.KEY_MAGIC_REVEAL_COUNT, count);
                    magicReveal.update(count);
                }
                else{
                    int count = remainingCoins - Constants.COIN_COST_OF_USING_MAGIC_REVEAL;
                    DataManager.set(Constants.KEY_COIN_COUNT, count);
                    coinView.update(count);
                    WordGame.analytics.logSpendCoinEvent(Constants.KEY_MAGIC_WAND, Constants.COIN_COST_OF_USING_MAGIC_REVEAL);
                }
            }else{
                showToast(baseScreen.wordGame.resourceManager, Language.get("no_hint"));
            }
        }else{
            noCoinsLeft();
        }
    }





    public void openBonusWordsDialog(){
        if(gameScreen.tutorial != null && gameScreen.tutorial.getId() == Constants.TUTORIAL_BONUS){
            DataManager.set(Constants.KEY_TUTORIAL_BONUS_COMPLETE, true);
            gameScreen.tutorial.close();
        }

        if(bonusWordsDialog == null) bonusWordsDialog = new BonusWordsDialog(gameScreen);
        gameScreen.stage.addActor(bonusWordsDialog);
        bonusWordsDialog.show();
    }






    public void showDictionary(String[] words) {
        if(!ConnectionManager.network.isConnected()){
            showToast(gameScreen.wordGame.resourceManager, Language.get("no_connection"));
            return;
        }

        if(words != null)
            DictionaryDialog.words = words;

        if(dictionaryDialog == null) dictionaryDialog = new DictionaryDialog(gameScreen);

        gameScreen.stage.addActor(dictionaryDialog);
        dictionaryDialog.show();
    }




    public void showLevel(Runnable callback){
        levelBoard.setY(LABEL_Y + CategoryRibbon.ANIM_MOVE_DST);
        introduceLevelLabel(true);
        introduceBottomContainer(true, callback);
    }



    public void hideLevel(Runnable callback){
        introduceLevelLabel(false);
        introduceBottomContainer(false, callback);
    }




    private void introduceLevelLabel(boolean in){
        levelBoard.addAction(
                Actions.sequence(
                        Actions.delay(in ? CategoryRibbon.ANIM_TIME_1 + CategoryRibbon.ANIM_TIME_2 : 0f),
                        Actions.parallel(
                                (in ? Actions.fadeIn(CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow) : Actions.fadeOut(CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow)),
                                Actions.moveTo(levelBoard.getX(), in ? LABEL_Y : LABEL_Y + CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow)
                        )
                )
        );
    }




    private Runnable introduceBottomCallback;

    public void introduceBottomContainer(boolean in, Runnable callback){
        introduceBottomCallback = callback;

        bottomContainer.addAction(
                Actions.sequence(
                        Actions.delay(in ? CategoryRibbon.ANIM_TIME_1 + CategoryRibbon.ANIM_TIME_2 : 0f),
                        Actions.parallel(
                                (in ? Actions.fadeIn(CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow) : Actions.fadeOut(CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow)),
                                Actions.moveTo(bottomContainer.getX(), in ? marginV : marginV - CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow)
                        ),
                        Actions.run(introduceBottomEnd)
                )
        );

        if(!in) {
            if(btnWatchAd != null) btnWatchAd.disappear(gameScreen.stage.getWidth());
            singleRevealOnWord.stopIndicating();
        }
    }




    private Runnable introduceBottomEnd = new Runnable() {
        @Override
        public void run() {
            if(introduceBottomCallback != null) introduceBottomCallback.run();
        }
    };




    @Override
    protected void updateHintStateAfterPurchase(String which, int quantity) {
        if(which.equals(Constants.KEY_SINGLE_WORD_REVEAL_COUNT)) singleRevealOnWord.update(quantity);
        else if(which.equals(Constants.KEY_SINGLE_BOARD_REVEAL_COUNT)) singleRevealOnBoard.update(quantity);
        else if(which.equals(Constants.KEY_MAGIC_REVEAL_COUNT)) magicReveal.update(quantity);
    }



    private void showNoCoinsDialog(){
        if (noCoinsDialog == null) noCoinsDialog = new NoCoinsDialog(baseScreen, gotoShop);
        baseScreen.stage.addActor(noCoinsDialog);
        noCoinsDialog.show();
    }



    public void noCoinsLeft(){
        boolean iapEnabled = baseScreen.wordGame.shoppingProcessor != null && baseScreen.wordGame.shoppingProcessor.isIAPEnabled();
        boolean rewardedVideoEnabled = baseScreen.wordGame.adManager != null && baseScreen.wordGame.adManager.isRewardedAdEnabled();

        if(iapEnabled && rewardedVideoEnabled){
            showNoCoinsDialog();
        }else{
            if(baseScreen.wordGame.adManager != null && baseScreen.wordGame.adManager.isRewardedAdEnabled() && !AdButton.shownRewardedAdInThisLevel()){
                super.openWatchAndEarnDialog(false);
            }else{
                gameScreen.hud.showToast(gameScreen.wordGame.resourceManager, Language.get("not_enough_coins"));
            }
        }
    }




    private Runnable gotoShop = new Runnable() {
        @Override
        public void run() {
            iapDialogOpener.changed(null, null);
        }
    };



    public void setHintButtonPadlockByLevelIndex(int index){
        if(index < Constants.SINGLE_WORD_LETTER_APPEAR_LEVEL) {
            singleRevealOnWord.lock();
        } else {
            singleRevealOnWord.unlock();
            singleRevealOnWord.indicate();
        }

        if(index < Constants.SINGLE_BOARD_LETTER_APPEAR_LEVEL) singleRevealOnBoard.lock();
        else singleRevealOnBoard.unlock();

        if(index < Language.locale.magicWandTutorialLevel) magicReveal.lock();
        else magicReveal.unlock();

        if(index < Constants.ROTATE_BUTTON_APPEAR_LEVEL) rotateButton.lock();
        else rotateButton.unlock();
    }




    @Override
    public void openWatchAndEarnDialog(boolean option) {
        if(!option){
            super.openWatchAndEarnDialog(option);
        }else {
            if (DataManager.get(Constants.KEY_DONT_SHOW_AD_DIALOG, false)) {
                if (!gameScreen.wordGame.adManager.isRewardedAdLoaded()) {
                    gameScreen.hud.showToast(gameScreen.wordGame.resourceManager, Language.get("no_video"));
                } else {
                    baseScreen.stage.getRoot().setTouchable(Touchable.disabled);
                    gameScreen.wordGame.adManager.showRewardedAd(btnWatchAd.giveRewardWhenNoDialog);
                }
            } else {
                if (!gameScreen.wordGame.adManager.isRewardedAdLoaded()) {
                    gameScreen.hud.showToast(gameScreen.wordGame.resourceManager, Language.get("no_video"));
                } else {
                    super.openWatchAndEarnDialog(option);
                }
            }
        }
    }




    public Runnable watchAdButtonHider = new Runnable() {

        @Override
        public void run() {
            AdButton.shownRewardedAdInThisLevel(true);
            if(btnWatchAd != null && gameScreen.wordGame.adManager != null && gameScreen.wordGame.adManager.allowOnlyOneRewardedInaLevel()) {
                btnWatchAd.setTouchable(Touchable.disabled);
            }
        }

    };

}

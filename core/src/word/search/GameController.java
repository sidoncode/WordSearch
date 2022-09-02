package word.search;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Pools;

import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Level;
import word.search.model.LevelReadyCallback;
import word.search.model.Word;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.screens.GameScreen;
import word.search.ui.dialogs.DictionaryDialog;

import word.search.ui.hud.game_hud.AdButton;
import word.search.ui.hud.game_hud.GameScreenHud;
import word.search.ui.tutorial.TutorialFindWord;
import word.search.ui.util.UiUtil;

public class GameController {


    public GameScreen gameScreen;
    public Level level;
    public int comboCount;



    public void setNewLevel(){
        int levelIndex = 0;

        if(GameConfig.LEVEL_INDEX_TO_DEBUG == -1) levelIndex = DataManager.get(DataManager.getLocaleAwareKey(Constants.NEXT_LEVEL_INDEX), 0);
        else levelIndex = GameConfig.LEVEL_INDEX_TO_DEBUG;

        if(GameConfig.ENABLE_LOGGING_LEVEL_START_EVENT)
            WordGame.analytics.logLevelStartEvent(levelIndex);

        gameScreen.setBackgroundImage(levelIndex);

        Level.load(levelIndex, levelReadyCallback);


    }



    private LevelReadyCallback levelReadyCallback = new LevelReadyCallback() {

        @Override
        public void onLevelReady(Level level) {
            app.log("words: " + level.words);
            GameController.this.level = level;
            gameScreen.gameScreenHud.setLevelNumber(level.index + 1);
            gameScreen.board.setLevelData(level);
            gameScreen.categoryRibbon.setCategory(level.category);
            gameScreen.wordsView.setData(level, level.displayPattern, preferedPlaceHolderSize());

            gameScreen.stage.addAction(Actions.sequence(Actions.delay(0.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    gameScreen.showLevel(levelStarted);
                }
            })));
        }

    };




    private float preferedPlaceHolderSize(){
        switch (level.boardSize){
            case 3: return 70;
            case 4: return 70;
            case 5: return 70;
            case 6: return 65;
            case 7: return 65;
            case 8: return 60;
        }
        return 70;
    }



    private Runnable levelStarted = new Runnable() {

        @Override
        public void run() {
            AdButton adButton = gameScreen.gameScreenHud.btnWatchAd;
            if(adButton != null) adButton.appear(-(adButton.getWidth() - 20));
            gameScreen.stage.getRoot().setTouchable(Touchable.enabled);
            gameScreen.checkTutorial();
        }

    };




    public void selectingLetters(String text){
        gameScreen.preview.setAnimatedText(text);
    }


    public void evaluateAnswer(String answer){
        Word word = Word.findWordByAnswer(answer, level.words);

        if(gameScreen.tutorial != null && gameScreen.tutorial instanceof TutorialFindWord && word != null){//if this is not the word the user is supposed to swipe
            TutorialFindWord tutorialFindWord = (TutorialFindWord)gameScreen.tutorial;
            if(!word.equals(tutorialFindWord.getCurrentWord())) word = null;
        }

        String sfx = null;

        if(word != null){
            gameScreen.board.answeredCorrect(word);
            if(gameScreen.board.levelEnded()) gameScreen.stage.getRoot().setTouchable(Touchable.disabled);
            gameScreen.wordsView.animateCorrectAnswer(word);
            gameScreen.preview.hide();
            gameScreen.board.clearSelection();
            comboCount++;
            if(comboCount > 1) gameScreen.showFeedback(null);
            sfx = ResourceManager.success;
        }else{
            answeredWrong(answer);
            gameScreen.board.clearSelection();
            gameScreen.board.clearSelectionData();
            if(answer.length() > 0) sfx = ResourceManager.fail;
        }

        if(sfx != null && !ConfigProcessor.mutedSfx) {
            Sound sound = gameScreen.wordGame.resourceManager.get(sfx, Sound.class);
            sound.play();
        }
    }




    private void answeredWrong(String answer){
        if(answer.length() > 1) {
            if (!isExtraWord(answer) || answer.length() < Constants.MIN_LETTERS) {
                gameScreen.preview.shake();
                comboCount = 0;
            } else {
                gameScreen.preview.hide();
            }
        }else{
            gameScreen.preview.hide();
        }
    }



    private boolean isExtraWord(String answer){
        if(level.index <= Constants.TUTORIAL_FIND_WORD && gameScreen.tutorial != null) return false;

        if(answer.length() > 1 && answer.length() < Constants.MIN_LETTERS) return false;

        int result = Word.insertWordToExtraJson(answer);

        int a = (result >> 8) & 0xFF;
        int b = result & 0xFF;

        if(a == 1){
            if(b == 1){
                Word.incrementFoundBonusWordCount();
                gameScreen.gameScreenHud.bonusWordButton.animateLetters();
                int count = DataManager.get(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT), 0);
                gameScreen.gameScreenHud.bonusWordButton.setCount(count);
                if(!ConfigProcessor.mutedSfx) {
                    Sound sound = gameScreen.wordGame.resourceManager.get(ResourceManager.bonus_word, Sound.class);
                    sound.play();
                }
            }else{
                if(gameScreen.gameScreenHud.bonusWordButton.animating) return true;
                gameScreen.gameScreenHud.bonusWordButton.animating = true;
                gameScreen.gameScreenHud.bonusWordButton.clearActions();
                UiUtil.shake(gameScreen.gameScreenHud.bonusWordButton,false,gameScreen.gameScreenHud.bonusWordButton.getHeight() * 0.25f, gameScreen.gameScreenHud.bonusWordButton.shakeFinished);
                if(!ConfigProcessor.mutedSfx) {
                    Sound sound = gameScreen.wordGame.resourceManager.get(ResourceManager.bonus_word_dup, Sound.class);
                    sound.play();
                }
            }
            return true;
        }
        return false;
    }



    public void triggerBonusWordAnimation(){
        gameScreen.stage.getRoot().setTouchable(Touchable.disabled);

        if(GameConfig.ENABLE_LOGGING_EARN_VIRTUAL_CURRENCY_EVENT) {
            WordGame.analytics.logEarnedCoinEvent(GameConfig.NUMBER_OF_COINS_GIVEN_AS_BONUS_WORDS_REWARD);
        }

        Word.clearFoundBonusWordCount();
        gameScreen.gameScreenHud.bonusWordButton.animateMileStone(gameScreen.gameScreenHud.coinView);
    }




    public void levelFinished(){
        if(GameConfig.ENABLE_LOGGING_LEVEL_END_EVENT) WordGame.analytics.logLevelEndEvent(level.index);
        AdButton.shownRewardedAdInThisLevel(false);


        DictionaryDialog.words = level.getWordsAsArray();
        DataManager.set(DataManager.getLocaleAwareKey(Constants.NEXT_LEVEL_INDEX), level.index + 1);
        DataManager.remove(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORDS));
        Level.clearLevelJson();

        if(!GameConfig.DEBUG_WORD_ORDER) {
            gameScreen.wordsView.addAction(Actions.sequence(
                    Actions.delay(0.5f),
                    Actions.run(levelFinished1)
            ));
        }else{
            gameScreen.wordsView.addAction(Actions.run(levelFinished1));
        }
    }



    private Runnable levelFinished1 = new Runnable() {

        @Override
        public void run() {
            boolean shouldWeShow = GameConfig.shouldWeShowAnInterstitialAd(gameScreen.gameController.level.index);
            boolean isInterstitialEnabled = gameScreen.wordGame.adManager.isInterstitialAdEnabled();
            boolean isInterstitialAdLoaded = gameScreen.wordGame.adManager.isInterstitialAdLoaded();
            if(shouldWeShow && isInterstitialEnabled && isInterstitialAdLoaded) {
                gameScreen.wordGame.adManager.showInterstitialAd(interstitialAdClosed);
            }else {
                gameScreen.hideLevel();
                gameScreen.gameScreenHud.hideLevel(levelFinished2);
            }
        }
    };


    private Runnable interstitialAdClosed = new Runnable() {
        @Override
        public void run() {
            gameScreen.hideLevel();
            gameScreen.gameScreenHud.hideLevel(levelFinished2);
        }
    };



    private Runnable levelFinished2 = new Runnable() {

        @Override
        public void run() {
            comboCount = 0;
            gameScreen.wordsView.clearLevel();
            gameScreen.board.clearLevel();

            for(Word word : level.words) Pools.free(word);
            level.words.clear();
            if(!GameConfig.DEBUG_WORD_ORDER) gameScreen.levelFinished();
            if(GameConfig.DEBUG_WORD_ORDER) setNewLevel();

        }

    };


}

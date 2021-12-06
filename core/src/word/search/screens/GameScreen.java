package word.search.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import word.search.GameController;
import word.search.WordGame;
import word.search.config.ColorConfig;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.model.Level;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.ui.game.Feedback;
import word.search.ui.game.Preview;
import word.search.ui.game.board.Board;
import word.search.ui.game.category.CategoryRibbon;

import word.search.ui.game.level_end.LevelEnd;
import word.search.ui.game.wordsview.WordsView;
import word.search.ui.hud.game_hud.GameScreenHud;
import word.search.ui.tutorial.Tutorial;
import word.search.ui.tutorial.TutorialFindWord;

public class GameScreen extends BaseScreen {

    public WordsView wordsView;
    public Preview preview;
    public Board board;
    public LevelEnd levelEnd;
    public Feedback feedback;
    public CategoryRibbon categoryRibbon;
    public GameScreenHud gameScreenHud;
    public GameController gameController;



    public GameScreen(WordGame wordGame) {
        super(wordGame);
        stage.getRoot().getColor().a = 0;
        stage.getRoot().setTouchable(Touchable.disabled);

    }




    @Override
    public void show() {
        super.show();

        if(GameConfig.ENABLE_LOGGING_SCREEN_VIEW_EVENT)
            WordGame.analytics.logScreenChangedViewEvent("game_screen");

        int levelIndex = DataManager.get(DataManager.getLocaleAwareKey(Constants.NEXT_LEVEL_INDEX), 0);
        setBackgroundImage(levelIndex);

        Level.looper = stage.getRoot();
        Level.wordGame = wordGame;

        gameController = new GameController();
        gameController.gameScreen = this;

        board = new Board(gameController);
        board.getColor().a = 0f;
        stage.addActor(board);
        board.setInputListener();

        preview = new Preview(wordGame);
        preview.getColor().a = 0f;
        stage.addActor(preview);


        wordsView = new WordsView(gameController, board);
        wordsView.getColor().a = 0f;
        stage.addActor(wordsView);

        categoryRibbon = new CategoryRibbon(this);
        categoryRibbon.getColor().a = 0f;
        stage.addActor(categoryRibbon);

        feedback = new Feedback(this);
        stage.addActor(feedback);

        hud = new GameScreenHud(this);
        gameScreenHud = (GameScreenHud)hud;

        if(wordGame.adManager != null && wordGame.adManager.isRewardedAdEnabled() && wordGame.adManager.allowOnlyOneRewardedInaLevel())
            wordGame.adManager.setRewardedVideoStartedCallback(gameScreenHud.watchAdButtonHider);

        stage.getRoot().addAction(Actions.sequence(
                Actions.fadeIn(0.5f, Interpolation.fastSlow),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        gameController.setNewLevel();
                    }
                })
        ));
    }




    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if(categoryRibbon != null) categoryRibbon.resize();

        board.resize();
        wordsView.resize();

        feedback.setX((stage.getWidth() - feedback.getWidth()) * 0.5f);
        feedback.setY(Board.POSITION_Y + board.getHeight() + 10);
        preview.setY(feedback.getY());

        gameScreenHud.resize();

        if(levelEnd != null) levelEnd.resize();
    }



    public void showFeedback(Runnable callback){
        feedback.show(ColorConfig.FEEDBACK_RIBBON_BACKGROUND_COLOR, callback);
    }




    public void checkTutorial(){
        if(DataManager.get(Constants.KEY_SKIPPED_TUTORIAL, false)) return;
        int tutorialStep = DataManager.get(Constants.KEY_TUTORIAL_STEP, Constants.TUTORIAL_ALL_COMPLETE);
        if(tutorialStep == Constants.TUTORIAL_INTRO_PLAY_BUTTON && gameController.level.index == 0) tutorialCategory();//TUTORIAL_0 is complete
        else if(tutorialStep == Constants.TUTORIAL_GAME_HINT && gameController.level.index == 0) tutorialFindWord();
        else if(tutorialStep == Constants.TUTORIAL_FIND_WORD && gameController.level.index == Constants.SINGLE_WORD_LETTER_APPEAR_LEVEL) tutorialSingleWordLetter();
        else if(tutorialStep == Constants.TUTORIAL_SINGLE_WORD_LETTER && gameController.level.index == Constants.SINGLE_BOARD_LETTER_APPEAR_LEVEL) tutorialSingleBoardLetter();
        else if(tutorialStep == Constants.TUTORIAL_SINGLE_BOARD_LETTER && gameController.level.index == Constants.ROTATE_BUTTON_APPEAR_LEVEL) tutorialRotateButton();
        else if(tutorialStep == Constants.TUTORIAL_ROTATE && gameController.level.index == Language.locale.magicWandTutorialLevel) tutorialMagicWand();
    }




    private void tutorialCategory(){
        tutorial = new Tutorial(stage.getWidth(), stage.getHeight(), this);
        stage.getRoot().addActor(tutorial);
        tutorial.setId(Constants.TUTORIAL_GAME_HINT);
        tutorial.setCloseCallback(tutorialRemover);

        tutorial.highlight(categoryRibbon);
        tutorial.arrow(90);
        tutorial.text(Language.get("tutorial_hint"), 600,  wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class));

        tutorial.getLabelContainer().setX((stage.getWidth() - tutorial.getLabelContainer().getWidth()) * 0.5f);
        tutorial.getLabelContainer().setY(tutorial.getArrow().getY() - tutorial.getLabelContainer().getHeight() - 100);
        tutorial.setTouchToDismiss();
        tutorial.show();
    }





    private void tutorialFindWord(){
        tutorial = new TutorialFindWord(stage.getWidth(), stage.getHeight(), this);
        stage.addActor(tutorial);
        tutorial.setId(Constants.TUTORIAL_FIND_WORD);
        tutorial.setCloseCallback(tutorialRemover);
        tutorial.highlight(board);

        TutorialFindWord tutorialFindWord = (TutorialFindWord)tutorial;
        tutorialFindWord.indicateWords(board, gameController.level.words);
        tutorial.getLabelContainer().setX((stage.getWidth() - tutorial.getLabelContainer().getWidth()) * 0.5f);
        tutorial.getLabelContainer().setY(board.getY() + board.getHeight() + 20);
        tutorialFindWord.setPreview(preview);
        tutorial.show();
    }




    private void tutorialSingleWordLetter(){
        GameScreenHud gameScreenHud = (GameScreenHud)hud;
        gameScreenHud.singleRevealOnWord.stopIndicating();
        tutorial = new Tutorial(stage.getWidth(), stage.getHeight(), this);
        stage.getRoot().addActor(tutorial);
        tutorial.setId(Constants.TUTORIAL_SINGLE_WORD_LETTER);
        tutorial.setCloseCallback(tutorialRemover);

        tutorial.highlight(gameScreenHud.singleRevealOnWord);
        tutorial.arrow(-90);
        tutorial.text(Language.get("word_letter_tutorial"), 600,  wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class));

        tutorial.getLabelContainer().setX((stage.getWidth() - tutorial.getLabelContainer().getWidth()) * 0.5f);
        tutorial.getLabelContainer().setY(tutorial.getArrow().getY() + 300);
        tutorial.show();
    }




    private void tutorialSingleBoardLetter(){
        tutorial = new Tutorial(stage.getWidth(), stage.getHeight(), this);
        stage.getRoot().addActor(tutorial);
        tutorial.setId(Constants.TUTORIAL_SINGLE_BOARD_LETTER);
        tutorial.setCloseCallback(tutorialRemover);

        GameScreenHud gameScreenHud = (GameScreenHud)hud;
        tutorial.highlight(gameScreenHud.singleRevealOnBoard);
        tutorial.arrow(-90);
        tutorial.text(Language.get("board_letter_tutorial"), 600,  wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class));

        tutorial.getLabelContainer().setX((stage.getWidth() - tutorial.getLabelContainer().getWidth()) * 0.5f);
        tutorial.getLabelContainer().setY(tutorial.getArrow().getY() + 300);
        tutorial.show();
    }





    private void tutorialRotateButton(){
        tutorial = new Tutorial(stage.getWidth(), stage.getHeight(), this);
        stage.getRoot().addActor(tutorial);
        tutorial.setId(Constants.TUTORIAL_ROTATE);
        tutorial.setCloseCallback(tutorialRemover);

        GameScreenHud gameScreenHud = (GameScreenHud)hud;
        tutorial.highlight(gameScreenHud.rotateButton);
        tutorial.arrow(-90);
        tutorial.text(Language.get("rotate_tutorial"), 600,  wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class));

        tutorial.getLabelContainer().setX((stage.getWidth() - tutorial.getLabelContainer().getWidth()) * 0.5f);
        tutorial.getLabelContainer().setY(tutorial.getArrow().getY() + 300);
        tutorial.show();
    }





    private void tutorialMagicWand(){
        tutorial = new Tutorial(stage.getWidth(), stage.getHeight(), this);
        stage.getRoot().addActor(tutorial);
        tutorial.setId(Constants.TUTORIAL_MAGIC_WAND);
        tutorial.setCloseCallback(tutorialRemover);

        GameScreenHud gameScreenHud = (GameScreenHud)hud;
        tutorial.highlight(gameScreenHud.magicReveal);
        tutorial.arrow(-90);
        tutorial.text(Language.get("magic_wand_tutorial"), 600,  wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class));

        tutorial.getLabelContainer().setX((stage.getWidth() - tutorial.getLabelContainer().getWidth()) * 0.5f);
        tutorial.getLabelContainer().setY(tutorial.getArrow().getY() + 300);
        tutorial.show();
    }





    public void checkBonusWordsTutorial(){
        if(!DataManager.get(Constants.KEY_SKIPPED_TUTORIAL, false) && !DataManager.get(Constants.KEY_TUTORIAL_BONUS_COMPLETE, false)){
            tutorial = new Tutorial(stage.getWidth(), stage.getHeight(), this);
            stage.getRoot().addActor(tutorial);
            tutorial.setId(Constants.TUTORIAL_BONUS);
            tutorial.setCloseCallback(tutorialRemover);

            GameScreenHud gameScreenHud = (GameScreenHud)hud;
            tutorial.highlight(gameScreenHud.bonusWordButton);
            tutorial.arrow(-90);
            tutorial.text(Language.format("bonus_words_tutorial", GameConfig.NUMBER_OF_BONUS_WORDS_TO_FIND_FOR_REWARD), 600,  wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class));

            tutorial.getLabelContainer().setX((stage.getWidth() - tutorial.getLabelContainer().getWidth()) * 0.5f);
            tutorial.getLabelContainer().setY(tutorial.getArrow().getY() + 300);
            tutorial.show();
        }
    }





    @Override
    protected void clearTutorial() {
        int id = tutorial.getId();
        super.clearTutorial();

        if(!DataManager.get(Constants.KEY_SKIPPED_TUTORIAL, false) && id == Constants.TUTORIAL_GAME_HINT) tutorialFindWord();
    }





    public void showLevel(Runnable callback){
        GameScreenHud gameScreenHud = (GameScreenHud)hud;
        gameScreenHud.setHintButtonPadlockByLevelIndex(gameController.level.index);
        gameScreenHud.updateAllRevealButtons();

        categoryRibbon.setY(stage.getHeight() * 0.5f);
        categoryRibbon.show(true);
        board.setY(Board.POSITION_Y - CategoryRibbon.ANIM_MOVE_DST);
        board.show(true);
        wordsView.setY(WordsView.POSITION_Y + CategoryRibbon.ANIM_MOVE_DST);
        wordsView.show(true);

        gameScreenHud.showLevel(callback);
    }



    public void hideLevel(){
        board.show(false);
        wordsView.show(false);
        categoryRibbon.show(false);
    }





    public void levelFinished(){
        if(levelEnd == null) levelEnd = new LevelEnd(this);
        stage.addActor(levelEnd);
        levelEnd.animate();
    }




    public void setBackgroundImage(int level){
        setBackground(ColorConfig.GAME_SCREEN_BACKGROUND_COLOR, UIConfig.getGameScreenBackgroundImage(level));
    }




    @Override
    public void modalOpened() {
        if(board != null) board.removeInputListener();
    }



    @Override
    public void modalClosed() {
        if(board != null) board.setInputListener();
    }



    @Override
    protected boolean onBackPress() {
        if(!stage.getRoot().isTouchable()) return false;
        boolean hasDialog = super.onBackPress();

        if(!hasDialog){
            ((GameScreenHud)hud).backToHome();
            return true;
        }
        return false;
    }





    @Override
    public void render(float delta) {
        super.render(delta);

        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }



}

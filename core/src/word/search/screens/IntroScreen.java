package word.search.screens;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.TimeUtils;

import word.search.WordGame;
import word.search.config.ColorConfig;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.managers.DataManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.ui.Bird;
import word.search.ui.dialogs.DailyRewardDialog;

import word.search.ui.hud.intro_hud.IntroHud;
import word.search.ui.tutorial.Tutorial;
import word.search.ui.util.UiUtil;

import static word.search.model.Constants.TUTORIAL_NON;

public class IntroScreen extends BaseScreen {


    private IntroHud introHud;
    private DailyRewardDialog dailyRewardDialog;




    public IntroScreen(WordGame modernWordSearchGame) {
        super(modernWordSearchGame);
        Language.updateSelectedLanguage(modernWordSearchGame.resourceManager);
    }



    private void checkTutorial(){
        if(!DataManager.get(Constants.KEY_SKIPPED_TUTORIAL, false) && DataManager.get(Constants.KEY_TUTORIAL_STEP, TUTORIAL_NON) == Constants.TUTORIAL_NON){
            tutorial = new Tutorial(stage.getWidth(), stage.getHeight(), this);
            tutorial.setCloseCallback(tutorialRemover);
            stage.getRoot().addActor(tutorial);
            tutorial.show();
            tutorial.highlight(introHud.btnPlay);
            tutorial.arrow(90);

            if(GameConfig.ENABLE_LOGGING_TUTORIAL_BEGIN_EVENT){
                WordGame.analytics.logTutorialBegin();
            }
        }else{
            checkWheelDialogTiming();
        }
    }





    @Override
    public void show() {
        super.show();

        if(GameConfig.ENABLE_LOGGING_SCREEN_VIEW_EVENT)
            WordGame.analytics.logScreenChangedViewEvent("intro_screen");

        setBackground(ColorConfig.INTRO_SCREEN_BACKGROUND_COLOR, UIConfig.INTRO_SCREEN_BACKGROUND_IMAGE);

        hud = new IntroHud(this);
        introHud = (IntroHud)hud;

        stage.addAction(Actions.sequence(
                Actions.delay(0.01f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        introHud.animateIn(animateInFinished);
                    }
                })
        ));
    }





    private Runnable animateInFinished = new Runnable() {
        @Override
        public void run() {
            if(UIConfig.ENABLE_BIRD_ANIMATION){
                Bird.BirdManager.create(IntroScreen.this, new Rectangle(-350, 900, 300, 300), UIConfig.NUMBER_OF_BIRDS);
            }

            checkTutorial();
            if(dailyRewardDialog == null){
                UiUtil.pulsate(introHud.btnPlay);
            }
        }
    };



    private void dailyReward(){
        if(dailyRewardDialog == null) dailyRewardDialog = new DailyRewardDialog(this);
        stage.addActor(dailyRewardDialog);
        dailyRewardDialog.show();
    }




    public void nullifyDailyDialog(){
        dailyRewardDialog = null;
    }





    protected boolean checkWheelDialogTiming(){
        long lastSpinTime = DataManager.get(Constants.KEY_LAST_GIFT_SHOW_TIME, (long)0);

        boolean spin = false;

        if(lastSpinTime == 0){
            spin = true;
        }else{
            final long millisInADay = 86400000;
            long elapsed = TimeUtils.timeSinceMillis(lastSpinTime);

            if(elapsed > millisInADay)
                spin = true;
        }

        spin |= GameConfig.DEBUG_DAILY_GIFT;

        if(spin){
            dailyReward();
            return true;
        }

        return false;
    }



    @Override
    protected boolean onBackPress() {
        if(!stage.getRoot().isTouchable()) return false;
        boolean hasDialog = super.onBackPress();
        if(!hasDialog && wordGame.appEvents != null) wordGame.appEvents.exitApp();
        return false;
    }



    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        introHud.resize();
    }




    @Override
    public void render(float delta) {
        super.render(delta);

        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();

        if(introHud != null) introHud.onUpdate();
    }




    @Override
    public void dispose() {
        super.dispose();
        if(introHud != null) introHud.dispose();
    }
}

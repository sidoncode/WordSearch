package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.Map;

import word.search.WordGame;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.model.Locale;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.screens.BaseScreen;
import word.search.ui.game.buttons.DarkeningTextButton;


public class LanguageDialog extends BaseDialog {

    private Image glow;
    private Runnable callback;
    private String currentLanguage;
    private String newCode;

    public LanguageDialog(BaseScreen screen, Runnable callback) {
        super(screen);
        this.callback = callback;

        String selectedCode = null;
        if(Language.locale != null) selectedCode = Language.locale.code;

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;
        content.setSize(screen.stage.getHeight() * aspectRatio * (selectedCode == null ? 0.7f : 0.65f), screen.stage.getHeight() * 0.9f);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        style.up = new TextureRegionDrawable(AtlasRegions.btn_lang);
        style.down = style.up;

        glow = new Image(AtlasRegions.lang_glow);
        glow.getColor().a = 0f;
        content.addActorAt(0, glow);

        float y = 70f;

        for(Map.Entry<String, Locale> entry : GameConfig.availableLanguages.entrySet()){
            String code = entry.getKey();

            DarkeningTextButton button = new DarkeningTextButton(entry.getValue().displayName, style);
            button.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
            button.setName(code);
            button.setX((content.getWidth() - button.getWidth()) * 0.5f);
            button.setY(y);
            button.addListener(clickListener);
            content.addActor(button);
            y += button.getHeight() + 30f;

            if(glow != null && code.equals(selectedCode)) {
                glow.getColor().a = 1f;
                positionGlow(button);
            }
        }

        content.setHeight(y +  120);
        setContentBackground();
        contentBackground.setSize(content.getWidth(), content.getHeight());

        setTitleLabel(Language.getSelectedLocaleCode() == null ? "Please Select a Language" : Language.get("language"));

        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });

        if(Language.locale == null)
            closeButton.setVisible(false);

    }



    private void positionGlow(Actor button){
        glow.setPosition(button.getX() - 8f, button.getY() - 8f);
    }



    @Override
    public void show() {
        super.show();
        if(Language.locale != null && Language.locale.code != null) currentLanguage = Language.locale.code;
    }




    private ChangeListener clickListener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            Actor target = event.getTarget();
            glow.getColor().a = 0;
            positionGlow(target);
            glow.addAction(Actions.fadeIn(0.2f));

            if(target.getName() != null) {
                newCode = target.getName();

                if(GameConfig.ENABLE_LOGGING_LANGUAGE_SELECTION_EVENT)
                    WordGame.analytics.logEvent(AnalyticsEvent.EVENT_LANGUAGE_SELECTION, AnalyticsParam.LANGUAGE, newCode);

                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        }
    };






    @Override
    protected void hideAnimFinished() {
        getStage().getRoot().setTouchable(Touchable.enabled);
        remove();

        if(currentLanguage == null || (newCode != null && !currentLanguage.equals(newCode))){
            screen.setNewLanguage(newCode);
            if(callback != null)callback.run();
        }
    }




    @Override
    public boolean navigateBack() {
        if(currentLanguage == null) return false;
        return super.navigateBack();
    }


}

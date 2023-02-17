package word.search.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import word.search.WordGame;
import word.search.app;
import word.search.config.ColorConfig;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.screens.BaseScreen;
import word.search.ui.game.buttons.DarkeningTextButton;

public class PrivacyDialog extends BaseDialog{

    private DarkeningTextButton btnOk;
    private Label lblMsg;
    private TextButton privacy, terms;
    private Runnable callback;


    public PrivacyDialog(BaseScreen screen, Runnable callback, boolean full) {
        super(screen);

        this.callback = callback;

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;
        content.setWidth(screen.stage.getHeight() * aspectRatio * 0.7f);

        if(full) {
            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
            buttonStyle.up = new TextureRegionDrawable(AtlasRegions.btn_settings_row);
            buttonStyle.down = buttonStyle.up;

            btnOk = new DarkeningTextButton("Accept and play", buttonStyle);

            float maxWidth = AtlasRegions.btn_settings_row.getRegionWidth() * 0.8f;
            if(btnOk.getLabel().getPrefWidth() > maxWidth){
                btnOk.getLabel().setFontScale(maxWidth / btnOk.getPrefWidth());
                btnOk.setWidth(AtlasRegions.btn_settings_row.getRegionWidth());
            }

            btnOk.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
            btnOk.setX((content.getWidth() - btnOk.getWidth()) * 0.5f);
            btnOk.setY(marginBottom);
            content.addActor(btnOk);
            btnOk.addListener(changeListener);

        }

        TextButton.TextButtonStyle linkStyle = new TextButton.TextButtonStyle();
        linkStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
        linkStyle.fontColor = Color.BLUE;
        Gdx.app.log("game", screen.wordGame.menuConfig.termsOfUseLinkAvailable()+"");
        if (screen.wordGame.menuConfig.termsOfUseLinkAvailable()) {
            terms = new TextButton(full ? "Terms of Use" : Language.get("terms_of_use"), linkStyle);
            terms.setX((content.getWidth() - terms.getWidth()) * 0.5f);
            if(btnOk != null) terms.setY(btnOk.getY() + btnOk.getHeight() + marginBottom);
            else terms.setY(marginBottom);
            terms.addListener(changeListener);
            content.addActor(terms);
        }


        privacy = new TextButton(full ? "Privacy Policy" : Language.get("privacy_policy"), linkStyle);
        privacy.setX((content.getWidth() - privacy.getWidth()) * 0.5f);

        if(btnOk == null && terms == null){
            privacy.setY(marginBottom);
        }else if(terms != null){
            privacy.setY(terms.getY() + terms.getHeight() + marginBottom);
        }else if(terms == null){
            privacy.setY(btnOk.getY() + btnOk.getHeight() + marginBottom);
        }

        privacy.addListener(changeListener);
        content.addActor(privacy);

        if(full) {
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
            labelStyle.fontColor = ColorConfig.DIALOG_TEXT_COLOR;

            lblMsg = new Label("In order to offer you a better experience, we need you to accept our Privacy Policy and Terms of Use.", labelStyle);
            lblMsg.setFontScale(0.8f);
            lblMsg.setAlignment(Align.bottom);
            lblMsg.setWrap(true);
            lblMsg.setWidth(AtlasRegions.welcome.getRegionWidth());
            lblMsg.setX((content.getWidth() - lblMsg.getWidth()) * 0.5f);

            lblMsg.setY(privacy.getY() + privacy.getPrefHeight() + marginBottom);
            content.addActor(lblMsg);
        }


        Image welcome = new Image(AtlasRegions.welcome);
        welcome.setX((content.getWidth() - welcome.getWidth()) * 0.5f);
        if(lblMsg != null) welcome.setY(lblMsg.getY() + lblMsg.getPrefHeight() + marginBottom);
        else welcome.setY(privacy.getY() + privacy.getPrefHeight() + marginBottom);
        content.addActor(welcome);

        content.setHeight(welcome.getY() + welcome.getHeight() + 12 + NinePatches.dialog_title.getTotalHeight());
        setContentBackground();
        setTitleLabel(full ? "Welcome" : Language.get("privacy"));

        if(!full) {
            setCloseButton();
            closeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    getStage().getRoot().setTouchable(Touchable.disabled);
                    hide();
                }
            });
        }
    }



    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if(actor == btnOk){
                DataManager.set(Constants.KEY_PRIVACY_ACCEPTED, true);
                hide();
            }else if(actor == privacy){
                screen.wordGame.linkOpener.openLink(screen.wordGame.privacyUrl);
            }else if(actor == terms){
                screen.wordGame.linkOpener.openLink(screen.wordGame.tosUrl);
            }
        }
    };




    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();
        if(callback != null) callback.run();
    }


    /*@Override
    public void notifyNavigationController(BaseScreen screen) {
        if(btnOk == null) super.notifyNavigationController(screen);
    }*/


    @Override
    public boolean navigateBack() {
        if(btnOk == null) return super.navigateBack();
        return false;
    }


}

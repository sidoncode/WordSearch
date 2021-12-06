package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import word.search.WordGame;
import word.search.app;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.screens.BaseScreen;
import word.search.ui.game.buttons.DarkeningImageButton;
import word.search.ui.game.buttons.DarkeningTextButton;

public class SettingsDialog extends BaseDialog{


    private DarkeningImageButton muteFx, muteMusic, share, contactUs;
    private DarkeningTextButton gdpr, privacy, rateUs, language;
    private LanguageDialog languageDialog;
    private Image icMuteFx, disabledSfx;
    private Image disabledMusic;
    private PrivacyDialog privacyDialog;

    public SettingsDialog(BaseScreen screen) {
        super(screen);

        content.setWidth(AtlasRegions.btn_settings_row.getRegionWidth() + 150);
        content.setHeight(calculateContentHeight());
        populate();
        setContentBackground();
        setTitleLabel(Language.get("settings"));
        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });
    }




    private void populate(){
        int numSmallButtons = 1;//mute fx
        if(screen.wordGame.menuConfig.appShareEnabled()) numSmallButtons++;
        if(ConfigProcessor.enableBackgroundMusic) numSmallButtons++;
        if(screen.wordGame.menuConfig.emailSupportEnabled()) numSmallButtons++;

        float remaining = AtlasRegions.btn_settings_row.getRegionWidth() - AtlasRegions.btn_green.getRegionWidth() * numSmallButtons;
        float space = remaining / (numSmallButtons + 1);
        float marginBottom = 30f;
        float left = (content.getWidth() - AtlasRegions.btn_settings_row.getRegionWidth()) * 0.5f;

        TextureRegionDrawable greenRegion = new TextureRegionDrawable(AtlasRegions.btn_green);

        muteFx = new DarkeningImageButton(greenRegion);
        muteFx.setX(left + space);
        muteFx.setY(marginBottom);
        muteFx.addListener(changeListener);
        content.addActor(muteFx);

        icMuteFx = new Image(AtlasRegions.fx_icon);
        icMuteFx.setX((muteFx.getWidth() - icMuteFx.getWidth()) * 0.5f);
        icMuteFx.setY((muteFx.getHeight() - icMuteFx.getHeight()) * 0.6f);
        muteFx.addActor(icMuteFx);
        toggleSfxMute();

        float smallX = muteFx.getX() + muteFx.getWidth() + space;

        if(ConfigProcessor.enableBackgroundMusic) {
            muteMusic = new DarkeningImageButton(greenRegion);
            muteMusic.setX(smallX);
            smallX += muteFx.getWidth() + space;
            muteMusic.setY(marginBottom);
            muteMusic.addListener(changeListener);
            content.addActor(muteMusic);

            Image icMuteMusic = new Image(AtlasRegions.music_icon);
            icMuteMusic.setX((muteMusic.getWidth() - icMuteMusic.getWidth()) * 0.5f);
            icMuteMusic.setY((muteMusic.getHeight() - icMuteMusic.getHeight()) * 0.6f);
            muteMusic.addActor(icMuteMusic);
            toggleMusicMute(DataManager.get(Constants.KEY_MUSIC_MUTED, false));
        }

        if(screen.wordGame.menuConfig.appShareEnabled()) {
            share = new DarkeningImageButton(greenRegion);
            share.setX(smallX);
            smallX += share.getWidth() + space;
            share.setY(marginBottom);
            share.addListener(changeListener);
            content.addActor(share);

            Image icShare = new Image(AtlasRegions.share_icon);
            icShare.setX((share.getWidth() - icShare.getWidth()) * 0.5f);
            icShare.setY((share.getHeight() - icShare.getHeight()) * 0.6f);
            share.addActor(icShare);
        }

        if(screen.wordGame.menuConfig.emailSupportEnabled()) {
            contactUs = new DarkeningImageButton(greenRegion);
            contactUs.setX(smallX);
            contactUs.setY(marginBottom);
            contactUs.addListener(changeListener);
            content.addActor(contactUs);

            Image icEmail = new Image(AtlasRegions.email_icon);
            icEmail.setX((contactUs.getWidth() - icEmail.getWidth()) * 0.5f);
            icEmail.setY((contactUs.getHeight() - icEmail.getHeight()) * 0.6f);
            contactUs.addActor(icEmail);
        }

        TextButton.TextButtonStyle orangeStyle = new TextButton.TextButtonStyle();
        orangeStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        orangeStyle.up = new TextureRegionDrawable(AtlasRegions.btn_settings_row);
        orangeStyle.down = orangeStyle.up;

        float padBottom = UIConfig.BUTTON_BOTTOM_PADDING;

        float gdprY = muteFx.getY() + muteFx.getHeight() + marginBottom;

        if(screen.wordGame.adManager != null && screen.wordGame.adManager.isUserInEU()){
            gdpr = new DarkeningTextButton("GDPR", orangeStyle);
            gdpr.getLabelCell().padBottom(padBottom);
            gdpr.setX(left);
            gdpr.setY(gdprY);
            gdpr.addListener(changeListener);
            clampButtonText(gdpr);
            content.addActor(gdpr);
        }

        float privacyY = gdpr == null ? (gdprY) : gdpr.getY() + gdpr.getHeight() + marginBottom;
        if(screen.wordGame.menuConfig.showPrivacyDialogInSettingsDialog()) {
            privacy = new DarkeningTextButton(Language.get("privacy"), orangeStyle);
            privacy.getLabelCell().padBottom(padBottom);
            privacy.setX(left);
            privacy.setY(privacyY);
            privacy.addListener(changeListener);
            clampButtonText(privacy);
            content.addActor(privacy);
        }

        float rateUsY = privacy == null ? privacyY : privacyY + muteFx.getHeight() + marginBottom;
        if(screen.wordGame.menuConfig.rateUsEnabled()) {
            rateUs = new DarkeningTextButton(Language.get("rate_us"), orangeStyle);
            rateUs.getLabelCell().padBottom(padBottom);
            rateUs.setX(left);
            rateUs.setY(rateUsY);
            rateUs.addListener(changeListener);
            clampButtonText(rateUs);
            content.addActor(rateUs);
        }

        float langY = rateUs == null ? rateUsY : rateUsY + muteFx.getHeight() + marginBottom;
        if(GameConfig.availableLanguages.size() > 1) {
            language = new DarkeningTextButton(Language.locale.displayName, orangeStyle);
            language.getLabelCell().padBottom(padBottom);
            language.setX(left);
            language.setY(langY);
            language.addListener(changeListener);
            clampButtonText(language);
            content.addActor(language);
        }
    }




    private void clampButtonText(DarkeningTextButton button){
        float maxWidth = AtlasRegions.btn_settings_row.getRegionWidth() * 0.8f;
        Label label = button.getLabel();
        if(label.getPrefWidth() > maxWidth){
            label.setFontScale(maxWidth / label.getPrefWidth());
            button.setWidth(AtlasRegions.btn_settings_row.getRegionWidth());
        }else{
            label.setFontScale(0.9f);
        }
    }




    private float calculateContentHeight(){
        float height = 330;
        float row = AtlasRegions.btn_settings_row.getRegionHeight() + 30;
        if(screen.wordGame.adManager != null && screen.wordGame.adManager.isUserInEU()) height += row;
        if(GameConfig.availableLanguages.size() > 1) height += row;
        if(screen.wordGame.menuConfig.showPrivacyDialogInSettingsDialog()) height += row;
        if(screen.wordGame.menuConfig.rateUsEnabled()) height += row;
        return height;
    }



    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if(actor == language) openLanguageDialog();
            else if(actor == muteFx) muteSfx();
            else if(actor == muteMusic) muteMusic();
            else if(actor == privacy) openPrivacy();
            else if(actor == rateUs) rateApp();
            else if(actor == share) shareApp();
            else if(actor == contactUs) sendEmail();
            else if(actor == gdpr) openGDPR();
        }
    };




    private void openLanguageDialog(){
        if(languageDialog == null) languageDialog = new LanguageDialog(screen, screen.languageSelectionComplete);
        addActor(languageDialog);
        languageDialog.show();
    }




    private void openPrivacy(){
        privacyDialog = new PrivacyDialog(screen, privacyDialogRemover, false);
        addActor(privacyDialog);
        privacyDialog.show();
    }



    private Runnable privacyDialogRemover = new Runnable() {
        @Override
        public void run() {
            privacyDialog = null;
        }
    };



    private void muteSfx(){
        boolean current = DataManager.get(Constants.KEY_SFX_MUTED, false);
        DataManager.set(Constants.KEY_SFX_MUTED, !current);
        ConfigProcessor.mutedSfx = !current;
        toggleSfxMute();
    }




    private void toggleSfxMute(){
        if(ConfigProcessor.mutedSfx){
            if(disabledSfx == null) {
                disabledSfx = new Image(AtlasRegions.disabled);
                disabledSfx.setX((muteFx.getWidth() - disabledSfx.getWidth()) * 0.5f);
                disabledSfx.setY((muteFx.getHeight() - disabledSfx.getWidth()) * 0.6f);
            }
            muteFx.addActor(disabledSfx);
        }else{
            if(disabledSfx != null) disabledSfx.remove();
        }
    }



    private void muteMusic(){
        boolean muted = DataManager.get(Constants.KEY_MUSIC_MUTED, false);
        muted = !muted;
        DataManager.set(Constants.KEY_MUSIC_MUTED, muted);
        toggleMusicMute(muted);
        screen.wordGame.playMusic(muted);
    }




    private void toggleMusicMute(boolean play){
        if(play){
            if(disabledMusic == null){
                disabledMusic = new Image(AtlasRegions.disabled);
                disabledMusic.setX((muteMusic.getWidth() - disabledMusic.getWidth()) * 0.5f);
                disabledMusic.setY((muteMusic.getHeight() - disabledMusic.getWidth()) * 0.6f);
            }
            muteMusic.addActor(disabledMusic);
        }else{
            if(disabledMusic != null) disabledMusic.remove();
        }
    }




    private void rateApp(){
        if(screen.wordGame.rateUsLauncher != null) screen.wordGame.rateUsLauncher.launch();
    }




    private void shareApp(){
        if(screen.wordGame.appShare != null) {
            screen.wordGame.appShare.share(Language.get("app_share_text"));
            if(GameConfig.ENABLE_LOGGING_APP_SHARE) WordGame.analytics.logShare();
        }
    }




    private void sendEmail(){
        if(screen.wordGame.supportRequest != null)
            screen.wordGame.supportRequest.sendSupportEmail(Language.get("support_email_subject"), Language.get("support_email_title"));
    }




    private void openGDPR(){
        screen.wordGame.adManager.openGDPRForm();
    }

}

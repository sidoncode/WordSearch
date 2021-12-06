package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import word.search.config.ColorConfig;
import word.search.config.UIConfig;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.screens.BaseScreen;
import word.search.ui.game.buttons.DarkeningTextButton;

public class AlertDialog extends BaseDialog {

    private DarkeningTextButton btnOk;
    private Label lblMsg;
    private Runnable callback;


    public AlertDialog(BaseScreen screen) {
        super(screen);

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;
        content.setSize(screen.stage.getHeight() * aspectRatio * 0.6f, screen.stage.getHeight() * 0.4f);
        setContentBackground();
        setTitleLabel("Label");

        setButton();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        labelStyle.fontColor = ColorConfig.DIALOG_TEXT_COLOR;

        lblMsg = new Label("Message", labelStyle);
        lblMsg.setAlignment(Align.center);
        lblMsg.setWrap(true);
        lblMsg.setWidth(content.getWidth() * 0.8f);
        content.addActor(lblMsg);
    }



    protected void setButton(){
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        buttonStyle.up = new NinePatchDrawable(NinePatches.btn_green_large);
        buttonStyle.down = buttonStyle.up;

        btnOk = new DarkeningTextButton("Button", buttonStyle);
        btnOk.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
        btnOk.setX((content.getWidth() - btnOk.getWidth()) * 0.5f);
        btnOk.setY(marginBottom);
        content.addActor(btnOk);
        btnOk.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
    }




    @Override
    public void show() {
        throw new RuntimeException("You shouldn't call this method for this dialog box");
    }



    public void show(String title, String msg, String buttonLabel, Runnable callback){
        titleLabel.setText(title);
        positionTitleLabel();

        lblMsg.setText(msg);
        lblMsg.setX((content.getWidth() - lblMsg.getWidth()) * 0.5f);
        float btnTop = btnOk.getY() + btnOk.getHeight();
        float dy = titleBackground.getY() - btnTop;
        lblMsg.setY(btnTop + (dy - lblMsg.getHeight()) * 0.5f);

        btnOk.setText(buttonLabel);
        this.callback = callback;
        super.show();
    }




    @Override
    protected void positionTitleLabel() {
        titleLabel.setX((titleBackground.getWidth() - titleLabel.getWidth()) * 0.5f);
        titleLabel.setY(titleBackground.getY() + (titleBackground.getHeight() - titleLabel.getHeight()) * 0.5f);
    }



    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();
        if(callback != null) callback.run();
    }
}

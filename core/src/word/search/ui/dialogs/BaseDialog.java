package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.search.app;
import word.search.config.ColorConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;
import word.search.screens.BaseScreen;
import word.search.ui.game.buttons.DarkeningImageButton;


public class BaseDialog extends Group implements BackNavigator {

    protected Modal modal;
    protected Group content;
    protected BaseScreen screen;
    protected float closeScale = 0.6f;
    protected Label titleLabel;
    private Label.LabelStyle titleStyle;
    protected DarkeningImageButton closeButton;
    protected Image contentBackground;
    protected Image titleBackground;
    protected float marginBottom = 40;


    public BaseDialog(BaseScreen screen){
        setSize(screen.stage.getWidth(), screen.stage.getHeight());
        this.screen = screen;

        modal = new Modal(getWidth(), getHeight());
        addActor(modal);

        content = new Group();
        addActor(content);
    }





    protected void setTitleBackground(){
        if(titleBackground == null){
            titleBackground = new Image(NinePatches.dialog_title);
            titleBackground.setWidth(content.getWidth());
            titleBackground.setY(content.getHeight() - titleBackground.getHeight() * 0.8f);
            content.addActor(titleBackground);
        }
        setTitleBackgroundColor(ColorConfig.DIALOG_TITLE_BACKGROUND_COLOR);
    }





    protected void setTitleLabel(String text){
        if(titleLabel == null){
            setTitleBackground();
            titleStyle = new Label.LabelStyle();
            titleStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
            titleStyle.fontColor = ColorConfig.DIALOG_TITLE_COLOR;

            titleLabel = new Label(text, titleStyle);

            GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
            glyphLayout.setText(titleLabel.getStyle().font, text);
            float maxWidth = titleBackground.getWidth() * 0.7f;
            if(glyphLayout.width > maxWidth) titleLabel.setFontScale(maxWidth / glyphLayout.width);

            titleLabel.setAlignment(Align.center);
            positionTitleLabel();
            content.addActor(titleLabel);
        }else{
            titleLabel.setText(text);
        }
    }





    protected void positionTitleLabel(){
        titleLabel.setX((content.getWidth() - titleLabel.getWidth() ) * 0.5f);
        titleLabel.setY(titleBackground.getY() + (titleBackground.getHeight() - titleLabel.getHeight()) * 0.5f);
    }




    public void setTitleBackgroundColor(Color color){
        titleBackground.setColor(color);
    }




    protected void setCloseButton(){
        closeButton = new DarkeningImageButton(new TextureRegionDrawable(AtlasRegions.close_btn_up));
        closeButton.setOrigin(Align.center);

        if(titleBackground != null) {
            closeButton.setX(titleBackground.getWidth() - closeButton.getWidth() * 2.0f);
            closeButton.setY(titleBackground.getY() + (titleBackground.getHeight() - closeButton.getHeight()) * 0.5f);
        }

        content.addActor(closeButton);
    }





    protected void setContentBackground(){
        contentBackground = new Image(new NinePatchDrawable(NinePatches.dialog));
        contentBackground.setSize(content.getWidth(), content.getHeight());
        setContentBackgroundColor(ColorConfig.DIALOG_BACKGROUND_COLOR);
        content.addActorAt(0, contentBackground);
    }




    public void setContentBackgroundColor(Color color){
        contentBackground.setColor(color);
    }



    @Override
    public void notifyNavigationController(BaseScreen screen) {
        screen.backNavQueue.push(this);
    }




    @Override
    public boolean navigateBack() {
        hide();
        return true;
    }




    protected void setContentPosition(){
        content.setOrigin(Align.center);
        content.setX((getWidth() - content.getWidth()) * 0.5f);
        content.setY((getHeight() - content.getHeight()) * 0.5f);
    }




    public void show(){
        notifyNavigationController(screen);
        updateSizes();
        setVisible(true);
        setContentPosition();

        content.setScale(closeScale);
        modal.getColor().a = 1f;

        content.clearActions();
        openDialog();
    }



    private void updateSizes(){
        setSize(screen.stage.getWidth(), screen.stage.getHeight());
        modal.resize(screen.stage.getWidth(), screen.stage.getHeight());
    }




    protected void openAnimFinished(){
        screen.modalOpened();
        getStage().getRoot().setTouchable(Touchable.enabled);

    }



    public void hide(){
        if(!screen.backNavQueue.empty()) screen.backNavQueue.pop();
        content.clearActions();
        closeDialog();
    }





    protected void hideAnimFinished(){
        getStage().getRoot().setTouchable(Touchable.enabled);
        remove();
        screen.modalClosed();
    }



    protected void openDialog(){
        float time = 0.4f;

        content.addAction(Actions.sequence(
                Actions.scaleTo(1,1, time, word.search.actions.Interpolation.backOut),
                Actions.run(openAnimFinishedCallback)
        ));
    }


    private Runnable openAnimFinishedCallback = new Runnable() {
        @Override
        public void run() {
            openAnimFinished();
        }
    };



    protected void closeDialog(){
        float time = 0.6f;

        content.addAction(Actions.sequence(
                Actions.scaleTo(0,0,time, word.search.actions.Interpolation.backIn),
                Actions.run(closeAnimFinishedCallback)
        ));
    }



    private Runnable closeAnimFinishedCallback= new Runnable() {
        @Override
        public void run() {
            modal.addAction(Actions.sequence(
                    Actions.fadeOut(0.1f),
                    Actions.run(modalFadeOutFinishedCallback)
            ));
        }
    };



    private Runnable modalFadeOutFinishedCallback = new Runnable() {
        @Override
        public void run() {
            hideAnimFinished();
        }
    };



}

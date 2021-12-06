package word.search.ui.tutorial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import word.search.config.ColorConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.screens.BaseScreen;
import word.search.ui.dialogs.BackNavigator;
import word.search.ui.dialogs.Modal;

public class Tutorial extends Group implements BackNavigator {

    private Actor actor;
    private Group actorParent;
    private int actorDepth;
    private Image arrow;
    private Group labelContainer;
    private int id;
    private Runnable callback;
    private Label label;
    protected Modal modal;
    private float actorOriginalX, actorOriginalY;
    protected BaseScreen screen;
    private Image textBg;

    public Tutorial(float width, float height, BaseScreen screen) {
        this.screen = screen;
        notifyNavigationController(screen);
        modal = new Modal(width, height, 0.6f);
        addActor(modal);
        getColor().a = 0;
        setSkipButton();
    }


    public void setId(int id){
        this.id = id;
    }


    public int getId(){
        return id;
    }


    public void setCloseCallback(Runnable callback){
        this.callback = callback;
    }


    public void show(){
        getColor().a = 0;
        addAction(Actions.fadeIn(0.3f));
        screen.modalOpened();
    }



    public void highlight(Actor actor){
        this.actor = actor;
        actorDepth = actor.getZIndex();
        actorParent = actor.getParent();
        actorOriginalX = actor.getX();
        actorOriginalY = actor.getY();

        Vector2 v2 = actor.localToActorCoordinates(this, new Vector2());
        addActor(actor);
        actor.setPosition(v2.x, v2.y);
    }




    public void arrow(float angle){
        arrow = new Image(AtlasRegions.arrow);
        arrow.setOrigin(Align.center);
        arrow.setRotation(angle);
        arrow.setTouchable(Touchable.disabled);
        arrow.getColor().a = 0f;
        addActor(arrow);
        arrow.addAction(Actions.fadeIn(0.2f));

        Vector2 p = actor.localToActorCoordinates(this, new Vector2());

        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;

        if(angle == 90f){
            x1 = p.x + (actor.getWidth() * actor.getScaleX() - arrow.getWidth()) * 0.5f;
            y1 = p.y - arrow.getHeight() * 2f;
        }else if(angle == 180f){
            x1 = p.x + actor.getWidth() + arrow.getWidth() * 0.5f;
            y1 = p.y + actor.getHeight() * 0.5f - arrow.getHeight() * 0.5f;
        }else if(angle == 0f){
            x1 = p.x - arrow.getWidth() * 1.5f;
            y1 = p.y + actor.getHeight() * 0.5f - arrow.getHeight() * 0.5f;
        }else if(angle == -90f){
            x1 = p.x + (actor.getWidth() - arrow.getWidth()) * 0.5f;
            y1 = p.y + actor.getHeight() + arrow.getHeight();
        }

        arrow.setX(x1);
        arrow.setY(y1);
        float radian = MathUtils.degreesToRadians * angle;
        float halfHeight = arrow.getHeight() * 0.5f;
        x2 = arrow.getX() + MathUtils.cos(radian) * halfHeight;
        y2 = arrow.getY() + MathUtils.sin(radian) * halfHeight;

        arrow.addAction(Actions.forever(Actions.sequence(Actions.moveTo(x2, y2, 0.5f, Interpolation.sineOut), Actions.moveTo(x1, y1, 0.5f, Interpolation.sineOut))));
    }



    public Image getArrow(){
        return arrow;
    }




    public void text(String msg, float width, BitmapFont font){
        if(labelContainer == null) {
            labelContainer = new Group();
            addActor(labelContainer);
            textBg = new Image(NinePatches.round_rect_shadow);
            textBg.setColor(ColorConfig.TUTORIAL_TEXT_BACKGROUND_COLOR);

            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = font;
            labelStyle.font.getData().markupEnabled = true;
            label = new Label(msg, labelStyle);
            label.setAlignment(Align.bottom);
            label.setWidth(width * 0.9f);
            label.setWrap(true);

            labelContainer.addActor(textBg);
            labelContainer.addActor(label);
        }else{
            label.setText(msg);
        }

        float extraHeight = 50;

        textBg.setSize(width, label.getPrefHeight() + extraHeight);
        labelContainer.setSize(textBg.getWidth(), textBg.getHeight());

        label.setX((labelContainer.getWidth() - label.getWidth()) * 0.5f);
        label.setY(extraHeight * 0.5f);
    }



    public void setTouchToDismiss(){
        if(labelContainer == null) throw new RuntimeException("First set labelContainer");
        labelContainer.addListener(inputListener);
    }



    private InputListener inputListener = new InputListener(){

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            close();
            removeListener(inputListener);
        }

    };




    public void close(){
        if(id != Constants.TUTORIAL_BONUS) DataManager.set(Constants.KEY_TUTORIAL_STEP, id);
        putBackActor();
        addAction(Actions.sequence(
                Actions.fadeOut(0.3f),
                Actions.run(callback)
        ));
        screen.modalClosed();
    }



    protected void putBackActor(){
        if(actor != null){
            actorParent.addActorAt(actorDepth, actor);
            actor.setPosition(actorOriginalX, actorOriginalY);
        }
    }



    public Group getLabelContainer(){
        return labelContainer;
    }


    private void setSkipButton(){
        TextButton.TextButtonStyle linkStyle = new TextButton.TextButtonStyle();
        linkStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
        linkStyle.fontColor = Color.WHITE;

        TextButton skip = new TextButton(Language.get("skip"), linkStyle);
        skip.getLabel().setFontScale(0.8f);
        skip.setX((screen.stage.getWidth() - skip.getPrefWidth()) * 0.5f);
        skip.setY(modal.getHeight() - skip.getPrefHeight() - 50);
        addActor(skip);
        skip.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                skip();
            }
        });
    }




    protected void skip(){
        DataManager.set(Constants.KEY_SKIPPED_TUTORIAL, true);
        close();
    }




    @Override
    public void notifyNavigationController(BaseScreen screen) {
        screen.backNavQueue.push(this);
    }




    @Override
    public boolean navigateBack() {
        skip();
        return true;
    }


}

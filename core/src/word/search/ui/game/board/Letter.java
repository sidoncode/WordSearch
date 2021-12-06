package word.search.ui.game.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool;

import word.search.GameController;
import word.search.app;
import word.search.config.ColorConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;

public class Letter extends Group implements Pool.Poolable {

    public Label label;
    private static Label.LabelStyle style;
    public static final Color mDefaultColor = Color.BLACK;
    public static final Color mSelectionColor = Color.WHITE;
    public char c;
    private boolean animated;
    private boolean solved;
    public boolean thrown;
    public boolean winded;
    public int index;
    public float originalX, originalY;
    private Image board_hint_bg;
    private float throwX, throwY;


    public Letter(){

    }


    public void init(GameController gameController){
        if(label == null) {
            style = new Label.LabelStyle();
            style.font = gameController.gameScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldBoard, BitmapFont.class);
            style.fontColor = Letter.mDefaultColor;
            label = new Label("T", style);
            addActor(label);
        }
    }



    public void setSolved(){
        solved = true;
    }



    public boolean isSolved(){
        return solved;
    }


    public void setChar(char c){
        this.c = c;
        label.setText(String.valueOf(c));
        label.setFontScale(getHeight() * 0.6f / label.getHeight());
        label.setX((getWidth() - label.getPrefWidth()) * 0.5f);
        label.setY((getHeight() - label.getHeight()) * 0.5f);
    }



    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        originalX = x;
        originalY = y;
    }



    public void setLabelColor(Color color){
        label.getStyle().fontColor = color;
    }







    public void animateFirstLetter(){
        if(animated) return;
        animated = true;

        addAction(Actions.sequence(
                Actions.scaleTo(1.2f, 1.2f, 0.15f),
                Actions.scaleTo(1f, 1f, 0.15f)
        ));

    }





    public void resetAnimated(){
        animated = false;
    }




    public void throwLabel() {

        getThrowDir();

        float halfHeight = getHeight() * 0.5f;
        float dst = getParent().getHeight() * 2f;

        float x1 = throwX != 0 ? halfHeight * throwX * -1 : 0;
        float y1 = throwY != 0 ? halfHeight * throwY * -1 : 0;

        float x2 = throwX != 0 ? dst * throwX : 0;
        float y2 = throwY != 0 ? dst * throwY : 0;

        addAction(Actions.rotateBy(MathUtils.randomBoolean() ? MathUtils.random(180) : -MathUtils.random(180), 0.8f));

        addAction(Actions.sequence(
                Actions.moveBy(x1, y1, 0.3f, Interpolation.sineOut),
                Actions.moveBy(x2, y2, 0.6f, Interpolation.sineIn),
                Actions.run(throwFinished)
        ));


    }


    private Runnable throwFinished = new Runnable() {
        @Override
        public void run() {
            setVisible(false);
        }
    };



    private void getThrowDir(){
        int angle = Math.round(getRotation()) % 360;

        if(angle == -90){
            throwX = -1;
            throwY = 0;
        }else if(angle == -180){
            throwX = 0;
            throwY = 1;
        }else if(angle == -270){
            throwX = 1;
            throwY = 0;
        }else if(angle == 0){
            throwX = 0;
            throwY = -1;
        }

    }





    public void revealHint(){
        if(board_hint_bg == null) {
            board_hint_bg = new Image(AtlasRegions.hint_bg);
            board_hint_bg.setColor(ColorConfig.BOARD_REVEAL_DISC_COLOR);
            board_hint_bg.setScale(getWidth() * 0.7f / board_hint_bg.getWidth());
            board_hint_bg.setX((getWidth() - board_hint_bg.getWidth() * board_hint_bg.getScaleX()) * 0.5f);
            board_hint_bg.setY((getHeight() - board_hint_bg.getHeight() * board_hint_bg.getScaleY()) * 0.5f);
            addActorBefore(label, board_hint_bg);

            board_hint_bg.addAction(Actions.forever(Actions.sequence(
                    Actions.fadeIn(0.7f, Interpolation.sineOut),
                    Actions.fadeOut(0.7f, Interpolation.sineOut)
            )));
        }

    }


    public boolean hasHint(){
        return board_hint_bg != null;
    }





    public void removeHint(){
        if(board_hint_bg != null){
            board_hint_bg.clearActions();
            board_hint_bg.remove();
            board_hint_bg = null;
        }
    }






    public void rotate(float time){
        addAction(Actions.rotateBy(-90, time));
    }





    @Override
    public void reset() {
        animated = false;
        solved = false;
        thrown = false;
        winded = false;

        getColor().a = 1;
        label.getColor().a = 1;
        setRotation(0);
        setScale(1);
        setVisible(true);
        setLabelColor(mDefaultColor);
        removeHint();
    }


}

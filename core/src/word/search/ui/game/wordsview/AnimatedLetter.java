package word.search.ui.game.wordsview;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import word.search.actions.BezierToAction;
import word.search.app;

//The letters that animate from baord to wordsview
public class AnimatedLetter extends Group implements Pool.Poolable {

    private Label label;

    public float srcX, srcY, dstX, dstY;
    public float dstScale;
    private Vector2 vector2;

    public AnimatedLetter() {

    }


    public void init(String text, Label.LabelStyle labelStyle, float fontScale){
        if(label == null){
            label = new Label(text, labelStyle);
            addActor(label);
        }else{
            label.setText(text);
            label.setStyle(labelStyle);
        }
        label.setFontScale(fontScale);
        label.setOrigin(Align.bottomLeft);

        setSize(label.getWidth(), label.getHeight());
    }



    public Vector2 getVector2(){
        if(vector2 == null) {
            vector2 = new Vector2();
        }else{
            vector2.x = 0;
            vector2.y = 0;
        }
        return vector2;
    }



    private BezierToAction bezierToAction;
    private Runnable callback;


    public void moveToBonusButton(float startX, float startY, float p1x, float p1y, float p2x, float p2y, float endX, float endY, float scale, Runnable callback){
        this.callback = callback;
        float time = 0.3f;

        bezierToAction = Pools.obtain(BezierToAction.class);
        bezierToAction.setDuration(time);
        bezierToAction.setStartPosition(startX, startY);
        bezierToAction.setPointA(p1x, p1y);
        bezierToAction.setPointB(p2x, p2y);
        bezierToAction.setEndPosition(endX, endY);
        bezierToAction.setInterpolation(Interpolation.slowFast);

        addAction(Actions.sequence(
                Actions.parallel(
                        bezierToAction,
                        Actions.scaleTo(scale, scale, time, Interpolation.slowFast)
                ),
                Actions.run(bonusAnimEnded)
        ));
    }




    private Runnable bonusAnimEnded = new Runnable() {

        @Override
        public void run() {
            if(callback != null) callback.run();
            Pools.free(bezierToAction);
            remove();
            Pools.free(this);

        }

    };




    @Override
    public void reset() {
        setScale(1f);
    }

}

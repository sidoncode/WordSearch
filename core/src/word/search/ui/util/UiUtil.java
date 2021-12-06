package word.search.ui.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Pools;

import word.search.actions.Interpolation;
import word.search.app;


public class UiUtil {


    public static void shake(Actor actor, boolean horizontal, float amount, Runnable callback){
        Shaker shaker = Pools.obtain(Shaker.class);
        shaker.shake(actor, horizontal, amount, callback);
    }



    public static void pulsate(Actor actor){
        SequenceAction sequenceAction = new SequenceAction(
                Actions.scaleTo(0.95f, 0.95f, 1),
                Actions.scaleTo(1,1, 1)
        );
        actor.addAction(Actions.forever(sequenceAction));
    }

    
}

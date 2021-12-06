package word.search.ui.util;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;


public class Shaker implements Pool.Poolable {

    private Runnable callback;


    public void shake(Actor actor, boolean horizontal, float amount, Runnable callback){
        this.callback = callback;
        float x = actor.getX();
        float y = actor.getY();

        SequenceAction sequence;

        if(horizontal) {
            sequence = Actions.sequence(
                    Actions.moveTo(x - amount * 0.5f, y, 0.1f),
                    Actions.moveTo(x + amount * 0.5f, y, 0.1f),
                    Actions.moveTo(x - amount * 0.33f, y, 0.07f),
                    Actions.moveTo(x + amount * 0.33f, y, 0.07f),
                    Actions.moveTo(x - amount * 0.25f, y, 0.04f),
                    Actions.moveTo(x + amount * 0.25f, y, 0.04f),
                    Actions.moveTo(x, y, 0.02f)
            );
        }else{
            sequence = Actions.sequence(
                    Actions.moveTo(x, y - amount * 0.5f, 0.1f),
                    Actions.moveTo(x, y + amount * 0.5f, 0.1f),
                    Actions.moveTo(x, y - amount * 0.33f, 0.07f),
                    Actions.moveTo(x, y + amount * 0.33f, 0.07f),
                    Actions.moveTo(x, y - amount * 0.25f, 0.04f),
                    Actions.moveTo(x, y + amount * 0.25f, 0.04f),
                    Actions.moveTo(x, y, 0.02f)
            );
        }

        if(callback != null) sequence.addAction(Actions.run(finished));
        actor.addAction(sequence);
    }




    private Runnable finished = new Runnable() {
        @Override
        public void run() {
            Pools.free(Shaker.this);
            if(callback != null) callback.run();
        }
    };




    @Override
    public void reset() {

    }


}

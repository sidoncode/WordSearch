package word.search.ui.game.wordsview;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pool;

public class PoolableGroup extends Group implements Pool.Poolable {

    @Override
    public void reset() {
        setSize(0, 0);
        setPosition(0, 0);
    }
}

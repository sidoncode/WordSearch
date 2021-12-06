package word.search.ui.game.board;

import com.badlogic.gdx.utils.Pool;

public class HintData implements Pool.Poolable {

    public int totalHints;
    public int usedCount;
    public int solvedCount;
    public int consumed;


    @Override
    public void reset() {
        totalHints = 0;
        usedCount = 0;
        solvedCount = 0;
        consumed = 0;
    }
}

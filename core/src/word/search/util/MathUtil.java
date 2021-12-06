package word.search.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MathUtil {


    public static float scaleNumber(float Input, float InputLow, float InputHigh, float OutputLow, float OutputHigh){
        return ((Input - InputLow) / (InputHigh - InputLow)) * (OutputHigh - OutputLow) + OutputLow;
    }

}

package word.search.ui.game.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class DarkeningTextButton extends TextButton {

    private float time;

    public DarkeningTextButton(String text, TextButtonStyle style) {
        super(text, style);
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        if(isPressed()){
            time += Gdx.graphics.getDeltaTime();
            if(time <= 0.1f){
                float value = MathUtils.lerp(1f, 0.95f, time * 10);
                getColor().r = value;
                getColor().g = value;
                getColor().b = value;
            }
        }else {
            time = 0;
            getColor().r = 1;
            getColor().g = 1;
            getColor().b = 1;
        }

        super.draw(batch, parentAlpha);
        batch.setColor(color.r, color.g, color.b, 1);
    }


}

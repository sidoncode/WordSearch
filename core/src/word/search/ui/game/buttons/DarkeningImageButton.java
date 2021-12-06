package word.search.ui.game.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class DarkeningImageButton extends ImageButton {

    private float time;
    private boolean hasStyle;
    private float r = 1, g = 1, b = 1, a = 1;

    public DarkeningImageButton(Drawable imageUp) {
        super(imageUp);
        hasStyle = false;
    }



    public DarkeningImageButton(ImageButtonStyle style) {
        super(style);
        hasStyle = true;
    }


    public void setUpColor(float r, float g, float b){
        this.r = r;
        this.g = g;
        this.b = b;
    }


    public void setUpColor(float r, float g, float b, float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(isPressed()){
            time += Gdx.graphics.getDeltaTime();
            if(time <= 0.1f){
                float value = MathUtils.lerp(1f, 0.8f, time * 10);

                if(hasStyle) setColor(r * value, g * value, b * value, a);
                else getImage().setColor(r * value, g * value, b * value, a);
            }
        }else{
            time = 0;
            if(hasStyle) setColor(r, g, b, a);
            else getImage().setColor(r, g, b, a);
        }
    }

}

package word.search.ui.hud.game_hud;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.search.app;
import word.search.graphics.AtlasRegions;
import word.search.ui.game.buttons.DarkeningImageButton;

public class RevealButton extends DarkeningImageButton {

    private Image costImg;
    private Label label;
    private Image padlock;


    public RevealButton(TextureAtlas.AtlasRegion imageUp, TextureAtlas.AtlasRegion cost, Label.LabelStyle labelStyle) {
        super(new TextureRegionDrawable(imageUp));

        if(cost != null) {
            costImg = new Image(cost);
            costImg.setX((getWidth() - costImg.getWidth()) * 0.5f);
            costImg.setY(17);
            costImg.setVisible(false);
            addActor(costImg);
        }

        if(labelStyle != null) {
            label = new Label(" ", labelStyle);
            label.setFontScale(0.65f);
            label.setY(-3);
            label.setVisible(false);
            addActor(label);
        }
    }




    public void update(int quantity){
        if(padlock != null) return;
        if(quantity ==  0){
            label.setVisible(false);
            costImg.setVisible(true);
        }else{
            costImg.setVisible(false);
            label.setText(quantity);

            GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
            glyphLayout.setText(label.getStyle().font, label.getText());
            label.setX((getWidth() - glyphLayout.width * label.getFontScaleX()) * 0.5f);

            Pools.free(glyphLayout);
            label.setVisible(true);
        }
    }




    public void indicate(){
        setY(0f);
        setScale(1f);
        if(padlock != null) return;

        setTransform(true);
        setOrigin(Align.center);
        float time = 0.2f;
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(MathUtils.random(5f, 10f)),
            Actions.scaleTo(1.1f, 0.9f, time, Interpolation.fastSlow),
            Actions.scaleTo(0.8f, 1.2f, time, Interpolation.slowFast),
            Actions.moveBy(0,  + 40, time, Interpolation.slowFast),
            Actions.scaleTo(1f, 1f, time * 0.5f, Interpolation.slowFast),
            Actions.moveBy(0,  - 40, time * 0.7f, Interpolation.slowFast),
            Actions.scaleTo(1.1f, 0.9f, time * 0.7f, Interpolation.fastSlow),
            Actions.scaleTo(1, 1, time * 0.7f, Interpolation.slowFast)
        )));
    }


    public void stopIndicating(){
        clearActions();
    }




    public void lock(){
        if(costImg != null) costImg.setVisible(false);
        if(label != null) label.setVisible(false);

        if(padlock == null){
            padlock = new Image(AtlasRegions.padlock);
            padlock.setX((getWidth() - padlock.getWidth()) * 0.5f);
            padlock.setY(-padlock.getHeight() * 0.5f);
            addActor(padlock);
        }
    }



    public void unlock(){
        if(padlock != null){
            padlock.remove();
            padlock = null;
        }
    }


}

package word.search.ui.game.wordsview;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.search.app;
import word.search.config.ColorConfig;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;


//The stripe that appears behind the correct answer letters in wordsview
public class WordBg extends PoolableGroup {

    private Label label;
    private Image bg;
    public String text;
    private float maxBgWidth;

    public WordBg() {
        bg = new Image(NinePatches.word_bg);
        bg.setColor(ColorConfig.SOLVED_WORD_BACKGROUND_COLOR);
        addActor(bg);
    }


    @Override
    public void reset() {
        bg.setScale(1);
    }


   public void setBgScale(float scale){
        bg.setScale(scale);
   }


   public void setBgWidth(float width){
        bg.setWidth( width);

   }
   public void setMaxBgWidth(float width){
        maxBgWidth = width;

   }


    public void setText(String text, ResourceManager resourceManager){
        this.text = text;
        if(label == null){
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
            label = new Label(text, labelStyle);
            addActor(label);
        }else{
            label.setText(text);
        }

        label.setFontScale((bg.getHeight() * bg.getScaleY() * 0.83f / label.getHeight()));
        label.setAlignment(Align.bottomLeft);

        float w = Math.min(maxBgWidth, label.getPrefWidth() + 80);

        setBgWidth(w * (1 / bg.getScaleX()));
        setWidth(bg.getWidth() * bg.getScaleX());
        setHeight(bg.getHeight() * bg.getScaleY());
        label.setX((bg.getWidth() * bg.getScaleX() - label.getPrefWidth()) * 0.5f);
        label.setY((bg.getHeight() * bg.getScaleY() - label.getHeight() * label.getFontScaleY()) * 0.5f - 1f);
    }



    public void fadeIn(){
        addAction(Actions.fadeIn(0.25f));
    }



    public void growAndShrink(){
        addAction(Actions.sequence(
                Actions.scaleTo(1.2f, 1.2f, 0.3f, Interpolation.fastSlow),
                Actions.scaleTo(1f, 1f, 0.2f, Interpolation.slowFast)
        ));

    }


}

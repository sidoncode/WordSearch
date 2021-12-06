package word.search.ui.game.wordsview;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool;

import word.search.config.ColorConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.ResourceManager;


//The disc image that represents a letter in wordsview
public class PlaceHolder extends Image implements Pool.Poolable {

    public boolean hinted;
    public int index;
    public Label label;
    private static Label.LabelStyle labelStyle;
    public char c;



    public PlaceHolder() {
        super(AtlasRegions.letter_holder);
    }




    public Label getLabel(ResourceManager resourceManager){
        if(labelStyle == null){
            labelStyle = new Label.LabelStyle();
            labelStyle.font = resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
            labelStyle.fontColor = ColorConfig.WORDS_VIEW_LETTER_COLOR;
        }

        if(label == null){
            label = new Label(String.valueOf(c), labelStyle);
            label.setOrigin(Align.bottom);
            label.setAlignment(Align.bottom);
            label.setFontScale(getWidth() * getScaleX() * 0.9f / label.getHeight());
        }else{
            label.setText(String.valueOf(c));
        }

        label.setX(getX() + (getWidth() * getScaleX() - label.getWidth()) * 0.5f);
        label.setY(getY() + (getHeight() * getScaleY() - label.getHeight() * label.getFontScaleY()) * 0.5f);
        return label;
    }




    @Override
    public void reset() {
        hinted = false;
        if(label != null){
            label.remove();
        }
    }


}

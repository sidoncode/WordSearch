package word.search.ui.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import word.search.WordGame;
import word.search.config.ColorConfig;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;
import word.search.ui.util.UiUtil;

public class Preview extends Group {

    private Image bg;
    private Label label;
    private float padding;


    public Preview(WordGame modernWordSearchGame){
        NinePatch ninePatch = NinePatches.preview;
        padding = ninePatch.getLeftWidth() * 0.9f * 2f;

        bg = new Image(ninePatch);
        bg.setWidth(padding * 2);
        bg.setColor(ColorConfig.WORD_PREVIEW_COLOR);
        addActor(bg);
        setHeight(bg.getHeight());

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = modernWordSearchGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        label = new Label(" ", style);
        label.setAlignment(Align.bottomLeft);
        label.setY(10f);
        addActor(label);
        getColor().a = 0;
    }




    public void setAnimatedText(String text){
        if(text.isEmpty()){
            return;
        }

        label.setText(text);
        bg.setWidth(label.getPrefWidth() + padding);

        setWidth(bg.getWidth());
        bg.setX((getWidth() - bg.getWidth()) * 0.5f);
        label.setX((bg.getWidth() - label.getPrefWidth()) * 0.5f);
        setX((getStage().getWidth() - getWidth()) * 0.5f);
        if(getColor().a == 0f){
            addAction(Actions.fadeIn(0.15f));
        }
    }


    public void hide(){
        if(hider != null) hider.run();
    }




    private Runnable hider = new Runnable() {
        @Override
        public void run() {
            if(getColor().a > 0f){
                addAction(Actions.fadeOut(0.15f));
            }
        }
    };



    public void shake(){
        UiUtil.shake(this, true,padding * 2, hider);
    }


}

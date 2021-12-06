package word.search.ui.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import word.search.config.ColorConfig;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;
import word.search.model.Language;
import word.search.screens.BaseScreen;

public class Feedback extends Group {

    private Image bg;
    private List<String> feedbackList;
    private Label label;
    private int fbIndex;
    private Runnable callback;
    private Label.LabelStyle labelStyle;


    public Feedback(BaseScreen screen){
        setVisible(false);

        NinePatch ninePatch = NinePatches.feedback_ribbon;
        bg = new Image(ninePatch);
        addActor(bg);

        setSize(bg.getWidth(), bg.getHeight());
        setOrigin(Align.center);
        setScale(0);

        String[] feedbacks = Language.get("feedback").split(",");
        feedbackList = Arrays.asList(feedbacks);
        Collections.shuffle(feedbackList);

        String font = ResourceManager.fontSignikaBoldShadow;
        labelStyle = new Label.LabelStyle(screen.wordGame.resourceManager.get(font, BitmapFont.class), ColorConfig.FEEDBACK_RIBBON_TEXT_COLOR);

        label = new Label(feedbackList.get(0).trim(), labelStyle);
        label.setOrigin(Align.bottom);
        label.setFontScale(1.0f);
        addActor(label);

        setTouchable(Touchable.disabled);
    }




    public void show(Color color, String text){
        bg.setColor(color);
        label.setText(text);
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(labelStyle.font, label.getText());

        bg.setWidth(NinePatches.feedback_ribbon.getLeftWidth() + NinePatches.feedback_ribbon.getRightWidth() + glyphLayout.width * 1.2f);
        setWidth(bg.getWidth());
        setOrigin(Align.center);
        setX((getStage().getWidth() - getWidth()) * 0.5f);

        label.setX((getWidth() - glyphLayout.width * label.getFontScaleX()) * 0.5f);
        label.setY((getHeight() - label.getPrefHeight() * label.getFontScaleY()) * 0.4f);
        Pools.free(glyphLayout);

        setScale(0.5f);
        getColor().a = 1;
        setVisible(true);

        addAction(Actions.sequence(
                Actions.scaleTo(1.05f,1.05f,0.3f, Interpolation.fastSlow),
                Actions.scaleTo(1,1,0.1f, Interpolation.slowFast),
                Actions.fadeOut(0.5f),
                Actions.run(end)


        ));
    }



    private Runnable end = new Runnable() {

        @Override
        public void run() {
            if(callback != null) callback.run();
           setVisible(false);
        }
    };




    public void show(Color color, Runnable callback){
        this.callback = callback;
        show(color, feedbackList.get(fbIndex).trim());

        fbIndex++;
        fbIndex %= feedbackList.size();
    }


}

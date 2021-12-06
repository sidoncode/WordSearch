package word.search.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.search.app;
import word.search.config.ColorConfig;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;

public class Toast extends Group {


    private Label label;



    public Toast(ResourceManager resourceManager, float width){

        Image bg = new Image(NinePatches.rect);
        bg.setColor(ColorConfig.TOAST_BACKGROUND_COLOR);
        addActor(bg);

        String font = ResourceManager.fontSignikaBoldShadow;
        Label.LabelStyle labelStyle = new Label.LabelStyle(resourceManager.get(font, BitmapFont.class), ColorConfig.TOAST_FONT_COLOR);

        label = new Label(" ", labelStyle);
        addActor(label);

        bg.setWidth(width);
        bg.setHeight(label.getHeight() * 2f);
        setSize(bg.getWidth(), bg.getHeight());
        setOrigin(Align.center);

        setTouchable(Touchable.disabled);
    }



    public void show(String msg){
        clearActions();
        setScaleX(0);
        getColor().a = 1f;
        label.setText(msg);

        GlyphLayout layout = Pools.obtain(GlyphLayout.class);
        layout.setText(label.getStyle().font, msg);

        float maxWidth = 0.9f;

        if(layout.width > getWidth() * maxWidth){
            label.setFontScale(getWidth() * maxWidth / layout.width);
        }
        label.setX((getWidth() - layout.width * label.getFontScaleX()) * 0.5f);
        label.setY((getHeight() - label.getPrefHeight() * label.getFontScaleY()) * 0.5f);
        Pools.free(layout);
        label.getColor().a = 0;

        float time = 0.3f;

        addAction(Actions.sequence(
                Actions.scaleTo(1, 1, time, Interpolation.fastSlow),
                Actions.run(fader),
                Actions.delay(1.5f),
                Actions.run(labelFader),
                Actions.scaleTo(0, 1, time, Interpolation.slowFast),
                Actions.run(hider)
        ));
    }



    private Runnable labelFader = new Runnable() {
        @Override
        public void run() {
            label.addAction(Actions.fadeOut(0.1f));
        }
    };


    private Runnable fader = new Runnable() {
        @Override
        public void run() {
            label.addAction(Actions.fadeIn(0.2f));
        }
    };



    private Runnable hider = new Runnable() {
        @Override
        public void run() {
            remove();
            setVisible(false);
        }
    };


}

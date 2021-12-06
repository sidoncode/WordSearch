package word.search.ui.game.category;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.search.actions.Interpolation;
import word.search.config.ColorConfig;
import word.search.config.ConfigProcessor;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.screens.GameScreen;


public class CategoryRibbon extends Group{

    public static final float ANIM_TIME_1 = 0.5f;
    public static final float ANIM_TIME_2 = 1f;
    public static final float ANIM_TIME_3 = 0.5f;
    public static float ANIM_MOVE_DST = 100f;
    public static float POSITION_Y;

    private Image ribbon;
    private Label label;
    private float margin;
    private GameScreen gameScreen;

    public CategoryRibbon(GameScreen gameScreen){
        this.gameScreen = gameScreen;
        NinePatch ninePatch = NinePatches.word_cat_ribbon;
        ribbon = new Image(ninePatch);
        ribbon.setColor(ColorConfig.CATEGORY_RIBBON_COLOR);
        addActor(ribbon);
        setHeight(ribbon.getHeight());
        margin = ninePatch.getLeftWidth();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = gameScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        label = new Label(" ", labelStyle);
        label.setY((getHeight() - label.getHeight()) * 0.8f);
        addActor(label);
    }



    public void setCategory(String text){
        label.setFontScale(1f);
        label.setText(text);
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(label.getStyle().font, text);
        float maxWidth = Constants.GAME_CONTENT_WIDTH - margin * 2f;

        if(glyphLayout.width > maxWidth){
            label.setFontScale(maxWidth / glyphLayout.width);
        }

        ribbon.setWidth(Math.min(glyphLayout.width + margin * 2f, Constants.GAME_CONTENT_WIDTH));
        Pools.free(glyphLayout);

        setWidth(ribbon.getWidth());
        label.setX((getWidth() - label.getPrefWidth() ) * 0.5f);

        setX((gameScreen.stage.getWidth() - getWidth()) * 0.5f);
        setOrigin(Align.center);
    }



    public void resize(){
        POSITION_Y = gameScreen.stage.getHeight() * 0.8385f;
        ANIM_MOVE_DST = gameScreen.stage.getHeight() * 0.052f;
        setX((gameScreen.stage.getWidth() - getWidth()) * 0.5f);
        setY(POSITION_Y);
    }



    public void show(boolean in){
        if(!in){
            hide();
            return;
        }

        setScale(0.1f);
        float maxScale = (gameScreen.stage.getWidth() - 40f) / getWidth();

        if(!ConfigProcessor.mutedSfx) {
            Sound sound = gameScreen.wordGame.resourceManager.get(ResourceManager.level_start, Sound.class);
            sound.play();
        }

        addAction(Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(Math.min(1.5f, maxScale), Math.min(1.5f, maxScale), ANIM_TIME_1, Interpolation.backOut),
                        Actions.fadeIn(ANIM_TIME_1)
                ),
                Actions.delay(ANIM_TIME_2),
                Actions.parallel(
                        Actions.moveTo(getX(), POSITION_Y, ANIM_TIME_3, com.badlogic.gdx.math.Interpolation.fastSlow),
                        Actions.scaleTo(1f, 1f, ANIM_TIME_3, com.badlogic.gdx.math.Interpolation.fastSlow)
                )
        ));
    }



    private void hide(){
        addAction(
                Actions.parallel(
                        Actions.fadeOut(ANIM_TIME_3, com.badlogic.gdx.math.Interpolation.fastSlow),
                        Actions.moveTo(getX(), POSITION_Y + CategoryRibbon.ANIM_MOVE_DST, ANIM_TIME_3, com.badlogic.gdx.math.Interpolation.fastSlow)
                )
        );
    }


}

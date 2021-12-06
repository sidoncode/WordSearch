package word.search.ui.hud.game_hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pools;

import word.search.config.ColorConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.ResourceManager;
import word.search.model.Language;

public class LevelBoard extends Group {


    private Label levelLabel;

    public LevelBoard(ResourceManager resourceManager){
        Image bg = new Image(AtlasRegions.level_board);
        setSize(bg.getWidth(), bg.getHeight());
        bg.setColor(ColorConfig.LEVEL_BOARD_BACKGROUND_COLOR);
        addActor(bg);

        Label.LabelStyle levelStyle = new Label.LabelStyle(resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class), new Color(0xfdf9efff));
        levelLabel = new Label(" ", levelStyle);
        addActor(levelLabel);
    }



    public void setLevel(int level){
        levelLabel.setText(Language.format("level", level));
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(levelLabel.getStyle().font, levelLabel.getText());
        float maxWidth = getWidth() * 0.8f;
        if(glyphLayout.width > maxWidth) levelLabel.setFontScale(maxWidth / glyphLayout.width);

        levelLabel.setX((getWidth() - glyphLayout.width * levelLabel.getFontScaleX()) * 0.5f);
        levelLabel.setY((getHeight() - levelLabel.getHeight()) * 0.6f);
        Pools.free(glyphLayout);
    }


}

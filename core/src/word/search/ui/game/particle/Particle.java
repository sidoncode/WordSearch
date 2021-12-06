package word.search.ui.game.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Pool;

public class Particle extends Sprite implements Pool.Poolable {

    public float x, y               = 0;
    public float angle              = 0;
    public float rotation           = 0;
    public float vx, vy             = 0;
    public float radius             = 1;
    public float speed              = 1;
    public float friction           = 1;
    public float opacity            = 1;
    public float gravity            = 1;
    public float dst                = 1;
    public float startX, startY     = 0;
    public float wander             = 0;
    public float theta              = 0;
    public float drag               = 0;
    public Color color;
    public boolean alive;
    public boolean flag;


    public Particle(TextureAtlas.AtlasRegion sparkle) {
        super(sparkle);
    }




    @Override
    public void reset() {
        x = 0;
        y = 0;
        angle = 0;
        rotation = 0;
        vx = 0;
        vy = 0;
        radius = 1;
        speed = 1;

        opacity = 1;
        gravity = 1;
        dst = 1;
        startX = 0;
        startY = 0;

        setAlpha(1f);
        setScale(1f);
    }

}

package word.search.ui.game.board;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import word.search.graphics.AtlasRegions;
import word.search.ui.game.particle.MagicParticle;
import word.search.ui.game.particle.Particle;

public class MagicWand extends Image {


    private Array<Particle> particles;
    private int count = 30;
    private boolean sparkle;
    private float maxDst;

    MagicWand(){
        super(AtlasRegions.magic_wand);
        particles = new Array<>();
        maxDst = 200;
    }




    private void createParticle(){
        MagicParticle p = new MagicParticle();
        particles.add(p);
    }



    public void startSparkle(){
        sparkle = true;
    }



    public void stopSparkle(){
        sparkle = false;
        for(Particle particle : particles) particle = null;
        particles.clear();
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(sparkle){
            if(particles.size < count) createParticle();

            for(int i = 0; i < particles.size; i++){
                Particle sparkle = particles.get(i);

                float centerX = getX() + (getWidth() - sparkle.getWidth()) * 0.5f;
                float centerY = getY() + getHeight() * 0.67f - sparkle.getHeight() * 0.5f;

                float head = getHeight() * 0.3f;
                float x = centerX + MathUtils.cos(MathUtils.random(6.28f)) * head;
                float y = centerY + MathUtils.sin(MathUtils.random(6.28f)) * head;

                if(sparkle.getScaleX() == 1.0f) sparkle.setPosition(x, y);
                sparkle.draw(batch);
                sparkle.setScale(sparkle.getScaleX() - 0.02f);

                if(sparkle.getScaleX() <= 0){
                    sparkle.setScale(1);
                }
            }
        }
        super.draw(batch, parentAlpha);
    }


}

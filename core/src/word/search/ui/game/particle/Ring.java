package word.search.ui.game.particle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import word.search.actions.Interpolation;
import word.search.graphics.AtlasRegions;

public class Ring extends Image implements Pool.Poolable{

    private Actor target;
    private RingCallback callback;

    public Ring(){
        super(AtlasRegions.place_holder_ring);
    }



    public void setTarget(Actor target, RingCallback callback){
        this.target = target;
        this.callback = callback;
        setOrigin(Align.center);

        float scale = (target.getHeight() * target.getScaleY() * 3.0f) / getHeight();
        setScale(scale);

        setX(target.getX() - (getWidth() - target.getWidth() * target.getScaleX()) * 0.5f);
        setY(target.getY() - (getHeight() - target.getHeight() * target.getScaleY()) * 0.5f);

        maxDst = target.getWidth() * target.getScaleX() * 1.5f;
    }



    public void animate(){
        float scale = target.getHeight() / getHeight();
        addAction(Actions.sequence(
                Actions.scaleTo(scale, scale, 0.5f, Interpolation.cubicIn),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        callback.part1Finished(target);
                        getColor().a = 0;
                        doSparkle();
                    }
                })
        ));
    }



    private Array<Particle> particles = new Array<>();
    private float maxDst;


    private void doSparkle(){
        for(int i = 0; i < 50; i++) createParticle();
    }



    private void createParticle(){
        SparkleParticleSmall p = Pools.obtain(SparkleParticleSmall.class);
        particles.add(p);
        resetParticle(p);
    }




    private void resetParticle(Particle particle){
        float TWO_PI = 6.283185f;
        float angle = MathUtils.random() * TWO_PI;
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);

        particle.angle = angle * MathUtils.radiansToDegrees;
        particle.x =  particle.startX = target.getX() +  (target.getWidth() * target.getScaleX() - particle.getWidth()) * 0.5f + ( target.getWidth() * target.getScaleX()-particle.getWidth()) * cos * 0.5f;
        particle.y =  particle.startY = target.getY() +  (target.getHeight() * target.getScaleY()  - particle.getHeight()) * 0.5f + (target.getHeight() * target.getScaleY() -particle.getHeight()) * sin * 0.5f;
        particle.radius = MathUtils.random(0.8f, 1f);
        particle.rotation = MathUtils.random() * 360.0f;
        particle.friction = 0.97f;
        particle.dst = 0;
        if(MathUtils.randomBoolean()) particle.setColor(1f,0.96f,0.79f, 0.7f);
        else particle.setColor(0.96f,0.86f,0.36f, 0.7f);

        float speed = MathUtils.random(2f, 7f);
        particle.vx = cos * speed;
        particle.vy = sin * speed;
    }




    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        for(int i = 0; i < particles.size; i++){
            Particle p = particles.get(i);
            p.x += p.vx;
            p.y += p.vy;
            p.dst = (float)Math.sqrt((p.x - p.startX) * (p.x - p.startX) + (p.y - p.startY) * (p.y - p.startY));
            p.speed *= p.friction;
            p.radius *= 0.99f;
            p.setPosition(p.x, p.y);
            p.setScale(p.radius);
            p.setRotation(p.angle);
            p.opacity -= 0.01f;
            p.setAlpha(p.opacity);

            p.draw(batch);

           if(p.dst > maxDst){
                particles.removeValue(p, true);
                Pools.free(p);
                if(particles.size == 0){
                    Pools.free(this);
                }
            }
        }
    }




    @Override
    public void reset() {
        remove();
        setScale(1);
        getColor().a = 1f;
    }



    public interface RingCallback{
        void part1Finished(Actor target);
        void part2Finished(Actor target);
    }



}

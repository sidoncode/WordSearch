package word.search.ui.game.wordsview;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import word.search.app;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.ui.game.particle.SparkleParticleSmall;
import word.search.ui.hud.CoinView;

public class PlaceHolderCoin extends Image implements Pool.Poolable {

    private CoinView coinView;
    private boolean first, last;
    private Runnable callback, firstCallback;
    private ResourceManager resourceManager;


    public PlaceHolderCoin() {
        super(AtlasRegions.placeholder_coin);
    }


    public void animate(float delay, float x, float y, CoinView coinView, boolean first, boolean last, Runnable callback, Runnable callback2, ResourceManager resourceManager){
        firstCallback = callback2;
        this.resourceManager = resourceManager;
        animate(delay, x, y, coinView, first, last, callback);
    }



    public void animate(float delay, float x, float y, CoinView coinView, boolean first, boolean last, Runnable callback, ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        animate(delay, x, y, coinView, first, last, callback);
    }



    public void animate(float delay, float x, float y, CoinView coinView, boolean first, boolean last, Runnable callback){
        this.coinView = coinView;
        this.first = first;
        this.last = last;
        this.callback = callback;

        addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.moveTo(x, y, 0.6f, Interpolation.slowFast),
                Actions.run(animFinished)
        ));
    }




    private Runnable animFinished = new Runnable() {

        @Override
        public void run() {
            remove();
            Pools.free(this);

            int startFrom = DataManager.get(Constants.KEY_COIN_COUNT, GameConfig.DEFAULT_COIN_COUNT);
            coinView.incrementCoinLabelWithAnimationAndDeleteCoinImages(startFrom, 1, null);
            DataManager.set(Constants.KEY_COIN_COUNT, startFrom + 1);

            if(first) {
                coinView.startSparkleAnimation();
                if(firstCallback != null) firstCallback.run();

                if(resourceManager != null && !ConfigProcessor.mutedSfx){
                    Sound sound = resourceManager.get(ResourceManager.coin_add, Sound.class);
                    sound.play();
                }
            }

            if(last) {
                coinView.stopSparkleAnimation();

                if(resourceManager != null) {
                    Sound sound = resourceManager.get(ResourceManager.coin_add, Sound.class);
                    sound.stop();
                }
            }
            run = false;


            for(SparkleParticleSmall p : particles) Pools.free(p);
            particles.clear();
            if(callback != null) callback.run();
        }

    };




    @Override
    public void reset() {

    }


    private Array<SparkleParticleSmall> particles = new Array<>();

    public void setup(){
        SparkleParticleSmall particle = Pools.obtain(SparkleParticleSmall.class);
        resetParticle(particle);
        particles.add(particle);
    }



    private void resetParticle(SparkleParticleSmall p){
        float height = getHeight() * getScaleX();

        float cx = getX() + (height - p.getWidth()) * 0.5f;
        float cy = getY() + (height - p.getHeight()) * 0.5f;

        cx += MathUtils.cos(MathUtils.random(MathUtils.PI2)) * height * 0.3f;
        cy += MathUtils.sin(MathUtils.random(MathUtils.PI2)) * height * 0.3f;

        p.x = cx;
        p.y = cy;

        p.radius = 1f;
    }



    public boolean run;



    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(run && particles.size < 30) setup();

        for(SparkleParticleSmall p : particles){
            p.setPosition(p.x, p.y);
            p.radius *= 0.95f;
            p.setScale(p.radius);
            p.draw(batch);

            if (p.radius <= 0.1f) resetParticle(p);
        }
        super.draw(batch, parentAlpha);
    }


}

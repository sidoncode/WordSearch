package word.search.ui.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import word.search.app;
import word.search.config.GameConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.screens.BaseScreen;
import word.search.ui.game.buttons.DarkeningImageButton;
import word.search.ui.game.particle.Particle;
import word.search.ui.game.particle.SparkleParticleSmall;


public class CoinView extends Group {


    public Image coin;
    private Label label;
    private float maxWidth;
    public BaseScreen screen;
    private CoinCountInrementer coinCountInrementer;
    public DarkeningImageButton plus;



    public CoinView(BaseScreen screen){
        this.screen = screen;

        Image bg = new Image(AtlasRegions.coin_view_bg);
        addActor(bg);
        setSize(bg.getWidth(), bg.getHeight());

        coin = new Image(AtlasRegions.coin_view_coin);
        coin.setOrigin(Align.center);
        coin.setScale(0.8f);
        coin.setX(-coin.getWidth() * (1 - coin.getScaleX()));
        coin.setY((getHeight() - coin.getHeight()) * 0.5f);
        addActor(coin);
        maxDst = coin.getWidth();

        if(screen.wordGame.shoppingProcessor != null && screen.wordGame.shoppingProcessor.isIAPEnabled()){
            plus = new DarkeningImageButton(new TextureRegionDrawable(AtlasRegions.coin_view_plus_up));
            plus.setX(getWidth() - plus.getWidth() + 5);
            plus.setY((getHeight() - plus.getHeight()) * 0.6f);
            addActor(plus);
            maxWidth = getWidth() - plus.getWidth() - coin.getWidth();
        }else{
            maxWidth = getWidth() - coin.getWidth();
        }

        maxWidth *= 0.9f;
        String font = ResourceManager.fontSignikaBold;
        Label.LabelStyle style = new Label.LabelStyle(screen.wordGame.resourceManager.get(font, BitmapFont.class), Color.WHITE);

        label = new Label("", style);
        label.setOrigin(Align.bottom);
        label.setAlignment(Align.center);
        addActor(label);

        update(DataManager.get(Constants.KEY_COIN_COUNT, GameConfig.DEFAULT_COIN_COUNT));
    }




    public void setPlusListener(ChangeListener changeListener){
        if(plus != null)
            plus.addListener(changeListener);
    }




    public void update(int count){
        label.setText(count);
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(label.getStyle().font, label.getText());

        if(glyphLayout.width > maxWidth){
            label.setFontScale(maxWidth / glyphLayout.width);
        }

        float maxHeight = getHeight() * 0.6f;
        if(glyphLayout.height * label.getFontScaleY() > maxHeight){
            label.setFontScale(maxHeight / glyphLayout.height * label.getFontScaleY());
        }

        if(plus == null)
            label.setX((getWidth() - label.getWidth()) * 0.6f);
        else
            label.setX((getWidth() - label.getWidth()) * 0.47f);

        label.setY((getHeight() - label.getHeight()) * 0.5f);
        Pools.free(glyphLayout);
    }




    public void incrementCoinLabelWithAnimationAndDeleteCoinImages(int startFrom, int count, Runnable callback){
        if(coinCountInrementer == null) {
            coinCountInrementer = new CoinCountInrementer();
        }else{
            coinCountInrementer.reset();
        }

        coinCountInrementer.setInterpolation(Interpolation.sineOut);
        coinCountInrementer.setDuration(count * 0.05f);
        coinCountInrementer.setStart(startFrom);
        coinCountInrementer.setEnd(startFrom + count);

        if(callback == null){
            label.addAction(coinCountInrementer);
        }else{
            label.addAction(Actions.sequence(
                    coinCountInrementer,
                    Actions.run(callback)
            ));

        }
    }




    private class CoinCountInrementer extends IntAction{

        @Override
        protected void update(float percent) {
            super.update(percent);
            CoinView.this.update(getValue());
        }

    }




    private Array<Particle> particles = new Array<>();
    private boolean animateSparkles;
    private float maxDst;

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

        float x = getX() + coin.getX();
        float y = getY() + coin.getY();

        float sparkleRadius = 0.5f;

        particle.x = particle.startX = x + (coin.getOriginX() * coin.getScaleX() - particle.getWidth() * 0.5f)  + (cos * coin.getOriginX() * coin.getScaleX() * sparkleRadius);
        particle.y = particle.startY = y + (coin.getOriginY() * coin.getScaleY() - particle.getHeight() * 0.5f)  + (sin * coin.getOriginY() * coin.getScaleY() * sparkleRadius);
        particle.radius = MathUtils.random(0.4f, 1.0f);
        particle.rotation = MathUtils.random() * 360.0f;
        particle.friction = 0.97f;
        particle.dst = 0;

        float speed = MathUtils.random(2f, 4f);
        particle.vx = cos * speed;
        particle.vy = sin * speed;
    }


    public void startSparkleAnimation(){
        animateSparkles = true;
    }



    public void stopSparkleAnimation(){
        addAction(Actions.sequence(
                Actions.delay(0.5f),
                Actions.run(stopAnimDelayRunnable)
        ));
    }




    private Runnable stopAnimDelayRunnable = new Runnable() {
        @Override
        public void run() {
            animateSparkles = false;
        }
    };



    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(animateSparkles){
            if(particles.size < 40) createParticle();
        }

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
            p.setAlpha(batch.getColor().a);
            p.draw(batch);

            if(p.dst > maxDst){
                if(animateSparkles) {
                    resetParticle(p);
                }else{
                    particles.removeValue(p, true);
                    Pools.free(p);
                }
            }
        }
        super.draw(batch, parentAlpha);
    }


}

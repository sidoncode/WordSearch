package word.search.ui.hud.game_hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import word.search.GameController;
import word.search.config.GameConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Word;
import word.search.ui.game.board.Letter;
import word.search.ui.game.buttons.DarkeningImageButton;

import word.search.ui.game.particle.Particle;
import word.search.ui.game.particle.SparkleParticle;
import word.search.ui.game.wordsview.AnimatedLetter;
import word.search.ui.game.wordsview.PlaceHolderCoin;
import word.search.ui.hud.CoinView;

public class BonusWordButton extends DarkeningImageButton {

    private GameController gameController;
    private Label label;
    private Image glow;
    private CoinView coinView;
    public boolean animating;

    public BonusWordButton(GameController gameController) {
        super(new TextureRegionDrawable(AtlasRegions.btn_bonus));
        setOrigin(Align.center);
        setTransform(true);
        this.gameController = gameController;
    }


    public void update(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = gameController.gameScreen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        label = new Label(" ", style);
        label.setFontScale(0.65f);
        label.setY(-3);
        addActor(label);
    }


    public void animateLetters(){
        Array<Letter> selection = gameController.gameScreen.board.getSelectedViewsInstant();

        float scale = getHeight() * 0.3f / selection.get(0).label.getHeight();
        float endX = getWidth() * 0.5f - selection.get(0).label.getWidth() * scale * 0.5f;
        float endY = getHeight() * 0.5f - selection.get(0).label.getHeight() * scale * 0.5f;

        for(int i = 0; i < selection.size; i++) {
            Label copyFrom = selection.get(i).label;

            AnimatedLetter animatedLetter = Pools.obtain(AnimatedLetter.class);
            animatedLetter.init(copyFrom.getText().toString(), copyFrom.getStyle(), copyFrom.getFontScaleX());

            Vector2 p = copyFrom.localToActorCoordinates(this, animatedLetter.getVector2());
            animatedLetter.setPosition(p.x, p.y);
            addActor(animatedLetter);

            float startX = p.x, startY = p.y;
            float p1x = endX + (p.x - endX) * 0.5f;
            float p1y = p.y;
            float p2x = endX;
            float p2y = endY + (p.y - endY) * 0.5f;

            animatedLetter.moveToBonusButton(startX, startY, p1x, p1y, p2x, p2y, endX, endY, scale, i == selection.size - 1 ? letterAnimFinished : null);
        }
    }



    private Runnable letterAnimFinished = new Runnable() {

        @Override
        public void run() {
            addAction(Actions.sequence(
                    Actions.scaleTo(1.1f, 1.1f, 0.1f, Interpolation.slowFast),
                    Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fastSlow),
                    Actions.run(scaleFinished)
            ));
        }

    };



    private Runnable scaleFinished = new Runnable() {

        @Override
        public void run() {
            gameController.gameScreen.checkBonusWordsTutorial();

            if(Word.getBonusWordCount() == GameConfig.NUMBER_OF_BONUS_WORDS_TO_FIND_FOR_REWARD){
                gameController.triggerBonusWordAnimation();
            }
        }

    };




    public Runnable shakeFinished = new Runnable() {
        @Override
        public void run() {
            animating = false;
        }
    };



    public void setCount(int count){
       label.setText(count);

        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(label.getStyle().font, label.getText());
        label.setX((getWidth() - glyphLayout.width * label.getFontScaleX()) * 0.5f);
        Pools.free(glyphLayout);
    }




    public void animateMileStone(CoinView coinView){
        this.coinView = coinView;

        glow = new Image(AtlasRegions.bonus_glow);
        glow.setOrigin(Align.center);
        glow.setX((getWidth() - glow.getWidth()) * 0.5f);
        glow.setY((getHeight() - glow.getHeight()) * 0.5f);
        glow.getColor().a = 0;
        addActor(glow);
        maxDst = getWidth();

        glow.addAction(Actions.sequence(
                Actions.fadeIn(1, word.search.actions.Interpolation.cubicInOut),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        doSparkle();
                    }
                }),
                Actions.parallel(
                        Actions.fadeOut(0.5f, word.search.actions.Interpolation.cubicIn),
                        Actions.scaleTo(3f, 3f, 0.5f, word.search.actions.Interpolation.cubicIn)
                ),
                Actions.run(glowEnd)
        ));
    }



    private Runnable glowEnd = new Runnable() {

        @Override
        public void run() {
            glow.remove();
            glow = null;
            setCount(0);
        }

    };


    private Array<Particle> particles = new Array<>();
    private float maxDst;


    private void doSparkle(){
        for(int i = 0; i < 30; i++) createParticle();
        coinViewAnim();
    }


    private float originalCoinViewX, originalCoinViewY;

    private void coinViewAnim(){
        originalCoinViewX = coinView.getX();
        originalCoinViewY = coinView.getY();

        Vector2 pos = coinView.localToActorCoordinates(this, new Vector2());

        coinView.remove();
        coinView.setPosition(pos.x, pos.y);
        addActor(coinView);

        pos.x = (glow.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
        pos.y = (glow.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

        Vector2 offset = glow.localToActorCoordinates(coinView, pos);

        Actor targetCoin = coinView.coin;
        float tx = targetCoin.getX() + (targetCoin.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
        float ty = targetCoin.getY() + (targetCoin.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

        for(int i = 0; i < GameConfig.NUMBER_OF_COINS_GIVEN_AS_BONUS_WORDS_REWARD; i++){
            PlaceHolderCoin coin = Pools.obtain(PlaceHolderCoin.class);

            coin.setPosition(offset.x, offset.y);
            coinView.addActor(coin);

            boolean last = i == GameConfig.NUMBER_OF_COINS_GIVEN_AS_BONUS_WORDS_REWARD - 1;
            coin.animate((i) * 0.1f, tx, ty, coinView, i == 0, last, last ? coinAnimFinished : null, gameController.gameScreen.wordGame.resourceManager);
            coin.run = true;
        }
    }



    private Runnable coinAnimFinished = new Runnable() {
        @Override
        public void run() {
            addAction(Actions.sequence(Actions.delay(1.2f), Actions.run(delayFinished)));
            getStage().getRoot().setTouchable(Touchable.enabled);
            setCount(0);
        }
    };



    private Runnable delayFinished = new Runnable() {
        @Override
        public void run() {
            coinView.setPosition(originalCoinViewX, originalCoinViewY);
            getStage().getRoot().addActor(coinView);

        }
    };



    private void createParticle(){
        SparkleParticle p = Pools.obtain(SparkleParticle.class);
        particles.add(p);
        resetParticle(p);
    }




    private void resetParticle(Particle particle){
        float TWO_PI = 6.283185f;
        float angle = MathUtils.random() * TWO_PI;
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);

        particle.angle = angle * MathUtils.radiansToDegrees;
        particle.x = particle.startX = getX() + glow.getX() + (glow.getWidth() * glow.getScaleX() - particle.getWidth()) * 0.5f + ( glow.getWidth() * glow.getScaleX()-particle.getWidth()) * cos * 0.5f;
        particle.y = particle.startY = getY() + glow.getY() + (glow.getHeight() * glow.getScaleY() - particle.getHeight()) * 0.5f + (glow.getHeight() * glow.getScaleY() -particle.getHeight()) * sin * 0.5f;
        particle.radius = MathUtils.random(0.7f, 1f);
        particle.rotation = MathUtils.random() * 360.0f;
        particle.friction = 0.97f;
        particle.dst = 0;

        float speed = MathUtils.random(5f, 8f);
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
            }
        }
    }


}

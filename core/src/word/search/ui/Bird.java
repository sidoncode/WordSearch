package word.search.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.managers.ResourceManager;
import word.search.screens.BaseScreen;

public class Bird extends Actor {

    private Animation<TextureRegion> animation;
    private float time;
    private float randomStartTime;


    public Bird(){
        TextureRegion[] walkFrames = {
                AtlasRegions.bird_1,
                AtlasRegions.bird_2,
                AtlasRegions.bird_3,
                AtlasRegions.bird_4,
                AtlasRegions.bird_5,
                AtlasRegions.bird_6,
                AtlasRegions.bird_7,
                AtlasRegions.bird_8,
                AtlasRegions.bird_9,
        };

        animation = new Animation<>(0.08f, walkFrames);
        randomStartTime = MathUtils.random(0.8f);
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(UIConfig.BIRD_COLOR.r, UIConfig.BIRD_COLOR.g, UIConfig.BIRD_COLOR.b, UIConfig.BIRD_COLOR.a * parentAlpha);

        time += Gdx.graphics.getDeltaTime();
        if(time > randomStartTime){
            TextureRegion currentFrame = animation.getKeyFrame(time - randomStartTime, true);
            batch.draw(currentFrame, getX(), getY(), 0, 0, currentFrame.getRegionWidth(), currentFrame.getRegionHeight(), getScaleX(), getScaleY(), 0);
        }
        batch.setColor(1, 1, 1, 1);
    }




    public static class BirdManager{

        private static BaseScreen screen;
        private static Array<Bird> birds = new Array();
        private static Rectangle bounds;
        private static int finishedCount;

        public static void create(BaseScreen screen, Rectangle bounds, int count){
            BirdManager.screen = screen;
            BirdManager.bounds = bounds;

            for(int i = 0; i < count; i++){
                Bird bird = new Bird();
                birds.add(bird);
                screen.stage.addActor(bird);
                reset(bird);
            }
        }



        private static void reset(Bird bird){
            bird.setX(MathUtils.random(bounds.x, bounds.x + bounds.width - bird.getWidth()));
            bird.setY(MathUtils.random(bounds.y, bounds.y + bounds.height - bird.getHeight()));
            bird.setScale(MathUtils.random(0.5f, 1.0f));

            bird.addAction(Actions.sequence(
                    Actions.moveTo(screen.stage.getWidth() + bird.getWidth(), bird.getY(), MathUtils.random(UIConfig.BIRD_FLY_MIN_DURATION, UIConfig.BIRD_FLY_MAX_DURATION)),
                    Actions.run(flyEnd)
            ));
        }




        private static Runnable flyEnd = new Runnable() {

            @Override
            public void run() {
                finishedCount++;
                if(finishedCount == birds.size){
                    finishedCount = 0;
                    screen.stage.getRoot().addAction(Actions.sequence(
                            Actions.delay(MathUtils.random(5f, 10f)),
                            Actions.run(restarter)
                    ));
                }
            }

        };




        private static Runnable restarter = new Runnable() {

            @Override
            public void run() {
                for(Bird bird : birds) reset(bird);
            }

        };

    }


}

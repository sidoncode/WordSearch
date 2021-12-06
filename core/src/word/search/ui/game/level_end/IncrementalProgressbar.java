package word.search.ui.game.level_end;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import word.search.actions.Interpolation;
import word.search.app;
import word.search.graphics.AtlasRegions;

public class IncrementalProgressbar extends Group {


    private Array<Image> backgrounds = new Array();
    private Array<Image> highlights = new Array();
    private Image part;
    private Runnable callback;

    public IncrementalProgressbar(int slices){

        float x = 0;

        for(int i = 0; i < slices; i++){
            Image img = null;
            if(i == 0) img = new Image(AtlasRegions.pb_start_bg);
            else if(i < slices - 1) img = new Image(AtlasRegions.pb_mid_bg);
            else img = new Image(AtlasRegions.pb_end_bg);

            addActor(img);
            img.setX(x - i * 15);
            x += img.getWidth();

            backgrounds.add(img);
        }

        if(slices > 0) {
            setWidth(backgrounds.peek().getX() + backgrounds.peek().getWidth());
            setHeight(backgrounds.get(0).getHeight());
        }
    }



    public void setHighlightedCount(int count, boolean animate){
        if(count > backgrounds.size) return;
        int iteration = count - highlights.size;
        int offset = highlights.size;

        for(int i = 0; i < iteration; i++){
            if(offset + i == 0) part = new Image(AtlasRegions.pb_start);
            else if(offset + i < backgrounds.size - 1) part = new Image(AtlasRegions.pb_mid);
            else part = new Image(AtlasRegions.pb_end);

            part.setX(backgrounds.get(offset + i).getX());
            highlights.add(part);

            if(!animate){
                addActor(part);
            }else{
                part.getColor().a = 0f;
                addActor(part);

                Image glow = new Image(AtlasRegions.pb_glow);
                glow.getColor().a = 0f;
                glow.setPosition(part.getX() - 30, part.getY() - 25);
                addActor(glow);

                float time = 0.7f;
                part.addAction(Actions.fadeIn(time, Interpolation.cubicIn));

                SequenceAction sequenceAction = Actions.sequence(
                        Actions.fadeIn(time, Interpolation.cubicIn),
                        Actions.fadeOut(time, Interpolation.cubicOut)
                );

                if(callback != null) sequenceAction.addAction(Actions.run(callback));

                glow.addAction(sequenceAction);
            }
        }
    }



    public void setCallback(Runnable runnable){
        callback = runnable;
    }



    public int getHiglightedCount(){
        return highlights.size;
    }


    public void reset(){
        for(Image part : highlights){
            part.remove();
            part = null;
        }
        highlights.clear();

    }


}

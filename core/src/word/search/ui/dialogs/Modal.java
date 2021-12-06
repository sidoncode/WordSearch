package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import word.search.graphics.NinePatches;


public class Modal extends Group {

    private Image bg;

    public Modal(float width, float height){
        this(width, height, 0.8f);
    }


    public Modal(float width, float height, float alpha){
        setSize(width, height);
        bg = new Image(NinePatches.rect);
        bg.setSize(width, height);
        bg.setColor(new Color(0, 0, 0, alpha));
        addActor(bg);
    }


    public void resize(float width, float height){
        setSize(width, height);
        bg.setSize(width, height);
    }

}

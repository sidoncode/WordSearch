package word.search.managers;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;


public class ResourceManager implements Disposable {

    //atlas files
    public static String ATLAS_1                    = "textures/pack1.atlas";
    public static String ATLAS_2                    = "textures/pack2.atlas";


    //fonts
    public static String fontSignikaBoldBoard       = "fonts/signika_bold_board.fnt";
    public static String fontSignikaBoldShadow      = "fonts/signika_bold_shadow.fnt";
    public static String fontSignikaBold            = "fonts/signika_bold.fnt";


    public static String introBackground;



    //Sound effects
    public static final String level_start                  = "sfx/level_start.mp3";
    public static final String letter_select_01             = "sfx/letter_select_01.mp3";
    public static final String letter_select_02             = "sfx/letter_select_02.mp3";
    public static final String letter_select_03             = "sfx/letter_select_03.mp3";
    public static final String letter_select_04             = "sfx/letter_select_04.mp3";
    public static final String letter_select_05             = "sfx/letter_select_05.mp3";
    public static final String letter_select_06             = "sfx/letter_select_06.mp3";
    public static final String letter_select_07             = "sfx/letter_select_07.mp3";
    public static final String letter_select_08             = "sfx/letter_select_08.mp3";
    public static final String fail                         = "sfx/fail.mp3";
    public static final String success                      = "sfx/success.mp3";
    public static final String level_complete               = "sfx/level_complete.mp3";
    public static final String word_reveal                  = "sfx/word_reveal.mp3";
    public static final String board_reveal                 = "sfx/board_reveal.mp3";
    public static final String magic                        = "sfx/magic.mp3";
    public static final String rotate                       = "sfx/rotate.mp3";
    public static final String bonus_word_dup               = "sfx/bonus_word_dup.mp3";
    public static final String daily_reward                 = "sfx/daily_reward.mp3";
    public static final String box_touch                    = "sfx/box_touch.mp3";
    public static final String purchased_iap                = "sfx/purchased_iap.mp3";
    public static final String bonus_word                   = "sfx/bonus_word.mp3";
    public static final String milestone_pop                = "sfx/milestone_pop.mp3";
    public static final String coin_add                     = "sfx/coin_add.mp3";
    public static final String daily_pick                   = "sfx/daily_pick.mp3";
    public static final String duble                        = "sfx/double.mp3";
    public static final String daily_item_hit               = "sfx/daily_item_hit.mp3";
    public static final String word_found_before            = "sfx/word_found_before.mp3";


    /***** DO NOT EDIT ANYTHING BELOW *****/

    public static String LOCALE_PROPERTIES_FILE;

    private final AssetManager manager = new AssetManager();




    public void load(String fileName, Class type){
        manager.load(fileName, type);
    }

    public void load(String fileName, Class type, AssetLoaderParameters parameter){
        manager.load(fileName, type, parameter);
    }

    public void load(AssetDescriptor desc){
        manager.load(desc);
    }



    public synchronized boolean update(){
        return manager.update();
    }



    public boolean update (int millis){
        return manager.update(millis);
    }

    public synchronized float getProgress(){
        return manager.getProgress();
    }


    public synchronized boolean isFinished (){
        return manager.isFinished();
    }

    public synchronized <T> T get (String fileName, Class<T> type) {
        return manager.get(fileName, type);
    }

    public <T> Array<T> getAll(Class<T> type, Array<T> out){
        return manager.getAll(type, out);
    }


    public void setLoader (Class type, AssetLoader loader){
        manager.setLoader(type, loader);
    }

    public void finishLoading(){
        manager.finishLoading();
    }

    public void finishLoadingAsset(String fileName){
        manager.finishLoadingAsset(fileName);
    }


    public boolean contains(String fileName){
        return manager.contains(fileName);
    }


    public void unload(String fileName){
        manager.unload(fileName);
    }


    public void clear(){
        manager.clear();
    }

    @Override
    public void dispose() {
        manager.dispose();
    }




}

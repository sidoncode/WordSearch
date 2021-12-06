package word.search.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import word.search.model.Constants;
import word.search.model.Language;

public class DataManager {

    private static boolean enabled = true;
    private static Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);


    public static String getLocaleAwareKey(String name){
        return name + "_" + Language.locale.code;
    }



    /*************************************************************************************/

    public static void set(String name, String value){
        if(enabled) {
            preferences.putString(name, value);
            preferences.flush();
        }
    }

    public static String get(String name, String defaultValue){
        return preferences.getString(name, defaultValue);
    }


    /*************************************************************************************/


    public static void set(String name, int value){
        if(enabled) {
            preferences.putInteger(name, value);
            preferences.flush();
        }
    }

    public static int get(String name, int defaultValue){
        return preferences.getInteger(name, defaultValue);
    }


    /*************************************************************************************/

    public static void set(String name, long value){
        if(enabled) {
            preferences.putLong(name, value);
            preferences.flush();
        }
    }

    public static long get(String name, long defaultValue){
        return preferences.getLong(name, defaultValue);
    }

    /*************************************************************************************/


    public static void set(String name, boolean value){
        if(enabled) {
            preferences.putBoolean(name, value);
            preferences.flush();
        }
    }

    public static boolean get(String name, boolean defaultValue){
        return preferences.getBoolean(name, defaultValue);
    }




    /*************************************************************************************/

    public static void remove(String name){
        if(enabled){
            preferences.remove(name);
            preferences.flush();
        }
    }

}

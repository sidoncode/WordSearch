package word.search.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Map;


import word.search.WordGame;
import word.search.config.GameConfig;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.platform.dict.WordMeaningProvider;

public class Language {

    public static Locale locale;
    public static I18NBundle bundle;
    public static Map<String, WordMeaningProvider> wordMeaningProviderMap;

    public static void setLocale(String code, WordGame wordConnectGame){
        Locale newLocale = GameConfig.availableLanguages.get(code);
        newLocale.code = code;
        Language.locale = newLocale;
        DataManager.set(Constants.KEY_SELECTED_LANGUAGE, code);
    }



    public static String getSelectedLocaleCode(){
        if(locale != null)
            return locale.code;

        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getString(Constants.KEY_SELECTED_LANGUAGE, null);
    }



    public static void updateSelectedLanguage(ResourceManager resourceManager){
        Word.readWords(resourceManager);
    }


    public static String get(String key){
        return bundle.get(key);
    }

    public static String format(String key, Object... args){
        return bundle.format(key, args);
    }

}

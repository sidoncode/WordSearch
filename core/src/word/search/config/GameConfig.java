package word.search.config;

import java.security.PublicKey;
import java.util.LinkedHashMap;
import java.util.Map;

import word.search.model.Locale;

public class GameConfig {



    //Warning: level numbers specified are always 0-based. Add 1 to get the real number.
    //Uyarı: bu dosyada belirtilen seviye numaraları 0 sayısından başlar. Gerçek sayı için 1 ekleyin.




    //See the daily gift dialog before its time
    //Günlük ödül diyaloğunu zamanından önce test etmek için
    public static final boolean DEBUG_DAILY_GIFT                                    = false;



    /**
     * The following option is good if you add new levels for any reason.
     * When you add new levels, you may want to adjust how words appear (ordering).
     * When you enable this option you can skip levels fast without solving them just by tapping
     * bulb hint button. Wait animations to finish while doing it.
     *
     * Aşağıdaki seçeneği yeni seviye eklediyseniz kullanabilirsiniz. Yeni bir seviye
     * eklediğinizde kelimelerin (kelime sıralaması) nasıl göründüğünü hızlı bir şekilde
     * görmek için bu seçeneği aktive edin. Aktive ettiğinizde ampul ikonlu butona basarak
     * mevcut seviyeyi çözmeden ilerleyebilirsiniz. Bu işlemi yaparken animasyonların
     * bitmesini bekleyiniz.
     */
    public static boolean DEBUG_WORD_ORDER                                          = false;


    //Enter the 0-based level index. It skips the levels before it.
    //Test etmek istediğiniz seviyenin numarası. 0'dan başlar.
    public static final int LEVEL_INDEX_TO_DEBUG                                    = -1;


    //How generous are you in giving away daily gift coins?
    //Günlük ödülde kaç adet jeton verilmeli?
    public static final int DAILY_COIN_GIFT_COUNT                                   = 25;



    //Number of free coins at app install
    //Varsayılan jeton sayısı
    public static final int DEFAULT_COIN_COUNT                                      = 25;


    //Number of light bulb hints at app install
    //Varsayılan lamba ipucu sayısı
    public static final int DEFAULT_SINGLE_WORD_REVEAL_COUNT                        = 1;


    //Number of magnifier hints at app install
    //Varsayılan büyüteç ipucu sayısı
    public static final int DEFAULT_SINGLE_BOARD_REVEAL_COUNT                       = 1;


    //Number of magic wand hints at app install
    //Varsayılan sihirli değnek ipucu sayısı
    public static final int DEFAULT_MAGIC_REVEAL_COUNT                              = 1;


    //Number of max letters to hide when magic wand is used (depending on the width/height of the board)
    //Sihirli değnek kullanılınca en fazla kaç harf gizlenmeli (tablo en/boy sayısına göre değişir)?
    public static int maxNumberOfLettersToRemoveForMagicWand(int boardSize){
        switch (boardSize){
            case 4: return 3;
            case 5: return 4;
            case 6: return 5;
            case 7: return 5;
            case 8: return 6;
        }
        return 0;
    }


    //How many extra words to find for earning bonus?
    //Ödül kazanmak için tabloda bulunmayan kelimelerden kaç adet bulmalı?
    public static final int NUMBER_OF_BONUS_WORDS_TO_FIND_FOR_REWARD                = 25;


    //How many levels in a row must be solved to earn coins?
    //Seviye sonu ödülü için üst-üste kaç seviye tamamlanmalı?
    public static final int NUMBER_OF_LEVELS_TO_SOLVE_FOR_MILESTONE_REWARD          = 10;


    //The number of coins to give for the above case
    //Üstte bahsedilen ödülde kaç jeton verilmeli?
    public static final int NUMBER_OF_REWARD_COINS_FOR_LEVEL_MILESTONE              = 10;


    //What should people watch rewarded video ad for? Give them something.
    //Ödüllü reklam izleyenlere kaç jeton verilmeli?
    public static final int NUMBER_COINS_GIVEN_FOR_WATHCING_VIDEO                   = 25;


    //The number of coins to give for collecting enough bonus words
    //Yeteri kadar bonus kelime bulununca ödül olarak kaç jeton verilmeli?
    public static final int NUMBER_OF_COINS_GIVEN_AS_BONUS_WORDS_REWARD             = 15;


    /**
     * Supported languages by the game. Each control both UI labels and game words.
     * k: two-letter language code such en, de, es, pt. Please refer to the help
     * file for the instructions to add a new language.
     * If you set only 1 language, language selection will not be prompted in the
     * first run of the game and it will not be visible in menu.
     * If you add new levels or delete existing ones, you must modify it below.
     * The number of levels is not read automatically by code because it is
     * a very slow process (this is tested and discarded). Therefore, it is
     * best to hard code it here.
     * Magic wand removes one or more unnecessary letters from the board.
     * However, its tutorial can be dangerous if there is no letter to remove
     * in that particular level. If you add a new language, make sure the
     * level specified below has some unnecessary letters.
     *
     *
     *
     * Oyunda kullanılan diller. Her bir dilin arayüz tercümesi ve seviyeleri
     * olmak zorunda.
     * k: 2 harfli dil kodu, örn. en, de, es, pt. Yeni bir dilin nasıl ekleneceği
     * yardım dosyasında yazılı. Eğer tek bir dil bırakırsanız, oyun menüsündeki
     * dil seçeneği görünmez. Herhangi bir dilin seviye sayısı değiştiriseniz
     * güncel sayıyı aşağıda belirtmeniz gerekmektedir.
     *
     * Sihirli değnek tablo üzerinde en az bir adet kullanılmayan harfi siler.
     * Fakat, tutorial esnasında tabloda kullanılmayan harf yoksa bu problem olabilir.
     * Bu yüzden yeni bir dil eklerseniz aşağıda belirtilen seviyede en az bir harfin
     * gereksiz olduğundan emin olun.
     */
    public static Map<String, Locale> availableLanguages = new LinkedHashMap<String, Locale>(){
        {
            put("en", new Locale("English", 1000, 12));
            put("tr", new Locale("Türkçe",1000, 12));
        }
    };



    //If you don't want to use the dictionary disable it.
    //Sözlük özelliğini kullanmak istemezseniz buradan kapatın.
    public static final boolean ENABLE_ENGLISH_LANGUAGE_DICTIONARY = true;


    //How many maximum result items do you wish to display at dictionary at result?
    //İngilizce sözlükte gösterilen maksimum sonuç sayısı
    public static final int ENGLISH_DICTIONARY_MAX_RESULT = 20;



    //You should register with wordnik.com to get an API key. You require this key to offer the
    //dictionary functionality for English language.
    //İngilizce sözlüğü kullanmak istiyorsanız wordnik.com sitesinden API anahtarı almanız gerek.
    public static final String WORDNIC_API_KEY = "PASTE_YOUR_WORDNIK_API_KEY_HERE";




    //Firebase analytics
    //Firebase analytics aç/kapat
    public static boolean ENABLE_ANALYTICS = true;




    //The events that will be logged by Firebase analytics
    //Firebase tarafından takip edilen olaylar

    //Custom events. Özel olaylar
    public static boolean ENABLE_LOGGING_LANGUAGE_SELECTION_EVENT       = true;
    public static boolean ENABLE_LOGGING_EARNED_EARNED_HINT_EVENT       = true;//daily reward
    public static boolean ENABLE_LOGGING_USED_HINT_EVENT                = true;


    //Firebase built-in events. Firebase'in kendi olayları
    public static boolean ENABLE_LOGGING_TUTORIAL_BEGIN_EVENT           = true;
    public static boolean ENABLE_LOGGING_TUTORIAL_COMPLETE_EVENT        = true;
    public static boolean ENABLE_LOGGING_EARN_VIRTUAL_CURRENCY_EVENT    = true;//logged after watching rewarded video. Ödüllü videodan sonra loglanır
    public static boolean ENABLE_LOGGING_SPEND_VIRTUAL_CURRENCY_EVENT   = true;
    public static boolean ENABLE_LOGGING_LEVEL_START_EVENT              = true;
    public static boolean ENABLE_LOGGING_LEVEL_END_EVENT                = true;
    public static boolean ENABLE_LOGGING_MILESTONE_EVENT                = true;//As achievement
    public static boolean ENABLE_LOGGING_SCREEN_VIEW_EVENT              = true;
    public static boolean ENABLE_LOGGING_APP_SHARE                      = true;



    /**
     * Determines whether to show and ad or not. This function runs at the end
     * of each level. You can return true or false depending on the levelIndex.
     * LevelIndex is 0-based. By default, it shows level at the end of every level.
     *
     * Interstitial reklam gösterilip gösterilmeyeceğini belirler. Bu fonksiyon
     * her seviye bitiminde çalıştırılır. levelIndex parametresine göre true
     * yada false değer döndürmeniz gerekmekte. leveIndex 0'dan başlar. Bu
     * haliyle her seviye sonunda reklam gösterilir.
     */
    public static boolean shouldWeShowAnInterstitialAd(int levelIndex){
        return true;
        //return (levelIndex % 2 == 0);//show ad every other level
    }





    /**
     * The game is capable of playing and controlling a background music loop. However the file is not
     * included. If decide to use this functionality copy and paste your music file to the location
     * specified below (it is under android/assets). It is recommended that use .ogg format as it is most
     * suitable for looping. When you use it, the music mute button will appear in the game menu.
     *
     * Uygulama arka plan müziği çalabilir ve kontrol edebilir. Fakat müzik dosyası projeye dahil değildir.
     * Bu özelliği kullanmak isterseniz, aşağıda belirtilen konuma müzik dosyanızı kopyalayın (android/assets altında).
     * .ogg formatı bu iş için en uygunudur. Eğer müzik kullanmayı tercih ederseniz oyun menüsünde mute butonu
     * görünür hale gelir.
     */
    public static final String PATH_TO_LOOP_MUSIC_FILE = "sfx/loop.ogg";

}

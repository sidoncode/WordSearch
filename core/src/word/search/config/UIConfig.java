package word.search.config;

import com.badlogic.gdx.graphics.Color;

public class UIConfig {


    //Çözülen kelime şeritlerinin şeffaflığı
    //The alpha of color stripes for solved words on board
    public static final float COMPLETED_WORD_SELECTION_COLOR_OPACITY = 0.7f;

    //Intro arkaplan resim dosyası
    //Background image for intro
    public static final String INTRO_SCREEN_BACKGROUND_IMAGE = "bg/intro.jpg";







    /**
     * Seviye indeksine göre oyunda arka plan resmi belirlenebilir.
     * Mevcut şekilde, her 10 seviyede bir resim değiştirilir. 100 seviye
     * sonunda ilk resimle devam edilir. Daha fazla resim ekleyebilirsiniz,
     * böyle bir durumda aşağıdaki GAME_BACKGROUND_COUNT sayısını değiştirmeniz gerekir.
     * Resimler assets/bg klasörüne koyulur. Yeni dosya isimleri mevcut
     * resimlerle aynı paternde olmalıdır.
     *
     * You can specify which background to show depending on the level index.
     * In the default implementation, it shows the same image for 10 levels
     * and then switches to the next, i.e. it cycles. The total number of images is 10.
     * You can add more images and in such a case you should increase the value
     * of GAME_BACKGROUND_COUNT variable below. The images are in assets/bg folder.
     * Use the same naming style if you want to add new images.
     *
     * @param levelIndex 0-based level number
     * @return It returns the path to the background image in the assets folder
     */
    public static final int GAME_BACKGROUND_COUNT = 10;
    public static String getGameScreenBackgroundImage(int levelIndex){
        int num = levelIndex / GAME_BACKGROUND_COUNT;
        num %= GAME_BACKGROUND_COUNT;

        return "bg/game_" + num + ".jpg";
    }



    //Intro ekranında uçan kuşların ayarı
    //Bird config for the flying birds in the intro screen
    public static boolean ENABLE_BIRD_ANIMATION     = true;
    public static int NUMBER_OF_BIRDS               = 6;
    public static Color BIRD_COLOR                  = Color.WHITE;
    public static float BIRD_FLY_MIN_DURATION       = 10f;
    public static float BIRD_FLY_MAX_DURATION       = 13f;


    //Oyun tablosu şeffaflık değeri
    ////Game board opacity
    public static final float GAME_BOARD_ALPHA = 0.9f;

    //Harflerle çerçeve arasındaki mesafe
    //The distance between the letters and the end of the board
    public static final float GAME_BOARD_PADDING_PX = 0;

    //Some buttons has bottom padding due to their shadow
    //Bazı butonların altında gölge olduğu için padding eklenir
    public static final float BUTTON_BOTTOM_PADDING = 10;

}

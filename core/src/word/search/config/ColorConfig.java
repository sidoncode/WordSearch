package word.search.config;

import com.badlogic.gdx.graphics.Color;

public class ColorConfig {

    /**
     * Renk değerlerini belirli bir formatta girmeniz gerekli.
     * Color.WHITE, Color.PURPLE, Color.GREEN gibi kısa ve kolay yol ilk alternatif fakat
     * bu renk isimleri sınırlıdır. Daha özel bir renk kullanmak için 4 baytlı sayı değeri
     * girmeniz gerekli. Örneğin new Color(0x65ab29ff). En sondaki ff şeffaflıktır.
     * Aşağıda örnekleri görebilirsiniz.
     *
     * You should enter color values in a specific format.
     * You can use short-hand color format such as: Color.WHITE, Color.PURPLE, Color.GREEN, etc.
     * For more fine-tuned colors you should create a Color object supplying the color value
     * in 4-byte hex format as in new Color(0x65ab29ff). This color is in RGBA format.
     */


    //Diyalog kutuları
    //Dialog boxes
    public static final Color DIALOG_TITLE_COLOR                    = new Color(Color.WHITE);
    public static final Color DIALOG_BACKGROUND_COLOR               = new Color(0xebe0ccff);
    public static final Color DIALOG_TITLE_BACKGROUND_COLOR         = new Color(0xa13b22ff);
    public static final Color DIALOG_TEXT_COLOR                     = new Color(0xA26952ff);

    //Tutorial metin arka plan rengi
    //Tutorial text bg color
    public static final Color TUTORIAL_TEXT_BACKGROUND_COLOR        = new Color(0xdb4cc5ff);


    //Sahne arka plan renkler.
    //Scene background colors.
    public static final Color INTRO_SCREEN_BACKGROUND_COLOR         = new Color(0x000000FF);
    public static final Color GAME_SCREEN_BACKGROUND_COLOR          = new Color(0x000000FF);


    //Level numarasını gösteren kısım
    //The part that shows level index
    public static final Color LEVEL_BOARD_BACKGROUND_COLOR          = new Color(0x22c003ff);

    //Çözülen kelime arka plan rengi (üst taraftaki kelimeler)
    //Solved word bg color (the words in the upper part)
    public static final Color SOLVED_WORD_BACKGROUND_COLOR          = new Color(0x00c22eff);


    //İpucu olarak açılan harf rengi
    //Color for the revealed letters
    public static final Color WORDS_VIEW_LETTER_COLOR               = Color.WHITE;



    //Seviyedeki kategoriyi gösteren kurdale
    //The Category ribbon for the level
    public static final Color CATEGORY_RIBBON_COLOR                 = new Color(0xfe973cff);


    //Seçilen kelimeleri gösteren tablo üzerindeki şerit
    //The stripe over the board that shows the selected letters
    public static final Color WORD_PREVIEW_COLOR                    = new Color(0xe648b0ff);

    //Video butonu
    //Video button
    public static final Color REWARDED_VIDEO_BUTTON_COLOR           = new Color(0xff66f2ff);

    //Tablo üzerinde ipucu noktasının rengi
    //Reveal letter dot on board
    public static final Color BOARD_REVEAL_DISC_COLOR               = new Color(0x2caf2cff);

    //Uyarı şeridi
    //Toast
    public static final Color TOAST_BACKGROUND_COLOR                = new Color(0x3bb44fff);
    public static final Color TOAST_FONT_COLOR                      = Color.WHITE;

    //Geribildirim.
    //Feedback
    public static final Color FEEDBACK_RIBBON_BACKGROUND_COLOR      = new Color(0xfe973cff);
    public static final Color FEEDBACK_RIBBON_TEXT_COLOR            = Color.WHITE;


    //Oyundaki kelime seçim renkleri
    //Colors for selection stripes on board
    public static final Color[] GAME_BOARD_COLORS = new Color[]{
            Color.ORANGE,
            Color.RED,
            Color.PURPLE,
            new Color(0x2caa22ff),//green
            Color.BLUE,
            Color.PINK,
            Color.FIREBRICK,
            Color.TEAL,
            new Color(0xe4c000ff),//gold
            new Color(0x255ba4ff),//dark blue
            new Color(0x9b752bff),//brown
            new Color(0x949027ff),//dark yellow
            new Color(0x772d9aff)//dark purple

    };
}

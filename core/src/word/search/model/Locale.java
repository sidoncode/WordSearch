package word.search.model;

public class Locale {

    public String code;
    public String displayName;
    public int levelCount;
    public int magicWandTutorialLevel;


    public Locale(String displayName, int levelCount, int magicWandTutorialLevel) {
        this.displayName = displayName;
        this.levelCount = levelCount;
        this.magicWandTutorialLevel = magicWandTutorialLevel;
    }
}

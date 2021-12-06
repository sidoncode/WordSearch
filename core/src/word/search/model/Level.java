package word.search.model;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Pools;

import java.io.UnsupportedEncodingException;

import word.search.WordGame;
import word.search.app;
import word.search.managers.DataManager;
import word.search.ui.game.board.Direction;
import word.search.util.Text;
import word.search.util.TextLoader;

public class Level {

    public int index;
    public Array<Word> words = new Array<>();
    public char[] boardData;
    public int boardSize;
    public String category;
    public String displayPattern;
    public int coinedWordIndex = -1;

    private static JsonReader jsonReader = new JsonReader();
    private static Level level = new Level();
    public static Actor looper;
    public static WordGame wordGame;
    private static String fileName;
    private static int levelIndex;
    private static LevelReadyCallback levelReadyCallback;



    public String[] getWordsAsArray(){
        String[] array = new String[words.size];
        for(int i = 0; i < words.size; i++) array[i] = words.get(i).answer;
        return array;
    }



    public static void load(int index, LevelReadyCallback levelReadyCallback){
        Level.levelIndex = index;
        Level.levelReadyCallback = levelReadyCallback;

        fileName = "data/" + Language.locale.code + "/levels/" + index;
        wordGame.resourceManager.load(fileName, Text.class, new TextLoader.TextParameter());

        if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
            looper.addAction(Actions.forever(Actions.sequence(
                    Actions.run(loadChecker),
                    Actions.delay(0.16f)
            )));
            return;
        }

        wordGame.resourceManager.finishLoading();
        processLevelData();
    }



    private static Runnable loadChecker = new Runnable() {
        @Override
        public void run() {
            if(wordGame.resourceManager.update()){
                looper.clearActions();
                processLevelData();
            }
        }
    };




    public static void processLevelData(){
        Text content = wordGame.resourceManager.get(fileName, Text.class);
        String data = null;

        try {
            data = new String(content.getString().getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(data == null) {
            app.log("level data is null: "+fileName);
            return;
        }

        JsonValue doc = jsonReader.parse(data.toCharArray(), 0, data.length());

        level.boardData = doc.getString("b").toCharArray();
        level.boardSize = (int)Math.sqrt(level.boardData.length);
        level.category = doc.getString("c");
        if(doc.has("r")) level.coinedWordIndex = doc.getInt("r");
        else level.coinedWordIndex = -1;

        JsonValue wordsJ = doc.get("w");

        if(!level.words.isEmpty()) level.words.clear();
        level.words.ensureCapacity(wordsJ.size);

        for(int i = 0; i < wordsJ.size; i++){
            Word word = Pools.obtain(Word.class);
            word.index = i;
            word.setAnswer(wordsJ.get(i).asString());
            level.words.add(word);
        }

        level.index = levelIndex;
        level.displayPattern = generateSuitableDisplayPattern(level.words.size, level.boardSize);
        levelReadyCallback.onLevelReady(level);
    }





    public static String generateSuitableDisplayPattern(int wordCount, int boardSize) {
        if(wordCount == 1) return "0";
        if(wordCount == 2) return "0#0";
        if(wordCount == 3) return "0#0#1";

        if(wordCount == 4){
            if(boardSize == 8) return "0#1#2#3";
            return "0#0#1#1";
        }

        if(wordCount == 5) return "0#0#1#1#2";
        if(wordCount == 6) return "0#0#1#1#2#2";

        if(wordCount == 7) {
            if(boardSize == 6 || boardSize == 7) return "0#0#0#1#1#2#2";
            if(boardSize == 8){
                return "0#0#1#1#2#2#3";
            }
            return "0#0#0#1#1#1#2";
        }

        if(wordCount == 8){
            if(boardSize == 8){
                return "0#0#1#1#2#2#3#3";
            }
            return "0#0#0#1#1#1#2#2";
        }

        if(wordCount == 9) return "0#0#0#1#1#2#2#3#3";

        if(wordCount == 10) {
            if(boardSize == 8) return "0#0#0#1#1#1#2#2#3#3";
            return "0#0#0#1#1#1#2#2#2#3";
        }

        if(wordCount == 11) {
            return "0#0#0#1#1#1#2#2#2#3#3";
        }

        if(wordCount == 12) return "0#0#0#1#1#1#2#2#2#3#3#3";
        if(wordCount == 13) return "0#0#0#0#1#1#1#2#2#2#3#3#3";
        if(wordCount == 14) return "0#0#0#0#1#1#1#1#2#2#2#3#3#3";
        if(wordCount == 15) return "0#0#0#0#1#1#1#1#2#2#2#2#3#3#3";
        if(wordCount == 16) return "0#0#0#0#1#1#1#1#2#2#2#2#3#3#3#3";

        return "";
    }




    public static void saveCorrectAnswer(int index, int thrownLetterIndex, Direction direction, Array<Integer> positions){
        String existingJsonString = DataManager.get(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), "[]");
        JsonValue doc = jsonReader.parse(existingJsonString);

        for(int i = 0; i < doc.size; i++){
            JsonValue jsonValue = doc.get(i);
            int jindex = jsonValue.getInt("index");
            if(jindex == index){
                addWordCommonProperties(jsonValue, direction, thrownLetterIndex, positions);
                DataManager.set(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), doc.toJson(JsonWriter.OutputType.json));
                return;
            }
        }

        JsonValue newObj = new JsonValue(JsonValue.ValueType.object);
        newObj.addChild("index", new JsonValue(index));
        addWordCommonProperties(newObj, direction, thrownLetterIndex, positions);
        doc.addChild(newObj);
        DataManager.set(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), doc.toJson(JsonWriter.OutputType.json));
    }



    private static void addWordCommonProperties(JsonValue jsonValue, Direction direction, int thrownLetterIndex, Array<Integer> positions){
        jsonValue.addChild("direction", new JsonValue(direction.ordinal()));
        if(thrownLetterIndex > -1) jsonValue.addChild("thrown", new JsonValue(thrownLetterIndex));

        JsonValue arr = new JsonValue(JsonValue.ValueType.array);
        for(Integer p : positions) arr.addChild(new JsonValue(p));
        jsonValue.addChild("positions", arr);
    }



    public static void saveWordHint(int index){
        String existingJsonString = DataManager.get(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), "[]");
        JsonValue doc = jsonReader.parse(existingJsonString);

        for(int i = 0; i < doc.size; i++){
            JsonValue jsonValue = doc.get(i);
            int jindex = jsonValue.getInt("index");
            if(jindex == index){
                int count = 1;
                if(jsonValue.has("whints")) {
                    count = jsonValue.getInt("whints");
                    count++;
                    jsonValue.remove("whints");
                }
                jsonValue.addChild("whints", new JsonValue(count));
                DataManager.set(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), doc.toJson(JsonWriter.OutputType.json));
                return;
            }
        }
        saveHintByType(index, "whints", 1, doc);
    }




    private static void saveHintByType(int index, String type, int amout, JsonValue doc){
        JsonValue newObj = new JsonValue(JsonValue.ValueType.object);
        newObj.addChild("index", new JsonValue(index));
        newObj.addChild(type, new JsonValue(amout));
        doc.addChild(newObj);
        DataManager.set(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), doc.toJson(JsonWriter.OutputType.json));
    }



    public static void saveWindHintIndices(int[] indices){
        String existingJsonString = DataManager.get(DataManager.getLocaleAwareKey(Constants.WIND_HINTED_INDICES), "[]");
        JsonValue json = jsonReader.parse(existingJsonString);

        for(int i = 0; i < indices.length; i++){
            json.addChild(new JsonValue(indices[i]));
        }

        DataManager.set(DataManager.getLocaleAwareKey(Constants.WIND_HINTED_INDICES), json.toString());
    }



    public static void clearLevelJson(){
        DataManager.set(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), "[]");
        DataManager.remove(DataManager.getLocaleAwareKey(Constants.WIND_HINTED_INDICES));
        DataManager.remove(DataManager.getLocaleAwareKey(Constants.WSINGLE_HINT_INDEX));
        DataManager.remove(DataManager.getLocaleAwareKey(Constants.BOARD_HINT_MAPPING));
    }

}

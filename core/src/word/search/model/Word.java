package word.search.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import word.search.app;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.ui.game.board.Direction;
import word.search.util.Text;

public class Word implements Pool.Poolable {

    public static HashSet<String> words = new HashSet<>();
    private static JsonReader jsonReader = new JsonReader();

    public String answer;
    public int row;
    public boolean solved;

    public Direction direction;
    public int thrown;
    public int index;
    public int revealedLetterCount;


    public Word(){

    }


    public void setAnswer(String answer){
        this.answer = answer;
    }



    @Override
    public void reset() {
        solved = false;
        thrown = 0;
        row = 0;
        answer = null;
        index = 0;
        direction = null;
        revealedLetterCount = 0;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return word.answer.equals(answer);
    }




    public static Word findWordByAnswer(String answer, Array<Word> words){
        if(answer == null) return null;

        for(Word word : words){
            if(word.answer.equals(answer)) return word;
        }
        return null;
    }



    public static int findIndexOfAnswer(String answer, Array<Word> words){

        for(int i = 0; i < words.size; i++){
            if(answer.equals(words.get(i).answer)) return i;
        }
        return -1;
    }


    @Override
    public String toString() {
        return answer;
    }





    public static void readWords(ResourceManager resourceManager) {
        String fileName = "data/" + Language.locale.code + "/words.txt";
        Text text = resourceManager.get(fileName, Text.class);

        try {
            String str = new String(text.getString().getBytes(), "UTF-8");
            String[] split = str.split(",");

            words.clear();
            for (String s : split) {
                words.add(s);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }






    public static int insertWordToExtraJson(String word){
        int a = 0;
        int b = 0;

        if(words.contains(word)){
            a = 1;
            boolean exists = doesWordExistInExtraJson(word);
            if(!exists){
                b = 1;
                addWordToExtraJson(word);
            }else{
                b = 0;
            }
        }
        return (a << 8) | b;
    }





    private static boolean doesWordExistInExtraJson(String answer) {
        String json = DataManager.get(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORDS), "[]");
        JsonValue doc = jsonReader.parse(json);

        for(int i = 0; i < doc.size; i++){
            if(answer.equals(doc.get(i).asString())) return true;
        }
        return false;
    }


    public static void incrementFoundBonusWordCount(){
        int count = getBonusWordCount();
        count++;

        DataManager.set(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT), count);
    }


    public static void clearFoundBonusWordCount(){
        DataManager.remove(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT));
    }


    public static int getBonusWordCount(){
        return DataManager.get(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT), 0);
    }



    private static void addWordToExtraJson(String word){
        JsonValue doc = jsonReader.parse(DataManager.get(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORDS), "[]"));
        doc.addChild(new JsonValue(word));
        DataManager.set(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORDS), doc.toString());
    }


}

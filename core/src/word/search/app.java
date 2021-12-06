package word.search;

import com.badlogic.gdx.Gdx;

public class app {


    public static void log(String s){
        Gdx.app.log("word.game", s);
    }

    public static void log(String s1, String s2){
        Gdx.app.log("word.game", s1 + ", "+s2);
    }

    public static void log(String s1, String s2, String s3){
        Gdx.app.log("word.game", s1 + ", "+s2+", "+s3);
    }


    public static void log(String s1, String s2, String s3, String s4){
        Gdx.app.log("word.game", s1 + ", "+s2+", "+s3+", "+s4);
    }




    public static void log(float s){
        Gdx.app.log("word.game", s+"");
    }

    public static void log(float s1, float s2){
        Gdx.app.log("word.game", s1 + ", "+s2);
    }

    public static void log(float s1, float s2, float s3){
        Gdx.app.log("word.game", s1 + ", "+s2+", "+s3);
    }


    public static void log(float s1, float s2, float s3, float s4){
        Gdx.app.log("word.game", s1 + ", "+s2+", "+s3+", "+s4);
    }


    public static void log(int s){
        Gdx.app.log("word.game", s+"");
    }

    public static void log(int s1, int s2){
        Gdx.app.log("word.game", s1 + ", "+s2);
    }

    public static void log(int s1, int s2, int s3){
        Gdx.app.log("word.game", s1 + ", "+s2+", "+s3);
    }


    public static void log(int s1, int s2, int s3, int s4){
        Gdx.app.log("word.game", s1 + ", "+s2+", "+s3+", "+s4);
    }



    public static void log(Object s){
        Gdx.app.log("word.game", s+"");
    }



    public static void log(boolean b){
        Gdx.app.log("word.game", b+"");
    }
}

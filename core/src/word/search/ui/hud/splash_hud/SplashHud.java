package word.search.ui.hud.splash_hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import word.search.screens.SplashScreen;
import word.search.ui.game.ProgressBar;
import word.search.ui.dialogs.LanguageDialog;
import word.search.ui.hud.Hud;


public class SplashHud extends Hud {

    SplashScreen splashScreen;
    public Texture progressbar_track, progressbar;
    public ProgressBar pbar;

    public SplashHud(SplashScreen splashScreen){
        super(splashScreen);
        this.splashScreen = splashScreen;
        setUI();
    }


    @Override
    public void resize() {
        super.resize();

        if(pbar != null) pbar.setX((splashScreen.stage.getWidth() - pbar.getWidth()) * 0.5f);
    }



    protected void setUI(){
        progressbar_track = new Texture(Gdx.files.internal("textures/progressbar_track.png"));
        progressbar = new Texture(Gdx.files.internal("textures/progressbar.png"));

        pbar = new ProgressBar(progressbar_track, progressbar);
        pbar.setY(500);
        splashScreen.stage.addActor(pbar);
    }




   public void showLanguageDialog(Runnable callback){
       LanguageDialog languageDialog = new LanguageDialog(splashScreen, callback);
       splashScreen.stage.addActor(languageDialog);
       languageDialog.show();
   }


}

package word.search;

import android.content.Context;

import com.badlogic.gdx.Gdx;

import net.codecanyon.trimax.android.wordsearchinnovation.R;

import word.search.platform.MenuConfig;

public class MenuConfigAndroid implements MenuConfig {

    private Context context;

    public MenuConfigAndroid(Context context){
        this.context = context;
    }


    @Override
    public boolean rateUsEnabled() {
        return context.getResources().getBoolean(R.bool.RATE_US_ENABLED);
    }




    @Override
    public boolean showPrivacyDialogOnFirstRun() {
        return context.getResources().getBoolean(R.bool.SHOW_PRIVACY_DIALOG_ON_FIRST_RUN);
    }




    @Override
    public boolean showPrivacyDialogInSettingsDialog() {
        return context.getResources().getBoolean(R.bool.SHOW_PRIVACY_DIALOG_IN_SETTINGS_DIALOG);
    }




    @Override
    public boolean termsOfUseLinkAvailable() {
        Gdx.app.log("gg", context.getResources().getBoolean(R.bool.TERMS_OF_USE_LINK_AVAILABLE)+"");
        return context.getResources().getBoolean(R.bool.TERMS_OF_USE_LINK_AVAILABLE);
    }



    @Override
    public boolean appShareEnabled() {
        return context.getResources().getBoolean(R.bool.APP_SHARE_ENABLED);
    }



    @Override
    public boolean emailSupportEnabled() {
        return context.getResources().getBoolean(R.bool.EMAIL_SUPPORT_ENABLED);
    }
}

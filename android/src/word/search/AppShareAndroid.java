package word.search;

import android.content.Context;
import android.content.Intent;

import word.search.platform.AppShare;

public class AppShareAndroid implements AppShare {

    private Context context;

    public AppShareAndroid(Context context){
        this.context = context;
    }

    @Override
    public void share(String param) {
        String url = context.getPackageName();
        String text = String.format(param, url);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}

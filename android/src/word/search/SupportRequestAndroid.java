package word.search;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;

import net.codecanyon.trimax.android.wordsearchinnovation.R;

import java.util.Locale;

import word.search.platform.SupportRequest;

public class SupportRequestAndroid implements SupportRequest {

    private Context context;


    public SupportRequestAndroid(Context context){
        this.context = context;
    }


    @Override
    public void sendSupportEmail(String subject, String title){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, getDeviceInfo());
        context.startActivity(Intent.createChooser(intent, title));
    }



    public String getDeviceInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("Please type your request above");
        sb.append("\n");
        sb.append("Brand: ");
        sb.append(Build.BRAND);
        sb.append("\n");
        sb.append("Model: ");
        sb.append(Build.MODEL);
        sb.append("\n");
        sb.append("SDK: ");
        sb.append(Build.VERSION.SDK_INT);
        sb.append("\n");

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            locale = Resources.getSystem().getConfiguration().locale;
        }

        sb.append("Locale: ");
        sb.append(locale.getLanguage() + "-" + locale.getCountry());
        sb.append("\n");
        sb.append("App version: ");

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            sb.append(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return sb.toString();
    }
}

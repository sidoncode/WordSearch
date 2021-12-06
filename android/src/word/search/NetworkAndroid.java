package word.search;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import word.search.platform.Network;


public class NetworkAndroid implements Network {
    private Context context;

    public NetworkAndroid(Context context){
        this.context = context;
    }


    @Override
    public boolean isConnected() {
        return checkInternetConnection();
    }


    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network net = null;
            if (connectivityManager == null) {
                return false;
            } else {
                net = connectivityManager.getActiveNetwork();
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(net);
                if (networkCapabilities == null) {
                    return false;
                }
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager == null) {
                return false;
            }
            if (connectivityManager.getActiveNetworkInfo() == null) {
                return false;
            }
            return connectivityManager.getActiveNetworkInfo().isConnected();
        }
        return false;
    }

}

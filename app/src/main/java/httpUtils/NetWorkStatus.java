package httpUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zhangguoyu on 2015/4/3.
 */
public class NetWorkStatus {

    /**Determine whether the network is available*/
    public static boolean networkIsAvailable(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        if (info.isConnected()) {
            return true;
        }
        return false;
    }
    /**Determine whether the wifi is available*/
    public static boolean isWifiAvailable(Context context) {
        boolean isWifiAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            isWifiAvailable = true;
        }
        return isWifiAvailable;
    }

}

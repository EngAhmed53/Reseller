package com.shouman.apps.reseller.admin.network;

//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkCapabilities;
//import android.net.NetworkInfo;
//import android.os.Build;
//import android.util.Log;
//
//public class NetworkUtils {
//    private static final String TAG = "NetworkUtils";
//    public static Boolean isConnectedToInternet(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            if (cm != null) {
//                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
//                if (capabilities != null) {
//                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                        Log.e(TAG, "isConnectedToInternet: wifi avialble" );
//                        return true;
//                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                        Log.e(TAG, "isConnectedToInternet: cellaur avialble" );
//                        return true;
//                    }
//                }
//            }
//        } else {
//            if (cm != null) {
//                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//                return activeNetwork != null &&
//                        activeNetwork.isConnectedOrConnecting();
//            }
//        }
//        return false;
//    }
//}
